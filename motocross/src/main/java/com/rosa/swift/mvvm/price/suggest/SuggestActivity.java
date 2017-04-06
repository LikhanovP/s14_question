package com.rosa.swift.mvvm.price.suggest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.common.TplstCollection;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.data.dto.geo.GeoObjectCollection;
import com.rosa.swift.core.network.requests.structure.PlantsRequest;
import com.rosa.swift.core.network.responses.price.GeoObjectsGeocoderResponse;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.LogonlessActivity;
import com.rosa.swift.core.ui.adapters.ItemsAdapter;
import com.rosa.swift.core.ui.adapters.TplstAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestActivity extends LogonlessActivity implements ListView.OnItemClickListener {

    private DataRepository mDataRepository;

    private CardView mPlantsCardView;
    private EditText mAddressEditText;
    private AppCompatImageButton mClearAddressButton;
    private AppCompatImageButton mApplyAddressButton;
    private AppCompatImageButton mShowPlantsButton;
    private ListView mAddressListView;

    private TextWatcher mWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(final Editable editable) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mAddress = null;
            mGeoCode = charSequence.toString();
            mHandler.removeCallbacks(runRequestGeoObjects);
            hideSimpleProgress();
            if (mGeoObjectResCall != null) {
                mGeoObjectResCall.cancel();
            }
            if (!TextUtils.isEmpty(mGeoCode)) {
                mHandler.postDelayed(runRequestGeoObjects,
                        Constants.TIME_DELAY_TO_REQUEST_GEOCODER);
            } else {
                mAddress = null;
            }
        }
    };

    private ItemsAdapter<GeoObject> mAddressAdapters;
    private Handler mHandler;
    private String mGeoCode;

    private GeoObject mAddress;

    private Call<GeoObjectsGeocoderResponse> mGeoObjectResCall;

    public static Intent newIntent(Context packageContext, GeoObject geoObject, boolean isStartAddress) {
        Intent intent = new Intent(packageContext, SuggestActivity.class);
        intent.putExtra(Constants.EXTRA_SUGGEST_IS_START, isStartAddress);
        intent.putExtra(Constants.EXTRA_SUGGEST_GEO_OBJECT, geoObject);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        mDataRepository = DataRepository.getInstance();

        mPlantsCardView = (CardView) findViewById(R.id.werks_card_view);
        mAddressEditText = (EditText) findViewById(R.id.address_et);
        mClearAddressButton = (AppCompatImageButton) findViewById(R.id.clear_address_image_button);
        mApplyAddressButton = (AppCompatImageButton) findViewById(R.id.apply_address_image_button);
        mShowPlantsButton = (AppCompatImageButton) findViewById(R.id.pick_werk_image_button);
        mAddressListView = (ListView) findViewById(R.id.address_list_view);

        mHandler = new Handler();
        mAddressListView.setOnItemClickListener(this);

        boolean showWerkCard = getIntent().getBooleanExtra(
                Constants.EXTRA_SUGGEST_IS_START, false);
        if (showWerkCard) {
            mPlantsCardView.setVisibility(View.VISIBLE);
        }
        setAddress((GeoObject) getIntent().getSerializableExtra(
                Constants.EXTRA_SUGGEST_GEO_OBJECT));


        mAddressEditText.addTextChangedListener(mWatcher);

        mClearAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddressEditText.getText().clear();
            }
        });

        mApplyAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSimpleProgress();
                mHandler.removeCallbacks(runRequestGeoObjects);
                if (mGeoObjectResCall != null) {
                    mGeoObjectResCall.cancel();
                }
                sendResult(RESULT_OK, mAddress);
            }
        });

        mShowPlantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPlants();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
        if (mAddressAdapters != null && mAddressAdapters.getCount() > 0) {
            setAddress(mAddressAdapters.getItem(position));
        }
    }

    private void sendResult(int resultCode, GeoObject point) {
        Intent intent = new Intent(this, SuggestActivity.class);
        intent.putExtra(Constants.EXTRA_SUGGEST_POINT, point);
        setResult(resultCode, intent);
        finish();
    }

    private void setAddress(GeoObject geoObject) {
        if (geoObject != null) {
            setAddress(geoObject.getAddress());
        }
        mAddress = geoObject;
    }

    private void setAddress(String address) {
        mAddressEditText.removeTextChangedListener(mWatcher);
        mAddressEditText.setText(address);
        mAddressEditText.setSelection(mAddressEditText.getText().length());
        mAddressEditText.addTextChangedListener(mWatcher);
    }

    private void focusOnAddressInput() {
        mAddressEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mAddressEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showPlantList(final List<Tplst> plants) {
        if (plants != null && plants.size() > 0) {
            TplstAdapter adapter = new TplstAdapter(this, plants);
            CommonUtils.showItemsDialog(this, R.string.suggest_dialog_plant_list, adapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int itemIndex) {
                            GeoObject geoObject = plants.get(itemIndex);
                            if (geoObject.isCorrect()) {
                                sendResult(RESULT_OK, geoObject);
                            } else {
                                CommonUtils.ShowErrorMessage(SuggestActivity.this,
                                        R.string.suggest_plant_error_msg);
                            }
                        }
                    });
        }
    }

    private void requestPlants() {
        //TODO: ipopov 25.03.2017 метод повторяется в MainActivity, вынести в один
        final String townCode = mDataRepository.getSelectedTownCode();
        if (!TextUtils.isEmpty(townCode)) {
            TplstCollection loadedPlants = mDataRepository.getPlants();
            if (loadedPlants != null && loadedPlants.size() > 0 &&
                    loadedPlants.getMainCode().equals(townCode)) {
                showPlantList(loadedPlants);
            } else {
                showSimpleProgress();
                PlantsRequest request = new PlantsRequest(townCode);
                SapRequestUtils.getPlants(request, new ServiceCallback() {
                    @Override
                    public void onEndedRequest() {
                        hideSimpleProgress();
                    }

                    @Override
                    public void onFinished(String jsonParam) {
                        TplstCollection plants = new TplstCollection(jsonParam, townCode);
                        mDataRepository.replacePlants(plants);
                        if (plants.size() > 0) {
                            showPlantList(plants);
                        } else {
                            CommonUtils.ShowErrorMessage(SuggestActivity.this,
                                    R.string.suggest_get_plant_list_error_msg);
                        }
                    }

                    @Override
                    public void onFinishedWithException(WSException ex) {
                        hideSimpleProgress();
                        CommonUtils.ShowErrorMessage(SuggestActivity.this,
                                god_mode ? ex.getFullMessage() :
                                        getString(R.string.suggest_get_plant_list_error_msg));
                    }

                    @Override
                    public void onCancelled() {
                        hideSimpleProgress();
                    }
                });
            }
        }
    }

    private void requestGeoObjects() {
        try {
            lockUserInterface();

            Tplst tplst = mDataRepository.getSelectedTown();
            String position = tplst != null ? tplst.getCoords() : null;

            mGeoObjectResCall = mDataRepository.getGeoObjects(mGeoCode, position);
            mGeoObjectResCall.enqueue(new Callback<GeoObjectsGeocoderResponse>() {
                @Override
                public void onResponse(Call<GeoObjectsGeocoderResponse> call, Response<GeoObjectsGeocoderResponse> response) {
                    if (response != null && response.body() != null &&
                            response.body().getResponse() != null && response.code() == 200) {
                        GeoObjectCollection collection = new GeoObjectCollection(response.body().getResponse()
                                .getGeoObjectCollection());
                        mAddress = collection.getFirst();
                        mAddressAdapters = new ItemsAdapter<>(SuggestActivity.this, collection);
                        mAddressListView.setAdapter(mAddressAdapters);
                    } else {
                        showErrorMessage(R.string.suggest_request_addresses_error_msg);
                    }
                    unlockUserInterface();
                }

                @Override
                public void onFailure(Call<GeoObjectsGeocoderResponse> call, Throwable t) {
                    showErrorMessage(R.string.suggest_request_addresses_error_msg);
                    unlockUserInterface();
                }
            });
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
            unlockUserInterface();
        }
    }

    private void lockUserInterface() {
        showSimpleProgress();
        mAddressEditText.setEnabled(false);
        mApplyAddressButton.setEnabled(false);
    }

    private void unlockUserInterface() {
        hideSimpleProgress();
        mAddressEditText.setEnabled(true);
        mApplyAddressButton.setEnabled(true);
    }

    private Runnable runRequestGeoObjects = new Runnable() {
        @Override
        public void run() {
            requestGeoObjects();
            focusOnAddressInput();
        }
    };


}
