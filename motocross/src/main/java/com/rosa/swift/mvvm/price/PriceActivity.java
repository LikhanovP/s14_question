package com.rosa.swift.mvvm.price;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.motocross.databinding.ActivityPriceBinding;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.data.dto.geo.GeoObjectCollection;
import com.rosa.swift.core.data.dto.routing.RoutePath;
import com.rosa.swift.core.data.dto.routing.RoutePrice;
import com.rosa.swift.core.data.dto.transports.TransportType;
import com.rosa.swift.core.data.dto.transports.TransportTypeCollection;
import com.rosa.swift.core.network.requests.price.PriceRouteRequest;
import com.rosa.swift.core.network.responses.price.GeoObjectsMapResponse;
import com.rosa.swift.core.network.responses.price.PriceTransportsResponse;
import com.rosa.swift.core.network.responses.price.RoutePathResponse;
import com.rosa.swift.core.network.responses.price.RoutePriceResponse;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.adapters.ItemsAdapter;
import com.rosa.swift.core.ui.views.YandexMapView;
import com.rosa.swift.mvvm.price.suggest.SuggestActivity;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class PriceActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,
        View.OnClickListener, YandexMapView.MapCallback {

    private final int ROUTE_START = 1;
    private final int ROUTE_END = 2;

    private ActivityPriceBinding mPriceBinding;
    private PriceViewModel mPriceViewModel;

    private TransportTypeCollection mPriceTransports;

    private boolean mIsAppBarExpanded = false;
    /**
     * Значение, показывающее, ожидается ли добавление метки на карте
     */
    private boolean mIsWaitingForPicking = false;

    /**
     * Номер текущей точки
     */
    private int mCurrentPoint;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPriceViewModel = new PriceViewModel(this);
        mPriceBinding = DataBindingUtil.setContentView(this, R.layout.activity_price);
        mPriceBinding.setViewModel(mPriceViewModel);

        mPriceBinding.appbarLayout.addOnOffsetChangedListener(this);
        mPriceBinding.startPositionButton.setOnClickListener(this);
        mPriceBinding.endPositionButton.setOnClickListener(this);
        mPriceBinding.mapView.setMapCallback(this);
        mPriceBinding.mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addPoint();
                return false;
            }
        });

        mHandler = new Handler();

        setSupportActionBar(mPriceBinding.toolbar);

        loadMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.price_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_show_price:
                mPriceBinding.appbarLayout.setExpanded(true, true);
                break;
            case R.id.menu_item_refresh:
                loadMap();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mIsAppBarExpanded = verticalOffset + appBarLayout.getHeight() -
                mPriceBinding.toolbar.getHeight() > 0;
        if (mIsAppBarExpanded) {
            mPriceBinding.toolbar.setVisibility(View.GONE);
        } else {
            mPriceBinding.toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        PanelState state = mPriceBinding.slidingLayout.getPanelState();
        if (mIsAppBarExpanded || state == PanelState.EXPANDED || state == PanelState.ANCHORED) {
            collapsePanels();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_SUGGEST_ITEM:
                    GeoObject point = data.getParcelableExtra(Constants.EXTRA_SUGGEST_POINT);
                    if (point == null) {
                        showErrorMessage(R.string.suggest_request_first_address_error_msg);
                    }
                    showPoint(point);
                    mCurrentPoint = 0;
                    mHandler.removeCallbacksAndMessages(null);
                    break;
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.price_start_pos_et:
            case R.id.price_end_pos_et:
                //временное решение. когда адресов будет больше 2х, то будет все через RecyclerView
                mCurrentPoint = view.getId() == R.id.price_start_pos_et ? ROUTE_START : ROUTE_END;
                GeoObject geoObject = mCurrentPoint == ROUTE_START ?
                        mPriceViewModel.getStartAddress() : mPriceViewModel.getEndAddress();
                boolean isStart = mCurrentPoint == ROUTE_START;
                Intent intent = SuggestActivity.newIntent(this, geoObject, isStart);
                startActivityForResult(intent, Constants.REQUEST_SUGGEST_ITEM);
                break;
            case R.id.start_position_button:
                startWaitingForClicking(ROUTE_START);
                break;
            case R.id.end_position_button:
                startWaitingForClicking(ROUTE_END);
                break;
            case R.id.price_transport_et:
                requestTypesTransport();
                break;
            case R.id.price_get_btn:
                requestPriceRoute();
                break;
        }
    }

    private void loadMap() {
        try {
            startLoading(15000, R.string.price_map_load_error_msg);
            mPriceBinding.slidingLayout.setPanelState(PanelState.HIDDEN);
            mPriceBinding.mapView.loadMap();
        } catch (Exception ex) {
            Log.e(ex);
            hideSimpleProgress();
            showErrorMessage(R.string.price_map_load_error_msg);
        }
    }

    private void showPoint(GeoObject geoObject) {
        if (geoObject != null && mCurrentPoint > 0) {
            switch (mCurrentPoint) {
                case ROUTE_START:
                    mPriceViewModel.setStartAddress(geoObject);
                    break;
                case ROUTE_END:
                    mPriceViewModel.setEndAddress(geoObject);
                    break;
                default:
                    return;
            }

            mPriceBinding.mapView.addPoint(mCurrentPoint, geoObject);
        }
    }

    private void showTypeTransports() {
        if (mPriceTransports != null) {
            final ItemsAdapter<TransportType> adapter = new ItemsAdapter<>(this,
                    mPriceTransports);
            if (adapter.getCount() > 0) {
                CommonUtils.showItemsDialog(this, R.string.price_dialog_title_transports,
                        adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int itemIndex) {
                                mPriceViewModel.setTransport(adapter.getItem(itemIndex));
                            }
                        });
            }
        }
    }

    private void addPoint() {
        if (mIsWaitingForPicking) {
            startLoading(20000, R.string.price_add_point_error_msg);
        }
    }

    private void startWaitingForClicking(int currentPoint) {
        CommonUtils.showToast(this, R.string.price_add_point_process_msg);
        mCurrentPoint = currentPoint;
        mIsWaitingForPicking = true;
        mPriceBinding.appbarLayout.setExpanded(false, true);
    }

    private void stopWaitingForClicking() {
        mCurrentPoint = 0;
        mIsWaitingForPicking = false;
        mPriceBinding.appbarLayout.setExpanded(true, true);
    }

    private void startLoading(final int timeout, final int stringId) {
        showSimpleProgress();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.removeCallbacksAndMessages(null);
                mPriceBinding.mapView.stopLoading();
                showErrorMessage(stringId);
                hideSimpleProgress();
            }
        }, timeout);
    }

    //region User interface

    private void collapsePanels() {
        PanelState state = mPriceBinding.slidingLayout.getPanelState();
        mPriceBinding.appbarLayout.setExpanded(false, true);
        if (state == PanelState.EXPANDED || state == PanelState.ANCHORED) {
            mPriceBinding.slidingLayout.setPanelState(PanelState.COLLAPSED);
        }
    }

    private void updateSlidePanelState() {
        mPriceBinding.slidingLayout.setPanelState(mPriceViewModel.isRouteInfoExist() ?
                PanelState.COLLAPSED : PanelState.HIDDEN);
    }

    public void showErrorMessage(int stringId) {
        showErrorMessage(getString(stringId));
    }

    public void showErrorMessage(String message) {
        CommonUtils.ShowErrorMessage(this, message);
    }

    public void showInfoMessage(String message) {
        CommonUtils.ShowInfoMessage(this, message);
    }

    public void showSimpleProgress() {
        mPriceBinding.progressBarLayout.setVisibility(View.VISIBLE);
    }

    public void hideSimpleProgress() {
        mPriceBinding.progressBarLayout.setVisibility(View.GONE);
    }

    //endregion

    //region Sap requests

    private void requestTypesTransport() {
        if (mPriceTransports != null && mPriceTransports.isLoaded()) {
            showTypeTransports();
        } else {
            try {
                String townCode = DataRepository.getInstance().getSelectedTownCode();
                if (TextUtils.isEmpty(townCode)) {
                    throw new Exception("Не задано место планирования транспорта");
                }
                showSimpleProgress();
                SapRequestUtils.getTypeTransports(townCode, new ServiceCallback() {
                    @Override
                    public void onEndedRequest() {
                        hideSimpleProgress();
                    }

                    @Override
                    public void onFinished(String evParams) {
                        try {
                            if (!TextUtils.isEmpty(evParams)) {
                                mPriceTransports = new TransportTypeCollection(new Gson()
                                        .fromJson(evParams, PriceTransportsResponse.class));
                                if (mPriceTransports.size() > 0) {
                                    mPriceTransports.setLoaded(true);
                                    showTypeTransports();
                                } else {
                                    showErrorMessage(R.string.price_get_type_transports_error_msg);
                                }
                            } else {
                                showErrorMessage(R.string.price_get_type_transports_error_msg);
                            }
                        } catch (Exception exception) {
                            showErrorMessage(R.string.price_get_type_transports_error_msg);
                            Log.e(exception);
                        }
                    }

                    @Override
                    public void onFinishedWithException(WSException ex) {
                        hideSimpleProgress();
                        showErrorMessage(R.string.price_get_type_transports_error_msg);
                        Log.e(ex);
                    }

                    @Override
                    public void onCancelled() {
                        hideSimpleProgress();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                hideSimpleProgress();
            }
        }
    }

    private void requestPriceRoute() {
        if (mPriceViewModel.isRequestPriceDataCorrect()) {
            startLoading(20000, R.string.price_add_route_error_msg);
            mPriceBinding.appbarLayout.setExpanded(false, true);
            mPriceBinding.slidingLayout.setPanelState(PanelState.HIDDEN);
            PriceRouteRequest priceRouteReq = new PriceRouteRequest(mPriceViewModel.getTransport().getCode(),
                    mPriceViewModel.getStartAddress(), mPriceViewModel.getEndAddress());

            showSimpleProgress();
            SapRequestUtils.getPriceRoute(priceRouteReq, new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    hideSimpleProgress();
                }

                @Override
                public void onFinished(String jsonString) {
                    try {
                        mPriceViewModel.setRoutePrice(new RoutePrice(new Gson()
                                .fromJson(jsonString, RoutePriceResponse.class)));
                        mPriceBinding.mapView.addRoute(mPriceViewModel.getGeoObjects());
                        updateSlidePanelState();
                    } catch (Exception ex) {
                        hideSimpleProgress();
                        showErrorMessage(R.string.price_get_price_route_error_msg);
                        Log.e(ex);
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    hideSimpleProgress();
                    showErrorMessage(R.string.price_get_price_route_error_msg);
                    Log.e(ex);
                }

                @Override
                public void onCancelled() {
                    hideSimpleProgress();
                }
            });
        }
    }

    //endregion

    //region YandexMapCallback implementation

    @Override
    @JavascriptInterface
    public void onLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHandler.removeCallbacksAndMessages(null);
                Tplst tplst = DataRepository.getInstance().getSelectedTown();
                if (tplst != null && !TextUtils.isEmpty(tplst.getCoords())) {
                    mPriceBinding.mapView.setCenterMap(tplst.getLongitude(), tplst.getLatitude());
                }
                hideSimpleProgress();
            }
        });
    }

    @Override
    @JavascriptInterface
    public void onClicked(final String jsonString, final double longitude, final double latitude) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mIsWaitingForPicking && mCurrentPoint > 0) {
                    try {
                        GeoObject geoObject = new GeoObjectCollection(new Gson()
                                .fromJson(jsonString, GeoObjectsMapResponse.class)
                                .getGeoObjectCollection())
                                .getFirst();

                        if (geoObject == null) {
                            showInfoMessage("Не удалось определить адрес точки");
                            geoObject = new GeoObject(longitude, latitude);
                        }

                        showPoint(geoObject);
                    } catch (Exception ex) {
                        Log.e(ex);
                        hideSimpleProgress();
                        showErrorMessage(R.string.price_add_point_error_msg);
                    }
                }
            }
        });
    }

    @Override
    @JavascriptInterface
    public void onRouteAdded(final String jsonString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mHandler.removeCallbacksAndMessages(null);
                    mPriceViewModel.setRoutePath(new RoutePath(new Gson()
                            .fromJson(jsonString, RoutePathResponse.class)));
                    updateSlidePanelState();
                    hideSimpleProgress();
                } catch (Exception ex) {
                    Log.e(ex);
                    hideSimpleProgress();
                    showErrorMessage(R.string.price_add_route_error_msg);
                }
            }
        });
    }

    @Override
    @JavascriptInterface
    public void onPointAdded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHandler.removeCallbacksAndMessages(null);
                stopWaitingForClicking();
                hideSimpleProgress();
            }
        });
    }

    @Override
    @JavascriptInterface
    public void onError(final String caption, final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(caption)) {
                    Log.e(error);
                    hideSimpleProgress();
                    showErrorMessage(caption);
                }
            }
        });
    }

    //endregion

}
