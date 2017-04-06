package com.rosa.swift.core.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.giljulio.imagepicker.ui.ImagePickerActivity;
import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.business.ChatMessageSentCallback;
import com.rosa.swift.core.business.DeliverySearch;
import com.rosa.swift.core.business.Message;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.DialogHandler;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.PermissionManager;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.business.utils.SupportUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.common.Tplst;
import com.rosa.swift.core.data.dto.common.TplstCollection;
import com.rosa.swift.core.data.dto.deliveries.TransportationType;
import com.rosa.swift.core.data.dto.deliveries.repositories.DeliveryTemplateDto;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.data.dto.geo.GeoObject;
import com.rosa.swift.core.network.json.sap.assign.JChangeOut;
import com.rosa.swift.core.network.json.sap.common.JMessage;
import com.rosa.swift.core.network.json.sap.common.JPosTerminalDataOut;
import com.rosa.swift.core.network.json.sap.cup.JCupSetInfoOut;
import com.rosa.swift.core.network.json.sap.driverRecords.JDriverRecords;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;
import com.rosa.swift.core.network.json.sap.swchat.JRoom;
import com.rosa.swift.core.network.json.sap.waisting.JWaistOut;
import com.rosa.swift.core.network.requests.chat.ChatMessageRequest;
import com.rosa.swift.core.network.requests.chat.RoomRequest;
import com.rosa.swift.core.network.requests.delivery.ChangeDeliveryRequest;
import com.rosa.swift.core.network.requests.function.CallRequest;
import com.rosa.swift.core.network.requests.function.GateRequest;
import com.rosa.swift.core.network.requests.function.WaistRequest;
import com.rosa.swift.core.network.requests.message.DriverMessageRequest;
import com.rosa.swift.core.network.requests.structure.PlantsRequest;
import com.rosa.swift.core.network.requests.terminal.PosTerminalDataRequest;
import com.rosa.swift.core.network.requests.terminal.RegisterPaymentRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.ServiceFunction;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.network.services.sap.ZMotoService;
import com.rosa.swift.core.services.LocationService;
import com.rosa.swift.core.terminal.Terminal;
import com.rosa.swift.core.ui.adapters.CurrentDeliveryAdapter;
import com.rosa.swift.core.ui.adapters.DeliveryAdapter;
import com.rosa.swift.core.ui.adapters.ItemsAdapter;
import com.rosa.swift.core.ui.fragments.CabinetFragment;
import com.rosa.swift.core.ui.fragments.CupSetFragment;
import com.rosa.swift.core.ui.fragments.DeliveryFragment;
import com.rosa.swift.core.ui.fragments.DeliveryListFragment;
import com.rosa.swift.core.ui.fragments.DriverRecordInfoFragment;
import com.rosa.swift.core.ui.fragments.FindDeliveryFragment;
import com.rosa.swift.core.ui.fragments.FragmentListener;
import com.rosa.swift.core.ui.fragments.IndicatorFragment;
import com.rosa.swift.core.ui.fragments.NavigationDrawerFragment;
import com.rosa.swift.mvp.ratings.documents.PhotoDocumentListFragment;
import com.rosa.swift.core.ui.fragments.SettingsFragment;
import com.rosa.swift.core.ui.fragments.SwiftRoomFragment;
import com.rosa.swift.core.ui.fragments.SwiftRoomListFragment;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;
import com.rosa.swift.mvp.assignments.base.repository.Dto.RejectionReasonDto;
import com.rosa.swift.mvp.history.DeliveriesHistoryFragment;
import com.rosa.swift.mvp.ratings.documents.PhotoDocumentActivity;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;
import com.rosa.swift.mvp.settings.SettingsDialog;
import com.rosa.swift.mvp.shift.quiz.repositories.QuestDto;
import com.rosa.swift.mvvm.price.PriceActivity;
import com.ru.ingenico.jarcus2.ResponseCode;
import com.ru.ingenico.jarcus2.exception.TerminalNotConnectedException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends RootActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentListener, DeliverySearch.SearchCallback {

    private static final int TAKE_A_PICTURE = 3;
    private static final int TAKE_FROM_GALLERY = 4;
    private static final int SHOW_PHOTO_DOC_ACTIVITY = 6;

    /**
     * Перечисляет состояние главного экрана приложения
     */
    public enum MainActivityState {
        Loading, //загрузка
        Offline, //не на смене
        Online, //на списке доступных доставок
        OnDeliveryList, //на списке назначенных доставок
        OnDelivery, //открыта конкретная доставка
        InCabinet
    }

    private FrameLayout dlvInWorkSchemaButton;
    private LinearLayout setHeaderView;
    private View currDlvListView;
    private View enterView;
    private View dlvView;
    private View headerView;
    private View listView;
    private View pcabLayout;
    private View swiftLayout;
    private TextView dlvInWorkHeader;
    private TextView dlvInWorkView;
    private TextView setNumberTextView;
    private TextView statusText;
    private TextView totalTextView;
    private ImageButton dlvInWorkGpsButton;
    private ImageView dlvInWorkSchemaImageView;
    private Button mShowTownsBtn;
    private ProgressBar dlvInWorkSchemaProgress;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private EditText mGateCodeEt;

    private DataRepository mDataRepository;

    private MainActivityState mState = MainActivityState.Offline;
    private MainActivityState mPreviousState = mState;

    private Fragment inCabinetFragmentDelayed;

    private Tplst mTplst;
    private TplstCollection mTownList;

    @NonNull
    private List<Delivery> mAssignedDeliveries = new ArrayList<>();
    private Delivery mCurrentDelivery;

    private List<Delivery> deliveryList;
    //список доступных для выбора доставок для отображения
    private List<Delivery> deliveryList4View;
    //список назначенных доставок для отображения
    private List<Delivery> currentDeliveryList4View;

    private ItemsAdapter<Tplst> mTplstAdapter;
    private DeliveryAdapter adapter;
    private CurrentDeliveryAdapter currAdapter;

    private Hashtable sections;

    private Time last_queue_num_request_time = null;

    private Terminal posTerminal;

    private String mTitle;
    private String photoFilePath = null;

    private boolean currInSet;
    private boolean gpsAvail;
    private boolean mManualSelect = false;

    private int current_fragment_code;

    //номер в очереди
    private int mQueueNum;

    //region LifeCircle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initCabinetDrawerSections();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataRepository = DataRepository.getInstance();

        TextView appVersionView = (TextView) findViewById(R.id.app_version_string);
        ListView deliveryListView = (ListView) findViewById(R.id.dlv_list_view);
        ListView currDeliveryListView = (ListView) findViewById(R.id.curr_dlv_list_view);

        mTownList = new TplstCollection();
        deliveryList = new ArrayList<>();
        deliveryList4View = new ArrayList<>();
        currentDeliveryList4View = new ArrayList<>();

        enterView = findViewById(R.id.enter_layout);
        listView = findViewById(R.id.dlv_list_layout);
        dlvView = findViewById(R.id.dlv_layout);
        currDlvListView = findViewById(R.id.curr_dlv_layout);
        swiftLayout = findViewById(R.id.swift_layout);
        pcabLayout = findViewById(R.id.container);
        headerView = findViewById(R.id.header_layout);
        dlvInWorkHeader = (TextView) findViewById(R.id.dlv_header_view);
        dlvInWorkView = (TextView) findViewById(R.id.dlv_text_view);
        dlvInWorkGpsButton = (ImageButton) findViewById(R.id.dlv_gps_location_button);
        dlvInWorkSchemaButton = (FrameLayout) findViewById(R.id.dlv_schema_location_button);
        dlvInWorkSchemaImageView = (ImageView) findViewById(R.id.dlv_schema_image);
        dlvInWorkSchemaProgress = (ProgressBar) findViewById(R.id.dlv_schema_progress);
        statusText = (TextView) findViewById(R.id.header_text_view);
        totalTextView = (TextView) findViewById(R.id.total_text_view);
        setNumberTextView = (TextView) findViewById(R.id.set_number_text_view);
        setHeaderView = (LinearLayout) findViewById(R.id.set_header_view);
        mShowTownsBtn = (Button) findViewById(R.id.townBtn);

        headerView.setOnClickListener(view -> onHeaderClick());

        appVersionView.setText(SwiftApplication.getVersionString());

        adapter = new DeliveryAdapter(this);
        deliveryListView.setAdapter(adapter);

        currAdapter = new CurrentDeliveryAdapter(this);
        currDeliveryListView.setAdapter(currAdapter);

        mShowTownsBtn.setOnClickListener(view -> showTownListRequest());

        mTplst = mDataRepository.getSelectedTown();
        mShowTownsBtn.setText(mTplst != null ? mTplst.getName() :
                getString(R.string.tplst_default_name));

        findViewById(R.id.shift_button).setOnClickListener(view -> goOnline());
        findViewById(R.id.settings_button).setOnClickListener(view -> showSettingsDialog());
        findViewById(R.id.price_button).setOnClickListener(view -> showPrice());
        findViewById(R.id.cabinet_button).setOnClickListener(view -> goCabinetOnline());

        findViewById(R.id.finishButton).setOnClickListener(view -> finishCurrentDelivery());
        findViewById(R.id.buttonCancel).setOnClickListener(view -> rejectCurrentDelivery());

        dlvInWorkGpsButton.setOnClickListener(view -> openGpsLocation());
        dlvInWorkSchemaButton.setOnClickListener(view -> openSchemaLocation());

        //чтобы линки телефонов были кликабельны
        dlvInWorkView.setMovementMethod(LinkMovementMethod.getInstance());

        suspended = false;

        initDrawer();

        switchStateTo(MainActivityState.Loading);

        try {
            gpsAvail = getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        } catch (Exception ignored) {
            gpsAvail = false;
        }

        if (!PermissionManager.isPermissionGranted(this)) {
            PermissionManager.requestPermissions(this);
        }

    }

    //endregion

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        MenuItem mItem = mNavigationDrawerFragment.getFilteredItem(position);
        switch (mItem.getItemId()) {
            case R.id.pcab_action_exit:
                goCabinetOffline();
                break;
            case R.id.pcab_action_delivery:
            case R.id.pcab_action_delivery_list:
            case R.id.pcab_action_swift_room:
            case R.id.pcab_action_swift_room_list:
            case R.id.pcab_action_find_delivery:
            case R.id.pcab_action_settings:
            case R.id.pcab_action_photo_base:
            case R.id.pcab_action_indicator:
                inCabinetOpenFragment(mItem.getItemId());
                break;
            default:
                onOptionsItemSelected(mItem);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_A_PICTURE:
                if (resultCode != 0) {
                    if ((photoFilePath != null) && (mCurrentDelivery != null)) {
                        sendIncident(mCurrentDelivery.getNumber(), photoFilePath);
                        photoFilePath = null;
                    }
                }
                break;
            case Constants.REQUEST_TAKE_PHOTO:
                if (resultCode != 0) {
                    if ((photoFilePath != null) && (mCurrentDelivery != null)) {
                        sendIncident(mCurrentDelivery.getNumber(), photoFilePath);
                        photoFilePath = null;
                    }
                }
                break;
            case TAKE_FROM_GALLERY:
                if (resultCode == RESULT_OK) {
                    onScansFromGalleryGet(data);
                }
                break;
            case Constants.REQUEST_CUP_PERFORMING:
                boolean isCupPerforming = false;
                if (data != null) {
                    isCupPerforming = data.getBooleanExtra(
                            CupRequestActivity.CUP_IS_PERFORMING, false);
                    if (isCupPerforming) {
                        startCupRequest();
                    }
                }
                break;
            case SHOW_PHOTO_DOC_ACTIVITY:
                startPhotoRequest();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_LOCATION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(LocationService.newIntent(this));
                } else {
                    PermissionManager.showApplicationSettingsDialog(this);
                }
                break;
            case Constants.REQUEST_PERMISSION_CAMERA_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getIncidentPhoto();
                } else {
                    showInfoMessage(R.string.error_message_permission_camera);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu != null) menu.clear();
        int res_id = -1;
        switch (mState) {
            case Offline:
                res_id = R.menu.state_0;
                navigationDrawerInflate();
                break;
            case Online:
                res_id = R.menu.state_1;
                navigationDrawerInflate(R.menu.navdraw_state_1);
                break;
            case OnDelivery:
                res_id = R.menu.state_2;
                navigationDrawerInflate(R.menu.navdraw_state_2);
                break;
            case OnDeliveryList:
                res_id = R.menu.state_3;
                navigationDrawerInflate(R.menu.navdraw_state_3);
                break;
        }
        if (res_id != -1) {
            getMenuInflater().inflate(res_id, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (mState) {
            case OnDelivery:
                if (menu != null &&
                        (
                                mCurrentDelivery == null ||
                                        (StringUtils.isNullOrEmpty(mCurrentDelivery.getFirstPhone()) && StringUtils.isNullOrEmpty(mCurrentDelivery.getSecondPhone()))
                        )
                        ) {
                    mNavigationDrawerFragment.removeItem(R.id.action_call_client);
                    menu.removeItem(R.id.action_call_client);
                }
                if (menu != null && mCurrentDelivery != null) {
                    MenuItem item = menu.findItem(R.id.action_waist_end);
                    if (item != null)
                        item.setVisible(mCurrentDelivery.isIdling() && gpsAvail);
                    item = menu.findItem(R.id.action_waist_start);
                    if (item != null)
                        item.setVisible(!mCurrentDelivery.isIdling() && gpsAvail);
                    if (mDataRepository.getDriverMessages().isEmpty()) {
                        menu.removeItem(R.id.action_send_message);
                    }
                    item = menu.findItem(R.id.action_terminal);
                    if (item != null)
                        item.setVisible(mCurrentDelivery.isPayOnPlace());
                }
                break;
            case OnDeliveryList:
                MenuItem item = menu.findItem(R.id.action_reject_set_delivery);
                if (item != null)
                    item.setVisible(currInSet);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_online:
                goOnline();
                break;
            case R.id.action_settings:
                showSettingsDialog();
                break;
            case R.id.action_go_offline:
                goOffline(true);
                break;
            case R.id.action_reject_delivery:
                rejectCurrentDelivery();
                break;
            case R.id.action_assignment_complete:
                finishCurrentDelivery();
                break;
            case R.id.action_reject_set_delivery:
                rejectCurrentSet();
                break;
            case R.id.action_filter_werks:
                showPlantListRequest();
                break;
            case R.id.action_call_client:
                callClient();
                break;
            case R.id.action_incident:
                if (!PermissionManager.isCameraPermissionGranted(MainActivity.this)) {
                    PermissionManager.requestCameraPermissions(MainActivity.this);
                } else {
                    getIncidentPhoto();
                }
                break;
            case R.id.action_assignment_print:
                printCurrentDelivery();
                break;
            case R.id.action_waist_start:
                waistCurrentDelivery(true);
                break;
            case R.id.action_waist_end:
                waistCurrentDelivery(false);
                break;
            case R.id.action_dlv_update:
                updateCurrentDelivery();
                break;
            case R.id.action_send_message:
                sendMessage();
                break;
            case R.id.action_scan:
                sendTTNScan();
                break;
            case R.id.action_pcab:
                goCabinetOnline();
                break;
            case R.id.action_price:
                showPrice();
                break;
            case R.id.action_open_gate:
                showOpenGateDialog();
                break;
            case R.id.action_relocations:
                showRelocations();
                break;
            case R.id.action_terminal:
                showTerminalMenu();
                break;
            case R.id.action_documents:
                showDeliveryDocsList(mCurrentDelivery.getNumber());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer(Gravity.START);
        } else {
            if (mState == MainActivityState.OnDelivery && mAssignedDeliveries.size() != 0) {
                mCurrentDelivery = null;
                mManualSelect = false;
                switchStateTo(MainActivityState.OnDeliveryList);
            } else if (mState == MainActivityState.InCabinet) {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    super.onBackPressed();
                } else {
                    Fragment fragment = fm.findFragmentById(R.id.container);
                    if (fragment != null) {
                        fm.beginTransaction().remove(fragment).commit();
                    }
                    setUpCabinetDrawerAndBar();
                }
            }
        }
    }

    @Override
    public void onDeliveryMenuItemSelected(Delivery d, MenuItem item) {
        mCurrentDelivery = d;
        onOptionsItemSelected(item);
    }

    @Override
    public void onChatRoomSelect(JRoom room) {
        SwiftRoomFragment sFragment = SwiftRoomFragment.getInstance(room);
        inCabinetOpenFragment(sFragment);
    }

    @Override
    public void onSearchCompleted(DeliverySearch.SearchResult result, DeliverySearch.SearchOptions options) {
        hideProgress();
        hideKeyboard();
        if (result == null) return;
        if (result.isEmpty()) {
            Toast.makeText(this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
        } else {
            int code = R.id.pcab_action_empty;
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
            Iterator<Map.Entry> it = sections.entrySet().iterator();
            Map.Entry entry;
            while (it.hasNext()) {
                entry = it.next();
                if (entry.getValue() == current) {
                    code = (int) entry.getKey();
                    break;
                }
            }
            switch (code) {
                case R.id.pcab_action_delivery_list:
                    ((DeliveryListFragment) current).setSearchResult(result);
                    break;
                case R.id.pcab_action_find_delivery:
                    DeliveryListFragment fragment = DeliveryListFragment.newInstance(1);
                    fragment.setSearchOptions(options);
                    fragment.setSearchResult(result);
                    inCabinetOpenFragment(fragment);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSearchCompletedError(Exception e) {
        hideProgress();
        CommonUtils.ShowErrorMessage(this, e.getMessage());
    }

    @Override
    public void onFragmentStart(int code) {
        current_fragment_code = code;
        Fragment cFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        mTitle = ((CabinetFragment) cFragment).getTitle();
        switch (code) {
            case R.id.pcab_action_delivery_list:
                if (cFragment != null) {
                    try {
                        DeliveryListFragment f = (DeliveryListFragment) cFragment;
                        if (f.getMode() == 0)
                            f.setSearchOptions(new DeliverySearch.SearchOptions(
                                    mDataRepository.getDriverCall().toUpperCase()));
                    } catch (Exception e) {
                        Log.e(e.getMessage());
                    }
                }
                break;
        }
        restoreActionBar();
    }

    @Override
    public void onDeliverySelect(Delivery delivery) {
        Fragment dFragment = DeliveryFragment.getInstance(delivery);
        inCabinetOpenFragment(dFragment);
        mCurrentDelivery = delivery;
    }

    @Override
    public void onFindClick(DeliverySearch.SearchOptions options) {
        options.driver = mDataRepository.getDriverCall().toUpperCase();
        doFind(options);
    }

    @Override
    public void onCupSelected(JCupSetInfoOut.JCupSetInfo cupSetInfo) {
        CupSetFragment cupSetFragment = CupSetFragment.getInstance(cupSetInfo);
        inCabinetOpenFragment(cupSetFragment);
    }

    @Override
    public void onDriverRecordsSelected(JDriverRecords.JDriverRecord driverRecordsInfo) {
        DriverRecordInfoFragment recordInfoFragment = DriverRecordInfoFragment.getInstance(driverRecordsInfo);
        inCabinetOpenFragment(recordInfoFragment);
    }

    @Override
    protected void onServiceBinded() {
        super.onServiceBinded();
        switchState();
    }

    protected void onStuffLoaded() {
        //TODO: ipopov 29.03.2017 восстановить работу с задачами

        //Опрос для опциональных вопросов
        startSurveyForOptionalQuests();

        //Документы
        //startPhotoRequest();

        //ЦУП
        //startCupRequest(false);
    }

    private void rejectDelivery(final Delivery rejectingDelivery, RejectionReasonDto reason) {
        if (rejectingDelivery == null) return;
        suspend();
        showProgress(getString(R.string.unassign_delivery_progress_message));
        ChangeDeliveryRequest request = new ChangeDeliveryRequest(rejectingDelivery.inSet() ?
                rejectingDelivery.getSetNumber() : rejectingDelivery.getNumber(), reason.getCode());
        SapRequestUtils.rejectDelivery(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onCancelled() {
                hideProgress();
                unsuspend();
            }

            @Override
            public void onFinished(String evParams) {
                try {
                    JChangeOut jout = new Gson().fromJson(evParams, JChangeOut.class);
                    if (StringUtils.isNullOrEmpty(jout.getErrorMessage()))
                        rejectDelivery(rejectingDelivery);
                    else
                        CommonUtils.ShowErrorMessage(MainActivity.this, jout.getErrorMessage());
                } catch (Exception e) {
                    CommonUtils.ShowErrorMessage(MainActivity.this, R.string.unassign_delivery_error);
                    if (god_mode)
                        CommonUtils.ShowErrorMessage(MainActivity.this, e.getMessage());
                    Log.e(e.getMessage());
                } finally {
                    unsuspend();
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                unsuspend();
                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
            }
        });
    }

    private void rejectDelivery(Delivery rejectedDelivery) {
        mManualSelect = false;
        Toast.makeText(this, R.string.unassign_delivery_success, Toast.LENGTH_SHORT).show();
        List<Delivery> removeDeliveryList = new ArrayList<>();
        removeDeliveryList.add(rejectedDelivery);
        //удаляем весь комплект
        for (Delivery currentDelivery : mAssignedDeliveries) {
            if (currentDelivery.inSameSet(rejectedDelivery)) {
                removeDeliveryList.add(currentDelivery);
            }
        }
        for (Delivery removingDelivery : removeDeliveryList) {
            mDataRepository.removeDelivery(removingDelivery.getNumber());
        }
        mAssignedDeliveries = mDataRepository.getDeliveries();
        if (mAssignedDeliveries.size() != 0) {
            mCurrentDelivery = null;
            switchStateTo(MainActivityState.OnDeliveryList);
        } else
            startListeningFromTheBeginning();
    }

    //отказ от текущей транспортировки
    private void rejectCurrentDelivery() {
        final Delivery rejectingDelivery = mCurrentDelivery;
        if (rejectingDelivery != null) {
            CommonUtils.confirm(MainActivity.this, R.string.yes_no_confirm,
                    rejectingDelivery.inSet() ? R.string.reject_delivery_set_confirm :
                            R.string.reject_delivery_confirm, new DialogHandler() {
                        @Override
                        public void YesClick() {
                            //rejectDelivery(reject);
                            ArrayAdapter<RejectionReasonDto> adapter = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.select_dialog_singlechoice,
                                    mDataRepository.getRejectionReasons());

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.title_activity_reasons)
                                    .setSingleChoiceItems(adapter, 0, (dialogInterface, i) -> {
                                        dialogInterface.cancel();
                                        rejectDelivery(rejectingDelivery, adapter.getItem(i));
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }

                        @Override
                        public void NoClick() {
                        }
                    });
        }
    }

    public void assignDelivery(String assigningDeliveryNumber) {
        for (Delivery assigningDelivery : deliveryList)
            if (assigningDelivery.getNumber().equals(assigningDeliveryNumber)) {
                final Delivery foundedDelivery = assigningDelivery;
                suspend();
                showProgress(getString(R.string.assign_delivery_progress_message));
                String deliveryNumber = foundedDelivery.inSet() ?
                        foundedDelivery.getSetNumber() : foundedDelivery.getNumber();
                ChangeDeliveryRequest request = new ChangeDeliveryRequest(deliveryNumber);
                SapRequestUtils.assignDelivery(request, new ServiceCallback() {
                    @Override
                    public void onEndedRequest() {
                        hideProgress();
                    }

                    @Override
                    public void onCancelled() {
                        hideProgress();
                        unsuspend();
                    }

                    @Override
                    public void onFinished(String evParams) {
                        try {
                            JChangeOut jout = new Gson().fromJson(evParams, JChangeOut.class);
                            if (!StringUtils.isNullOrEmpty(jout.getErrorMessage())) {
                                CommonUtils.ShowErrorMessage(MainActivity.this, jout.getErrorMessage());
                            } else {
                                takeDelivery(foundedDelivery);
                            }
                        } catch (Exception e) {
                            CommonUtils.ShowErrorMessage(MainActivity.this, R.string.assign_delivery_error);
                            if (god_mode)
                                CommonUtils.ShowErrorMessage(MainActivity.this, e.getMessage());
                            Log.e(e.getMessage());
                        } finally {
                            unsuspend();
                        }
                    }

                    @Override
                    public void onFinishedWithException(WSException ex) {
                        unsuspend();
                        CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                        if (god_mode)
                            CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                    }
                });
                return;
            }
    }

    //снятие водителя с доставки
    private void unassignDelivery(String deliveryNumber) {
        boolean isDeliveryExist = false;
        for (Delivery currentDelivery : mAssignedDeliveries) {
            if (currentDelivery.getNumber().equals(deliveryNumber)) {
                isDeliveryExist = true;
                break;
            }
        }

        //доставка есть в списке назанченных доставок
        if (isDeliveryExist) {
            CommonUtils.ShowInfoMessage(this, R.string.unassign_delivery_message);
            mDataRepository.removeDelivery(deliveryNumber);
            mAssignedDeliveries = mDataRepository.getDeliveries();
            if (mState != MainActivityState.InCabinet) {
                if (mAssignedDeliveries.size() != 0) {
                    if (mState == MainActivityState.OnDeliveryList)
                        updateCurrentDeliveryListView();
                    else {
                        if (mState == MainActivityState.OnDelivery && mCurrentDelivery != null &&
                                mCurrentDelivery.getNumber().equals(deliveryNumber)) {
                            switchStateTo(MainActivityState.OnDeliveryList);
                        }
                    }
                } else {
                    startListeningFromTheBeginning();
                }
            }
        }
    }

    public void displayDelivery(final String deliveryNumber) {
        if (!TextUtils.isEmpty(deliveryNumber)) {
            Delivery foundDelivery = null;
            for (Delivery delivery : deliveryList) {
                if (delivery.getNumber().equals(deliveryNumber)) {
                    foundDelivery = delivery;
                    break;
                }
            }
            if (foundDelivery != null) {
                String deliveryInfo = mDataRepository.getDeliveryDataHtml(
                        TemplateType.DMC, foundDelivery);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog dialog = builder.setTitle(R.string.title_activity_delivery_dialog)
                        .setView(R.layout.dialog_take_delivery)
                        .setCancelable(false)
                        .show();
                TextView deliveryText = (TextView) dialog.findViewById(R.id.delivery_info);
                Button acceptBtn = (Button) dialog.findViewById(R.id.take_delivery);
                Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_delivery);

                if (deliveryText != null) {
                    deliveryText.setText(Html.fromHtml(deliveryInfo));
                }
                if (acceptBtn != null) {
                    acceptBtn.setOnClickListener(view -> {
                        assignDelivery(deliveryNumber);
                        dialog.cancel();
                    });
                }
                if (cancelBtn != null) {
                    cancelBtn.setOnClickListener(view -> dialog.cancel());
                }
            }
        }
    }

    private void addDeliveryInList(Delivery jd) {
        if (!deliveryList.contains(jd)) {
            deliveryList.add(jd);
            updateDeliveryListView();
            //vibrate();
        } else {
            for (Delivery d : deliveryList)
                if (d.equals(jd)) {
                    if (d.update(jd))
                        updateDeliveryListView();
                    break;
                }
        }
    }

    private void takeDelivery(Delivery takenDelivery) {
        Toast.makeText(this, R.string.assign_delivery_success, Toast.LENGTH_SHORT).show();
        mDataRepository.addOrUpdateDelivery(takenDelivery);
        mAssignedDeliveries = mDataRepository.getDeliveries();

        //одиночная доставка - переход к экрану доставки
        if (!takenDelivery.inSet()) {
            mManualSelect = true;
            mCurrentDelivery = takenDelivery;
            deliveryList.clear();
            switchStateTo(MainActivityState.OnDelivery);
            //взяли комплект - покажем окно со всеми назначенными доставками
        } else {
            //если комплект - добавляем все доставки из комплекта на экран назначенных доставок
            for (Delivery delivery : deliveryList) {
                if (delivery.inSameSet(takenDelivery) && !mAssignedDeliveries.contains(delivery)) {
                    mDataRepository.addOrUpdateDelivery(delivery);
                }
            }
            mAssignedDeliveries = mDataRepository.getDeliveries();
            deliveryList.clear();
            switchStateTo(MainActivityState.OnDeliveryList);
        }
    }

    private boolean showDelivery(Delivery delivery) {
        TplstCollection plants = mDataRepository.getPlants();
        if (plants == null) return true;
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).getCode().equals(delivery.getPlantCode()))
                return plants.get(i).isSelected();
        }
        return true;
    }

    public void selectDeliveryByNumber(String deliveryNumber) {
        for (Delivery currentDelivery : mAssignedDeliveries) {
            if (currentDelivery.getNumber().equals(deliveryNumber)) {
                mManualSelect = true;
                selectDelivery(currentDelivery);
                break;
            }
        }
    }

    //выбор доставки в списке назанченных доставок
    private void selectDelivery(Delivery jd) {
        mCurrentDelivery = jd;
        switchStateTo(MainActivityState.OnDelivery);
    }

    //region Delivery
    //сообщение DLV+ добавление доставки в спискок доступных доставок
    private void addDeliveryFromString(String message) {
        Delivery jd = new Delivery(message);
        addDeliveryInList(jd);
    }

    //сообщение DLV- удаление доставки из списка доступных доставок
    private void removeDeliveryFromString(String message) {
        Delivery _jd = null;
        for (Delivery jd : deliveryList) {
            if (jd.getNumber().equals(message)) {
                _jd = jd;
                break;
            }
        }
        if (_jd != null) {
            deliveryList.remove(_jd);
            updateDeliveryListView();
        }
    }

    //Обработка сообщений вида DLV~ и DLV_
    private void assignDeliveryFromString(String message, boolean showMessage) {
        //если доставка уже есть в списке
        Delivery assignedDelivery = new Delivery(message);
        for (Delivery currentDelivery : mAssignedDeliveries) {
            if (currentDelivery.equals(assignedDelivery)) {
                mDataRepository.addOrUpdateDelivery(assignedDelivery);
                mAssignedDeliveries = mDataRepository.getDeliveries();
                updateCurrentDeliveryListView();
                if (mCurrentDelivery != null && currentDelivery.equals(mCurrentDelivery)) {
                    updateDeliveryView();
                }
                return;
            }
        }
        //ручное назначение или первоначальное подключение
        if (showMessage) {
            CommonUtils.ShowInfoMessage(this, R.string.assigned_delivery_message);
        }
        mDataRepository.addOrUpdateDelivery(assignedDelivery);
        mAssignedDeliveries = mDataRepository.getDeliveries();
        if (mState != MainActivityState.InCabinet) {
            //уже не первая доставка в списке
            if (mAssignedDeliveries.size() != 1) {
                if (mState != MainActivityState.OnDelivery || !mManualSelect) {
                    //переключаемся на список назначенных доставок
                    switchStateTo(MainActivityState.OnDeliveryList);
                }
            } else {
                selectDelivery(assignedDelivery);
            }
        }
    }
    //endregion

    //region Relocations


    //ручное назначение или первоначальное подключение
    private void assignRelocationFromString(String message, boolean showMessage) {
        //TODO: ipopov 20.02.2017 сделать уведомления о текущих изменениях
        if (!TextUtils.isEmpty(message)) {
            Delivery delivery = new Delivery(message, TransportationType.Relocation);
            mDataRepository.addOrUpdateDelivery(delivery);
            if (showMessage) {
                CommonUtils.ShowInfoMessage(this, R.string.relocation_assigned_message);
            }
        }
    }

    //снятие водителя с доставки
    private void unassignRelocationByNumber(String number) {
        //TODO: ipopov 20.02.2017 сделать уведомления о текущих изменениях
        if (!TextUtils.isEmpty(number)) {
            mDataRepository.removeRelocation(number);
            CommonUtils.ShowInfoMessage(this, R.string.relocation_unassigned_message);
        }
    }

    private void updateRelocationFromString(String message) {
        //TODO: ipopov 20.02.2017 обновлять перемещение на экране
        if (!TextUtils.isEmpty(message)) {
            Delivery delivery = new Delivery(message, TransportationType.Relocation);
            mDataRepository.addOrUpdateDelivery(delivery);
        }
    }

    //endregion

    @SuppressWarnings("MissingPermission")
    private void waistCurrentDelivery(final boolean waist) {
        final Delivery finish = mCurrentDelivery;
        Location loc = null;
        try {
            if (PermissionManager.isLocationPermissionGranted(this)) {
                loc = ((LocationManager) (this.getSystemService(LOCATION_SERVICE)))
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ignored) {
        }
        if (loc == null && waist) {
            if (!isGPSEnabled()) {
                CommonUtils.ShowInfoMessage(this, R.string.gps_disabled_message);
                showGpsOptions();
            } else {
                CommonUtils.ShowInfoMessage(this, R.string.no_location_message);
            }
        } else {
            Time t = new Time();
            if (loc != null) {
                t.set(loc.getTime());
                Time now = new Time();
                now.setToNow();
                if ((now.toMillis(false) - t.toMillis(false)) > 10 * 60 * 1000) {
                    if (!isGPSEnabled()) {
                        CommonUtils.ShowInfoMessage(this, R.string.gps_disabled_message);
                        showGpsOptions();
                    } else {
                        CommonUtils.ShowInfoMessage(this, R.string.no_location_message);
                    }
                    return;
                }
            }
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            DecimalFormat df = (DecimalFormat) nf;
            df.setMaximumFractionDigits(3);
            final String location = loc == null ? "" : String.format("%s;%s,%s", t.format2445(), df.format(loc.getLatitude()), df.format(loc.getLongitude()));
            if (finish != null) {
                CommonUtils.confirm(MainActivity.this, R.string.yes_no_confirm, waist ? R.string.start_waisting_confirm : R.string.end_waisting_confirm, new DialogHandler() {
                    @Override
                    public void YesClick() {
                        doWaisting(finish, location, waist);
                    }

                    @Override
                    public void NoClick() {

                    }
                });
            }
        }
    }

    public List<Delivery> getDeliveryListForView() {
        return deliveryList4View;
    }

    public List<Delivery> getCurrentDeliveryListForView() {
        return currentDeliveryList4View;
    }

    private void initCabinetDrawerSections() {
        sections = new Hashtable();
        //Доставки за сегодня
        sections.put(R.id.pcab_action_delivery_list, DeliveriesHistoryFragment.newInstance());
        //Поиск доставки
        sections.put(R.id.pcab_action_find_delivery, FindDeliveryFragment.newInstance());
        //Сообщения
        sections.put(R.id.pcab_action_swift_room_list, SwiftRoomListFragment.newInstance());
        //Настройки
        sections.put(R.id.pcab_action_settings, SettingsFragment.newInstance());
        //Личные данные
        sections.put(R.id.pcab_action_photo_base, PhotoDocumentListFragment.newInstance());
        //Показатели
        sections.put(R.id.pcab_action_indicator, IndicatorFragment.newInstance());
    }

    private void initDrawer() {

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.relocation_drawer));

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    CabinetFragment pcFragment = ((CabinetFragment) getSupportFragmentManager().findFragmentById(R.id.container));
                    if (pcFragment != null) {
                        mTitle = pcFragment.getTitle();
                        setNavigationDrawerEnabled(pcFragment.getDrawerEnabled());
                        restoreActionBar();
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage(), e);
                }
            }
        });
        getMenuInflater().inflate(R.menu.navdraw_pcab_menu, mNavigationDrawerFragment);
        mNavigationDrawerFragment.setDrawerEnabled(false);
    }

    public void inCabinetOpenFragment(int fragmentId) {
        inCabinetOpenFragment(fragmentId, false);
    }

    public void inCabinetOpenFragment(int fragmentId, boolean delayed) {
        if (mState != MainActivityState.InCabinet && !delayed) return;
        Fragment fragmentNew = (Fragment) sections.get(fragmentId);
        inCabinetOpenFragment(fragmentNew, delayed);
    }

    public void inCabinetOpenFragment(Fragment fragmentNew) {
        inCabinetOpenFragment(fragmentNew, false);
    }

    public void inCabinetOpenFragment(Fragment fragmentNew, boolean delayed) {
        if (delayed) {
            inCabinetFragmentDelayed = fragmentNew;
            return;
        }
        if (mState != MainActivityState.InCabinet) return;
        if (fragmentNew == null) return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragmentPrev = fragmentManager.findFragmentById(R.id.container);
        if (((CabinetFragment) fragmentNew).getBackStackName() != null) {
            fragmentManager.popBackStackImmediate(((CabinetFragment) fragmentNew).getBackStackName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragmentNew, ((CabinetFragment) fragmentNew).getPCabTag());
        if (fragmentPrev != null)
            fragmentTransaction.addToBackStack(((CabinetFragment) fragmentNew).getBackStackName());
        fragmentTransaction.commit();

        mTitle = ((CabinetFragment) fragmentNew).getTitle();
        restoreActionBar();

        ((CabinetFragment) fragmentNew).refreshData();
    }

    private void onHeaderClick() {
        if (mState == MainActivityState.Online) {
            boolean make_request = false;
            if (last_queue_num_request_time == null) {
                make_request = true;
                last_queue_num_request_time = new Time();
            } else {
                Time now = new Time();
                now.setToNow();
                if ((now.toMillis(false) - last_queue_num_request_time.toMillis(false)) > 20000)
                    make_request = true;
            }
            if (make_request) {
                last_queue_num_request_time.setToNow();
                SapRequestUtils.getQueueNum(new ServiceCallback() {
                    @Override
                    public void onEndedRequest() {
                    }

                    @Override
                    public void onFinished(String evParams) {
                        updateQueueNum(evParams);
                    }

                    @Override
                    public void onFinishedWithException(WSException ex) {
                    }

                    @Override
                    public void onCancelled() {
                    }
                });
            }
        } else {
            if (++god_mod_cnt > 5) {
                god_mod_cnt = 0;
                if (!god_mode) {
                    god_mode = true;
                    Toast.makeText(this, "Режим отображения ошибок: вкл", Toast.LENGTH_SHORT).show();
                } else {
                    god_mode = false;
                    Toast.makeText(this, "Режим отображения ошибок: выкл", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //список городов
    private void showTownListRequest() {
        showProgress(R.string.town_list_load);
        SapRequestUtils.getTowns(new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onFinished(String jsonParam) {
                mTownList = new TplstCollection(jsonParam);
                if (mTownList.size() > 0) {
                    showTownList();
                } else {
                    Toast.makeText(MainActivity.this, R.string.town_list_load_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                Toast.makeText(MainActivity.this, R.string.town_list_load_failed, Toast.LENGTH_SHORT).show();
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
            }

            @Override
            public void onCancelled() {
                hideProgress();
            }

        });
    }

    //список заводов
    private void showPlantListRequest() {
        //TODO: ipopov 25.03.2017 метод повторяется в SuggestActivity, вынести в один
        final String townCode = mDataRepository.getSelectedTownCode();
        if (!TextUtils.isEmpty(townCode)) {
            TplstCollection loadedPlants = mDataRepository.getPlants();
            if (loadedPlants != null && loadedPlants.size() > 0 &&
                    loadedPlants.getMainCode().equals(townCode)) {
                showPlantList();
            } else {
                showProgress(R.string.plant_list_load);
                PlantsRequest request = new PlantsRequest(townCode);
                SapRequestUtils.getPlants(request, new ServiceCallback() {
                    @Override
                    public void onEndedRequest() {
                        hideProgress();
                    }

                    @Override
                    public void onFinished(String jsonParam) {
                        TplstCollection plants = new TplstCollection(jsonParam, townCode);
                        mDataRepository.replacePlants(plants);
                        if (plants.size() > 0) {
                            showPlantList();
                        } else {
                            CommonUtils.ShowErrorMessage(MainActivity.this,
                                    R.string.suggest_get_plant_list_error_msg);
                        }
                    }

                    @Override
                    public void onFinishedWithException(WSException ex) {
                        Toast.makeText(MainActivity.this, R.string.plant_list_load_failed,
                                Toast.LENGTH_SHORT).show();
                        if (god_mode)
                            CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                    }

                    @Override
                    public void onCancelled() {
                        hideProgress();
                    }
                });
            }
        }
    }

    private void showPlantList() {
        TplstCollection plants = mDataRepository.getPlants();
        if (plants != null) {
            List<String> names = plants.getNames();
            CharSequence[] tplstName = names.toArray(new CharSequence[names.size()]);
            final boolean[] checkedItems = new boolean[names.size()];

            for (int i = 0; i < plants.size(); i++) {
                checkedItems[i] = plants.get(i).isSelected();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                    R.style.AppTheme_NoActionBar_FitsSystemWindows));
            builder.setTitle(R.string.title_activity_werks)
                    .setCancelable(true)
                    .setOnCancelListener(dialogInterface -> updateDeliveryListView())
                    .setMultiChoiceItems(tplstName, checkedItems,
                            (dialog, i, checked) -> {
                                plants.get(i).setSelected(checked);
                                checkedItems[i] = checked;
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mDataRepository.updatePlantsSelectedFlag(plants);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void startGalleryActivity() {
        Intent photoPickerIntent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(photoPickerIntent, TAKE_FROM_GALLERY);
    }

    //открыть окно настроек
    private void showSettingsDialog() {
        SettingsDialog dialog = new SettingsDialog();
        dialog.init(getMvpDelegate());
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    //показать экран прайса
    private void showPrice() {
        if (!isTplstExist()) return;
        Intent intent = new Intent(this, PriceActivity.class);
        startActivity(intent);
    }

    private void showRelocations() {
        Intent intent = new Intent(this, RelocationActivity.class);
        startActivity(intent);
    }

    private void setUpCabinetDrawerAndBar() {
        mNavigationDrawerFragment.openDrawer(Gravity.START);
        mTitle = getString(R.string.title_activity_pcab);
        restoreActionBar();
    }

    //region Terminal

    private void doPosTerminalOperation(final int operation) {
        if (mCurrentDelivery == null) return;
        final Delivery delivery = mCurrentDelivery;
        showProgress("Проверка параметров терминала");
        PosTerminalDataRequest request = new PosTerminalDataRequest(delivery.getNumber());
        SapRequestUtils.getPosTerminalData(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onFinished(String evParams) {
                //
                try {
                    Gson g = new Gson();
                    JPosTerminalDataOut posData = g.fromJson(evParams, JPosTerminalDataOut.class);
                    if (StringUtils.isNullOrEmpty(posData.deviceAddress)) {
                        CommonUtils.ShowInfoMessage(MainActivity.this, "Вам не выдан платежный терминал! Обратитесь в ЛТС");
                    } else {
                        if ((operation == 1) && (StringUtils.isNullOrEmpty(posData.amount) || "0.00".equals(posData.amount))) {
                            CommonUtils.ShowInfoMessage(MainActivity.this, "Сумма безналичной оплаты = 0 руб! Оплата по терминалу не требуется.");
                        } else
                            doPosTerminalOperation(posData.deviceAddress, delivery, posData.amount, operation);
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage(), e);
                    CommonUtils.ShowErrorMessage(MainActivity.this, "Не удалось получить параметры терминала");
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private void doPosTerminalOperation(final String deviceAddress, final Delivery delivery, final String amount, final int operation) {
        if (mCurrentDelivery == null || (mCurrentDelivery != delivery)) return;
        if (posTerminal != null) posTerminal.disconnect();
        posTerminal = new Terminal(this, deviceAddress);
        Log.d(String.format("POSTerminal %s Operation %s Delivery %s Amount %s", deviceAddress, operation, delivery.getNumber(), amount));
        Terminal.POSOperationListener listener = new Terminal.POSOperationListener() {
            @Override
            public void onOperationComplete(ResponseCode lastRc) {
                posTerminal.disconnect();
                posTerminal = null;
                hideProgress();
                if (lastRc.originalResponseCode() <= 10) { //success
                    switch (operation) {
                        case 0:
                            CommonUtils.ShowInfoMessage(MainActivity.this, "Тест связи завершен успешно. Терминал готов к работе.");
                            break;
                        case 1:
                            //CommonUtils.ShowInfoMessage(MainActivity.this, "Оплата успешно проведена. Дождитесь регистрации оплаты в системе.");
                            startPaymentRegistration(deviceAddress, delivery, amount, lastRc);
                            break;
                    }
                } else { //fail
                    switch (operation) {
                        case 0:
                            CommonUtils.ShowErrorMessage(MainActivity.this, "Нет связи с банком. Проверьте наличие интернет соединения!");
                            break;
                        case 1:
                            CommonUtils.ShowErrorMessage(MainActivity.this, "Оплата НЕ ПРОИЗВЕДЕНА");
                            break;
                    }
                }
                unsuspend();
            }

            @Override
            public void onOperationBegin() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        suspend();
                        showProgress("Опреация выполняется. Следуйте инструкциям на терминале!");
                    }
                });
            }

            @Override
            public void onOperationCompleteWithException(final Exception e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        if (posTerminal != null)
                            posTerminal.disconnect();
                        posTerminal = null;
                        Log.e(e.getMessage(), e);
                        try {
                            throw e;
                        } catch (TerminalNotConnectedException ee) {
                            CommonUtils.ShowErrorMessage(MainActivity.this, "Терминал не подключен!");
                        } catch (Terminal.DeviceNotFoundException ee) {
                            CommonUtils.ShowErrorMessage(MainActivity.this, "Устройство не найдено!");
                        } catch (Exception ee) {
                            CommonUtils.ShowErrorMessage(MainActivity.this, "Операцию выполнить не удалось!");
                        }
                        unsuspend();
                    }
                });
            }

            @Override
            public void onStoreRC(ResponseCode rc) {
                //do nothing
            }
        };
        suspend();
        showProgress("Опреация выполняется. Следуйте инструкциям на терминале!");
        switch (operation) {
            case 0:
                posTerminal.testDeviceConnection(listener);
                break;
            case 1:
                posTerminal.pay(amount, listener);
        }
    }

    private void startPaymentRegistration(final String deviceAddress, final Delivery delivery, final String amount, final ResponseCode lastRc) {
        showProgress("Оплата успешно проведена. Регистрируем оплату в системе");
        RegisterPaymentRequest request = new RegisterPaymentRequest(delivery.getNumber(),
                deviceAddress, amount, lastRc.originalResponseCode());
        SapRequestUtils.registerPayment(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onFinished(String evParams) {
                boolean success = "X".equals(evParams);
                if (success) {
                    CommonUtils.ShowInfoMessage(MainActivity.this, "Оплата успешно зарегистрирована");
                } else {
                    retryPaymentRegistration(deviceAddress, delivery, amount, lastRc);
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                //retryPaymentRegistration(deviceAddress, delivery, amount, lastRc);
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private void retryPaymentRegistration(final String deviceAddress, final Delivery delivery, final String amount, final ResponseCode lastRc) {
        CommonUtils.ShowInfoMessageWithButtons(this, R.string.retry_payment_title, "Поторить регистрацию оплаты? ", R.string.retry, R.string.no_payreg, new DialogHandler() {
            @Override
            public void YesClick() {
                startPaymentRegistration(deviceAddress, delivery, amount, lastRc);
            }

            @Override
            public void NoClick() {

            }
        });
    }

    //endregion

    //region Вынесено в AssignmentPresenter

    //region FinishDelivery

    //завершение текущей транспортировки
    private void finishCurrentDelivery() {
        final Delivery finish = mCurrentDelivery;
        if (finish != null) {
            CommonUtils.confirm(MainActivity.this,
                    R.string.yes_no_confirm,
                    R.string.finish_delivery_confirm,
                    new DialogHandler() {
                        @Override
                        public void YesClick() {
                            finishDelivery(finish);
                        }

                        @Override
                        public void NoClick() {

                        }
                    });
        }
    }

    private void finishDelivery(final Delivery finishingDelivery) {
        if (finishingDelivery == null) return;
        showProgress(getString(R.string.finish_delivery_progress_message));
        ChangeDeliveryRequest request = new ChangeDeliveryRequest(finishingDelivery.getNumber());
        SapRequestUtils.finishDelivery(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onCancelled() {
                hideProgress();
                unsuspend();
            }

            @Override
            public void onFinished(String evParams) {
                try {
                    JChangeOut jout = new Gson().fromJson(evParams, JChangeOut.class);
                    if (StringUtils.isNullOrEmpty(jout.getErrorMessage()))
                        onDeliveryFinished(finishingDelivery);
                    else
                        CommonUtils.ShowErrorMessage(MainActivity.this, jout.getErrorMessage());
                } catch (Exception e) {
                    CommonUtils.ShowErrorMessage(MainActivity.this, R.string.finish_delivery_error);
                    if (god_mode)
                        CommonUtils.ShowErrorMessage(MainActivity.this, e.getMessage());
                    Log.e(e.getMessage());
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
            }
        });
    }

    private void onDeliveryFinished(Delivery finishedDelivery) {
        mManualSelect = false;
        Toast.makeText(this, R.string.finish_delivery_success, Toast.LENGTH_SHORT).show();
        mDataRepository.removeDelivery(finishedDelivery.getNumber());
        mAssignedDeliveries = mDataRepository.getDeliveries();
        //обязательный вопрос - задаём при завершении доставки
        startSurveyForRequiredQuests(finishedDelivery);
        if (mAssignedDeliveries.size() != 0) {
            mCurrentDelivery = null;
            switchStateTo(MainActivityState.OnDeliveryList);
        } else
            startListeningFromTheBeginning();
    }

    //endregion

    //region PrintDelivery

    private void printCurrentDelivery() {
        final Delivery dlv = mCurrentDelivery;
        if (dlv != null) {
            CommonUtils.confirm(MainActivity.this, R.string.yes_no_confirm, R.string.print_delivery_confirm, new DialogHandler() {
                @Override
                public void YesClick() {
                    printDelivery(dlv);
                }

                @Override
                public void NoClick() {

                }
            });
        }
    }

    private void printDelivery(final Delivery delivery) {
        if (delivery == null) return;
        showProgress(getString(R.string.print_delivery_progress));
        ChangeDeliveryRequest request = new ChangeDeliveryRequest(delivery.getNumber());
        SapRequestUtils.printDelivery(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onFinished(String evParams) {
                if (!StringUtils.isNullOrEmpty(evParams))
                    CommonUtils.ShowInfoMessage(MainActivity.this, evParams);
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
            }

            @Override
            public void onCancelled() {
                hideProgress();
            }
        });
    }

    //endregion

    //region SendMessage

    public void sendMessage() {
        if (mCurrentDelivery == null) return;
        final String tknum = mCurrentDelivery.getNumber();
        sendMessageForDelivery(tknum);
    }

    private void sendMessageForDelivery(final String deliveryNumber) {
        List<DriverMessageDto> driverMessages = mDataRepository.getDriverMessages();
        String[] items = new String[driverMessages.size()];
        for (int i = 0; i < driverMessages.size(); i++) {
            items[i] = driverMessages.get(i).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.title_activity_driver_message)
                .setItems(items, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    sendDriverMessage(driverMessages.get(i).getType(), deliveryNumber);
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sendDriverMessage(final String messageId, final String deliveryNumber) {
        final EditText input = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.dialog_send_comment_title)
                .setView(input)
                .setPositiveButton(R.string.dialog_send_comment_apply, (dialogInterface, i) -> {
                    String comment = input.getText() != null ? input.getText().toString() : "";
                    showProgress(getString(R.string.send_data_progress));
                    DriverMessageRequest request = new DriverMessageRequest(messageId, comment, deliveryNumber);
                    SapRequestUtils.sendDriverMessage(request, new ServiceCallback() {
                        @Override
                        public void onEndedRequest() {
                            hideProgress();
                        }

                        @Override
                        public void onCancelled() {
                            hideProgress();
                        }

                        @Override
                        public void onFinished(String evParams) {
                            if (!evParams.equals("X"))
                                CommonUtils.ShowErrorMessage(MainActivity.this, "Не удалось отправить сообщение!");
                            else
                                Toast.makeText(MainActivity.this, "Отправлено успешно", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinishedWithException(WSException ex) {
                            CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                            if (god_mode)
                                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                        }
                    });
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //endregion

    //region updateDelivery

    private void updateCurrentDelivery() {
        updateDelivery(mCurrentDelivery);
    }

    private void updateDelivery(final Delivery delivery) {
        if (delivery != null) {
            showProgress(getString(R.string.update_delivery_progress_message));
            ChangeDeliveryRequest request = new ChangeDeliveryRequest(delivery.getNumber());
            SapRequestUtils.updateDelivery(request, new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    hideProgress();
                }

                @Override
                public void onCancelled() {
                    hideProgress();
                    unsuspend();
                }

                @Override
                public void onFinished(String evParams) {
                    try {
                        JChangeOut changeRes = new Gson().fromJson(evParams, JChangeOut.class);
                        List<JMessage> messages = changeRes.getMessages();
                        if (messages != null && messages.size() > 0) {
                            for (JMessage jm : messages) {
                                processMessage(new Message(jm));
                            }
                        }
                    } catch (Exception e) {
                        if (god_mode)
                            CommonUtils.ShowErrorMessage(MainActivity.this, e.getMessage());
                        Log.e(e.getMessage());
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                    if (god_mode)
                        CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                }
            });
        }
    }

    private void updateDeliveryFromString(String message) {
        Delivery updatedDelivery = new Delivery(message);
        for (Delivery delivery : deliveryList) {
            if (delivery.equals(updatedDelivery)) {
                if (delivery.update(updatedDelivery)) {
                    updateDeliveryListView();
                }
                return;
            }
        }
        for (Delivery currentDelivery : mAssignedDeliveries) {
            if (currentDelivery.equals(updatedDelivery)) {
                mDataRepository.addOrUpdateDelivery(updatedDelivery);
                mAssignedDeliveries = mDataRepository.getDeliveries();
                updateCurrentDeliveryListView();
                if (mCurrentDelivery != null && currentDelivery.equals(mCurrentDelivery)) {
                    mCurrentDelivery.update(currentDelivery);
                    updateDeliveryView();
                }
                break;
            }
        }
    }

    //endregion

    //region OpenGate

    private void showOpenGateDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = dialogBuilder.setTitle(R.string.dialog_open_gate_title)
                .setView(R.layout.dialog_open_gate)
                .setPositiveButton(R.string.dialog_open_gate_apply, (dialog, which) -> onOpenGateClick())
                .setNegativeButton(R.string.open_gate_dialog_cancel, null)
                .show();

        mGateCodeEt = (EditText) alertDialog.findViewById(R.id.gate_code_et);
    }

    private void onOpenGateClick() {
        if (mDataRepository.isGateKeeperEnabled()) {
            String gateNumber = mGateCodeEt.getText().toString();

            if (!TextUtils.isEmpty(gateNumber)) {
                try {
                    int gateNum = Integer.parseInt(gateNumber);
                    if (gateNum > 0) {
                        GateRequest request = new GateRequest(String.valueOf(gateNum));
                        SapRequestUtils.openGate(request, new ServiceCallback() {
                            @Override
                            public void onEndedRequest() {
                                hideProgress();
                            }

                            @Override
                            public void onFinished(String evParams) {
                                hideProgress();
                                if (!evParams.equals(Constants.SAP_TRUE_FLAG))
                                    showErrorMessage(getString(R.string.open_gate_message_unsuccessful));
                                else {
                                    showInfoMessage(getString(R.string.open_gate_message_successful));
                                }
                            }

                            @Override
                            public void onFinishedWithException(WSException ex) {
                                showErrorMessage(ex.getMessage());
                            }

                            @Override
                            public void onCancelled() {
                                hideProgress();
                            }
                        });
                    }
                } catch (Exception ignored) {
                    hideProgress();
                    showErrorMessage(getString(R.string.open_gate_message_error));
                }
            } else {
                showErrorMessage(getString(R.string.open_gate_message_set_gate_number));
            }
        }
    }

    private void setOpenGateVisibility() {
        MenuItem gateOpenItem = mNavigationDrawerFragment.findItem(R.id.action_open_gate);
        if (gateOpenItem != null) {
            gateOpenItem.setVisible(mDataRepository.isGateKeeperEnabled());
        }
    }

    //endregion

    //endregion

    private void callClient() {
        if (TextUtils.isEmpty(mDataRepository.getDriverPhone())) {
            CommonUtils.ShowErrorMessage(this, R.string.no_phone_error);
            return;
        }
        if (mCurrentDelivery != null) {
            String firstPhoto = mCurrentDelivery.getFirstPhone();
            String secondPhoto = mCurrentDelivery.getSecondPhone();
            if (!TextUtils.isEmpty(firstPhoto) && !TextUtils.isEmpty(secondPhoto)) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.select_dialog_singlechoice);
                if (!TextUtils.isEmpty(firstPhoto)) {
                    adapter.add("Тел1.: " + firstPhoto);
                }
                if (!TextUtils.isEmpty(secondPhoto)) {
                    adapter.add("Тел2.: " + secondPhoto);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.title_activity_phone_select)
                        .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                switch (i) {
                                    case 0:
                                        doCall("CLNT");
                                        break;
                                    case 1:
                                        doCall("CLNT2");
                                        break;
                                }
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if (!TextUtils.isEmpty(firstPhoto)) {
                doCall("CLNT");
            } else if (!TextUtils.isEmpty(secondPhoto)) {
                doCall("CLNT2");
            }

        }
    }

    private void doCall(String whom) {
        if (mCurrentDelivery != null) {
            CallRequest request = new CallRequest(mDataRepository.getDriverCall(), whom,
                    mDataRepository.getDriverPhone(), mCurrentDelivery.getNumber());
            SapRequestUtils.requestCall(request);
            Toast.makeText(this, R.string.call_requested_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showTownList() {
        if (mTownList != null && mTownList.size() > 0) {
            mTplstAdapter = new ItemsAdapter<>(this, mTownList);
            CommonUtils.showItemsDialog(this, R.string.main_menu_town_list_dialog_title,
                    mTplstAdapter, (dialog, itemIndex) -> {
                        mTplst = mTplstAdapter.getItem(itemIndex);
                        mShowTownsBtn.setText(mTplst.getName());
                        mDataRepository.setSelectedTown(mTplst);
                    });
        }
    }

    private void rejectCurrentSet() {
        if (currInSet) {
            final Delivery rejectingDelivery = mAssignedDeliveries.get(0);
            if (rejectingDelivery != null) {
                CommonUtils.confirm(MainActivity.this,
                        R.string.yes_no_confirm,
                        R.string.reject_set_delivery_confirm,
                        new DialogHandler() {
                            @Override
                            public void YesClick() {
                                ArrayAdapter<RejectionReasonDto> adapter = new ArrayAdapter<>(MainActivity.this,
                                        android.R.layout.select_dialog_singlechoice,
                                        mDataRepository.getRejectionReasons());
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(R.string.title_activity_reasons)
                                        .setSingleChoiceItems(adapter, 0, (dialogInterface, i) -> {
                                            dialogInterface.cancel();
                                            rejectDelivery(rejectingDelivery, adapter.getItem(i));
                                        });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }

                            @Override
                            public void NoClick() {
                            }
                        });
            }
        }
    }

    private boolean isTplstExist() {
        boolean isExist = true;
        if (TextUtils.isEmpty(mDataRepository.getSelectedTownCode())) {
            CommonUtils.ShowErrorMessage(this, R.string.tplst_empty_error);
            isExist = false;
        }
        return isExist;
    }

    private void clearFragments() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = fm.getBackStackEntryAt(0);
                fm.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            Fragment f = fm.findFragmentById(R.id.container);
            if (f != null)
                fm.beginTransaction().remove(f).commit();
            mNavigationDrawerFragment.reset();
        } catch (Exception ex) {
            Log.e(ex.getMessage(), ex);
        }
    }

    private Completable endCurrentSession() {
        return Completable.fromAction(() -> mQueueService.endCurrentSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showProgress(R.string.go_cabinet_offline_progress))
                .doOnError(throwable -> showErrorMessage(throwable.getMessage()))
                .doOnTerminate(this::hideProgress);
    }

    //region Online

    private void goOnline() {
        if (mQueueService == null) {
            CommonUtils.ShowErrorMessage(this, R.string.app_service_stopped);
            return;
        }

        if (StringUtils.isNullOrEmpty(mDataRepository.getDriverCall())) {
            CommonUtils.ShowErrorMessage(this, R.string.empty_settings_error);
            return;
        }

        if (!isTplstExist()) return;

        showProgress(getString(R.string.go_online_progress_message));
        //выполняем логин на сервер
        new LoginTask().execute();
    }

    private void goOnlineSucceeded() {
        mCurrentDelivery = null;
        switchStateTo(MainActivityState.Online);
    }

    private void goOnlineFailed(WSException wsException) {
        switch (wsException.getType()) {
            case SessionError:
                CommonUtils.ShowErrorMessage(this, wsException.getFullMessage());
                break;
            default:
                CommonUtils.ShowErrorMessage(this, wsException.getMessage());
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, wsException.getFullMessage());
                break;
        }
    }

    private void goOffline(boolean withConfirm) {
        if (withConfirm) {
            CommonUtils.confirm(MainActivity.this,
                    R.string.go_offline_confirm,
                    R.string.yes_no_confirm, new DialogHandler() {
                        @Override
                        public void YesClick() {
                            goOffline(false);
                        }

                        @Override
                        public void NoClick() {

                        }
                    });
        } else {
            //TODO: ipopov 21.03.2017 проверять на неотвеченные вопросы
            //if (GlobalContext.getInstance().hasUnansweredReqQuest()) {
            //    Toast.makeText(this, "Сначала ответьте на вопросы", Toast.LENGTH_SHORT).show();
            //    return;
            //}
            if (mDataRepository.getSessionId() != null) {
                showProgress(getString(R.string.go_offline_progress_message));
                new LogoutTask(MainActivity.this).execute();
            }
        }
    }

    private void goOfflineSucceeded() {
        if (mState == MainActivityState.InCabinet) {
            clearFragments();
        }
        switchStateTo(MainActivityState.Offline);
        mDataRepository.setDefaultQueueNumber();
    }

    private void goOfflineFailed(Context context, WSException e) {
        CommonUtils.ShowErrorMessage(context, e.getMessage());
        if (god_mode)
            CommonUtils.ShowErrorMessage(context, e.getFullMessage());
        CommonUtils.confirm(context,
                R.string.go_offline_text,
                R.string.go_offline_inspiteof,
                new DialogHandler() {
                    @Override
                    public void YesClick() {
                        if (mQueueService != null)
                            mQueueService.endCurrentSessionOffline();
                        goOfflineSucceeded();
                    }

                    @Override
                    public void NoClick() {

                    }
                });
    }

    //endregion

    //region Cabinet

    private void goCabinetOnline() {
        if (!isTplstExist()) return;

        if (mQueueService == null) {
            CommonUtils.ShowErrorMessage(this, R.string.app_service_stopped);
            return;
        }

        if (StringUtils.isNullOrEmpty(mDataRepository.getDriverCall())) {
            CommonUtils.ShowErrorMessage(this, R.string.empty_settings_error);
            return;
        }

        if (mDataRepository.getSessionId() != null) {
            mDataRepository.setLoginOnly(true);
            goCabinetOnlineSucceeded();
            return;
        }

        showProgress(getString(R.string.go_cabinet_online_progress_message));
        //выполняем логин на сервер
        new CabinetLoginTask().execute();
    }

    private void goCabinetOnlineSucceeded() {
        mCurrentDelivery = null;
        switchStateTo(MainActivityState.InCabinet);
        if (inCabinetFragmentDelayed != null) {
            inCabinetOpenFragment(inCabinetFragmentDelayed);
            inCabinetFragmentDelayed = null;
        }
    }

    private void goCabinetOffline() {
        if (TextUtils.isEmpty(mDataRepository.getSessionId())) {
            switchStateTo(MainActivityState.Offline);
            return;
        }
        mDataRepository.setLoginOnly(false);
        //пришли в кабинет из текущей доставки или списка или со смены
        if (mPreviousState == MainActivityState.OnDelivery ||
                mPreviousState == MainActivityState.OnDeliveryList ||
                mPreviousState == MainActivityState.Online) {
            clearFragments();
            mAssignedDeliveries = mDataRepository.getDeliveries();
            if (mPreviousState == MainActivityState.OnDelivery) {
                mCurrentDelivery = mAssignedDeliveries.size() == 1 ?
                        mAssignedDeliveries.get(0) : null;
            }

            switchStateTo(mPreviousState);
        } else {
            endCurrentSession().subscribe(this::goOfflineSucceeded, Throwable::printStackTrace);
        }
    }

    //endregion

    private void switchStateTo(MainActivityState state) {
        if (mState != state) {
            mPreviousState = mState;
            mState = state;
            if (mQueueService != null) {
                mQueueService.setMainActivityState(state);
            }
            switchState();
        }
    }

    private void switchState() {
        setHeaderState();
        setDrawerState();
        switch (mState) {
            case Loading:
                enterView.setVisibility(View.GONE);
                dlvView.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                currDlvListView.setVisibility(View.GONE);
            case Offline:
                stopService(new Intent(this, LocationService.class));
                enterView.setVisibility(View.VISIBLE);
                dlvView.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                currDlvListView.setVisibility(View.GONE);
                mQueueNum = Constants.DEFAULT_QUEUE_NUMBER;
                break;
            case OnDelivery:
                updateDeliveryView();
                enterView.setVisibility(View.GONE);
                dlvView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                currDlvListView.setVisibility(View.GONE);
                updateDeliveryView();
                break;
            case Online:
                updateDeliveryListView();
                enterView.setVisibility(View.GONE);
                dlvView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                currDlvListView.setVisibility(View.GONE);
                break;
            case OnDeliveryList:
                updateCurrentDeliveryListView();
                enterView.setVisibility(View.GONE);
                dlvView.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                currDlvListView.setVisibility(View.VISIBLE);
                break;
        }

        invalidateOptionsMenu();

        //запускаем сервис для логирования местоположения
        if (mState == MainActivityState.Online || mState == MainActivityState.OnDelivery ||
                mState == MainActivityState.OnDeliveryList) {
            //пока так отсекаем повторный вызов команд для сервиса
            if (!PermissionManager.isLocationPermissionGranted(this)) {
                PermissionManager.requestLocationPermissions(this);
            } else {
                startService(LocationService.newIntent(this));
            }
        }
    }

    private void setDrawerState() {
        switch (mState) {
            case InCabinet:
                navigationDrawerInflate(R.menu.navdraw_pcab_menu);
                if (getSupportFragmentManager().findFragmentById(R.id.container) == null
                        && inCabinetFragmentDelayed == null) {
                    setUpCabinetDrawerAndBar();
                }
                swiftLayout.setVisibility(View.GONE);
                pcabLayout.setVisibility(View.VISIBLE);
                break;
            default:
                mTitle = getString(R.string.motocross_app_name);
                restoreActionBar();
                pcabLayout.setVisibility(View.GONE);
                swiftLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setHeaderState() {
        int status_text;
        int status_color;
        String txt;
        switch (mState) {
            case Loading:
            case Offline:
                status_text = R.string.offline_status;
                status_color = R.drawable.header_state_offline_color;
                txt = String.format("<b>%s</b>", getString(status_text).toUpperCase());
                break;
            case Online:
                status_text = R.string.online_status;
                status_color = R.drawable.header_state_online_color;
                txt = mQueueNum != Constants.DEFAULT_QUEUE_NUMBER ?
                        String.format(Locale.getDefault(), "<b>%s (%d)</b>",
                                getString(status_text).toUpperCase(), mQueueNum) :
                        String.format("<b>%s</b>", getString(status_text).toUpperCase());
                break;
            case OnDelivery:
            case OnDeliveryList:
                status_text = R.string.cary_status;
                status_color = R.drawable.header_state_ondelivery_color;
                txt = String.format("<b>%s</b>", getString(status_text).toUpperCase());
                break;
            default:
                return;
        }
        statusText.setText(Html.fromHtml(txt));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            headerView.setBackground(getResources().getDrawable(status_color));
        } else {
            headerView.setBackgroundDrawable(getResources().getDrawable(status_color));
        }
    }

    @Override
    protected void processMessage(Message message) {
        try {
            if (suspended) {
                Log.i("Process message: suspended " + message.getType());
                return;
            }
            if (mQueueService != null) {
                mQueueService.messageReceived(message);
            }
            Log.i("Process message: " + message.getId() + "/" + message.getType());
            switch (message.getType()) {

                case RelocationCurrent:
                    assignRelocationFromString(message.getMessage(), false);
                    break;
                case RelocationAssign:
                    assignRelocationFromString(message.getMessage(), true);
                    break;
                case RelocationUnassign:
                    unassignRelocationByNumber(message.getMessage());
                    break;
                case RelocationUpdate:
                    updateRelocationFromString(message.getMessage());
                    break;

                case DeliveryAdd:
                    addDeliveryFromString(message.getMessage());
                    break;
                case DeliveryRemove:
                    removeDeliveryFromString(message.getMessage());
                    break;
                case DeliveryCurrent:
                    assignDeliveryFromString(message.getMessage(), false);
                    break;
                case DeliveryAssign:
                    assignDeliveryFromString(message.getMessage(), true);
                    break;
                case DeliveryUnassign:
                    unassignDelivery(message.getMessage());
                    break;
                case DeliveryUpdate:
                    updateDeliveryFromString(message.getMessage());
                    break;

                case SessionExpired:
                    sessionExpired();
                    break;
                case ConnectionLost:
                    onConnectionLost();
                    break;
                case QueueNum:
                    updateQueueNum(message.getMessage());
                    break;
                case Template:
                    updateTemplate(message.getMessage());
                    break;
                case StuffLoaded:
                    onStuffLoaded();
                    break;
                case ChatMessage:
                    addMessageToChat(message.getMessage());
                    break;
            }
        } catch (Exception exception) { //защитим от падения при обработке сообщений
            Log.e(exception);
        }
    }

    private void addMessageToChat(String msg) {
        try {
            final JChatMessage message = new Gson().fromJson(msg, JChatMessage.class);
            if (message.message_type != null && message.message_type.equals("MD")) { //модальное сообщение - покажем на экране
                //с влзможностью сразу ответить на сообщение
                CommonUtils.ShowInfoMessageWithButtons(this, R.string.new_chat_message, message.text, R.string.answer_btn_text, R.string.mark_as_read, new DialogHandler() {
                    @Override
                    public void YesClick() { //отправим ответ и пометим комнату как прочитанную
                        JRoom room = new JRoom();
                        room.room_id = message.room_id;
                        markRoomAsRead(room);
                        sendChatMessage(message.room_id, null);
                    }

                    @Override
                    public void NoClick() { //просто сообщаем, что прочитали
                        JRoom room = new JRoom();
                        room.room_id = message.room_id;
                        markRoomAsRead(room);
                    }
                });
            }
            //добавим в списки сообщений
            if (mState == MainActivityState.InCabinet) {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
                switch (current_fragment_code) {
                    case R.id.pcab_action_swift_room_list: //в списке комнат - подсветим ту, в котрой новое сообщение
                        ((SwiftRoomListFragment) current).addNewMessage(message);
                        break;
                    case R.id.pcab_action_swift_room: //покажем новое сообщение
                        ((SwiftRoomListFragment) sections.get(R.id.pcab_action_swift_room_list)).addNewMessage(message);
                        ((SwiftRoomFragment) current).addNewMessage(message);
                        break;
                }
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("com.rosa.motocross.chat.open".equals(intent.getAction())) {
            String room_id = intent.getStringExtra("room_id");
            openChatRoom(room_id);
        }
    }

    private void openChatRoom(String room_id) {
        if (!StringUtils.isNullOrEmpty(room_id)) {
            JRoom room = ((SwiftRoomListFragment) sections.get(R.id.pcab_action_swift_room_list)).getRoomById(room_id);
            SwiftRoomFragment sFragment = SwiftRoomFragment.getInstance(room);
            inCabinetOpenFragment(sFragment, true);
        } else {
            inCabinetOpenFragment(R.id.pcab_action_swift_room_list, true);
        }
        if (mState != MainActivityState.InCabinet) {
            goCabinetOnline();
        }
    }

    private void askQuestion(final List<QuestDto> quest, final Delivery dlv) {
        try {
            Intent i = new Intent(this, QuestActivity.class);
            i.putExtra("quest", (Serializable) quest);
            if (dlv != null) {
                i.putExtra("delivery", dlv.getNumber());
                i.putExtra("lgorts", (Serializable) dlv.getWarehouseList());
            }
            startActivity(i);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void startCupRequest() {
        startCupRequest(true);
    }

    private void startCupRequest(boolean isPerforming) {
        startCupRequest(true, true, isPerforming);
    }

    public void startCupRequest(boolean isBlocked, boolean canBeDelayed, boolean isPerforming) {
        int taskIndex = mDataRepository.getIndexForNextPhotoSessionTask();
        if (taskIndex != PhotoSessionTaskDto.EMPTY_TASK) {
            try {
                Intent cupIntent = CupRequestActivity.newIntent(this,
                        taskIndex, isBlocked, canBeDelayed, isPerforming);
                startActivityForResult(cupIntent, Constants.REQUEST_CUP_PERFORMING);
            } catch (Exception e) {
                Log.e(e.getMessage(), e);
            }
        }
    }

    private void startPhotoRequest() {
        int indexTask = mDataRepository.getIndexForNextDocumentTask();
        if (indexTask != PhotoDocumentTaskDto.EMPTY_TASK) {
            startActivityForResult(PhotoDocumentActivity.newInstance(this, indexTask),
                    SHOW_PHOTO_DOC_ACTIVITY);
        }
    }

    private void startSurveyForOptionalQuests() {
        List<QuestDto> quests = new ArrayList<>();
        //здесь проверим нужно ли задать вопрос
        for (QuestDto quest : mDataRepository.getOptionalQuests()) {
            //необязательный вопрос - задаём при выходе на смену
            quests.add(quest);
        }
        if (quests.size() > 0)
            askQuestion(quests, null);
    }

    private void startSurveyForRequiredQuests(Delivery delivery) {
        for (QuestDto quest : mDataRepository.getRequiredQuests()) {
            //askQuestion(quest, delivery);
        }
    }

    private void updateTemplate(String message) {
        try {
            DeliveryTemplateDto template = new DeliveryTemplateDto(message);
            mDataRepository.addOrUpdateDeliveryTemplate(template);
            switch (template.getType()) {
                case DML:
                case DMLS:
                    if (mState == MainActivityState.Online) {
                        updateDeliveryListView();
                    }
                    break;
                case DAD:
                    if (mState == MainActivityState.OnDelivery) {
                        updateDeliveryView();
                    }
                    break;
                case DCL:
                    if (mState == MainActivityState.OnDeliveryList)
                        updateCurrentDeliveryListView();
                    break;
            }
        } catch (Exception e) {
            Log.e(e);
        }
    }

    @Override
    protected void updateQueueNum(String message) {
        super.updateQueueNum(message);
        mQueueNum = mDataRepository.getQueueNumber();
        if (mState == MainActivityState.Online) {
            setHeaderState();
        }
    }

    @Override
    protected void sessionExpired() {
        super.sessionExpired();
        clearFragments();
        switchStateTo(MainActivityState.Offline);
    }

    private void startListeningFromTheBeginning() {
        mCurrentDelivery = null;
        deliveryList.clear();
        mAssignedDeliveries.clear();
        mDataRepository.removeRelocations();
        mDataRepository.removeDeliveries();
        mQueueService.resetLastId();
        switchStateTo(MainActivityState.Online);
    }

    public void prepareNavigationDrawerMenu(Menu menu) {
        //TODO: 28.10.2016 уточнить, корректно ли в этом месте показывать пункт меню "Открыть ворота"
        setOpenGateVisibility();

        switch (mState) {
            case OnDelivery:
                if (menu != null && (mCurrentDelivery == null ||
                        (TextUtils.isEmpty(mCurrentDelivery.getFirstPhone()) &&
                                TextUtils.isEmpty(mCurrentDelivery.getSecondPhone())))) {
                    menu.removeItem(R.id.action_call_client);
                }
                if (menu != null && mCurrentDelivery != null) {
                    MenuItem item = menu.findItem(R.id.action_waist_end);
                    if (item != null)
                        item.setVisible(mCurrentDelivery.isIdling() && gpsAvail);
                    item = menu.findItem(R.id.action_waist_start);
                    if (item != null)
                        item.setVisible(!mCurrentDelivery.isIdling() && gpsAvail);
                    if (mDataRepository.getDriverMessages().isEmpty()) {
                        menu.removeItem(R.id.action_send_message);
                    }
                    item = menu.findItem(R.id.action_terminal);
                    if (item != null)
                        item.setVisible(mCurrentDelivery.isPayOnPlace());
                }
                break;
            case OnDeliveryList:
                MenuItem item = menu.findItem(R.id.action_reject_set_delivery);
                if (item != null)
                    item.setVisible(currInSet);
                break;
        }
    }

    private void navigationDrawerInflate() {
        navigationDrawerInflate(-1);
    }

    private void navigationDrawerInflate(int menuId) {
        navigationDrawerInflate(menuId, true);
    }

    private void navigationDrawerInflate(int menuId, boolean enabled) {
        mNavigationDrawerFragment.clear();

        if (menuId <= 0) mNavigationDrawerFragment.setDrawerEnabled(false);
        else {
            //            if (_state != MainActivityState.InCabinet) { mNavigationDrawerFragment.setFirst_time(false); }
            //            else { mNavigationDrawerFragment.setFirst_time(true); }
            getMenuInflater().inflate(menuId, mNavigationDrawerFragment);
            prepareNavigationDrawerMenu(mNavigationDrawerFragment);
            mNavigationDrawerFragment.UpdateDrawerView();
            mNavigationDrawerFragment.setDrawerEnabled(enabled);
        }
    }

    public void setNavigationDrawerEnabled(boolean enabled) {
        if (mNavigationDrawerFragment != null)
            mNavigationDrawerFragment.setDrawerEnabled(enabled);
    }

    //Обновление информации о текущей открытой доставке
    private void updateDeliveryView() {
        if (mCurrentDelivery != null) {

            String deliveryText = mDataRepository.getDeliveryDataHtml(
                    TemplateType.DAD, mCurrentDelivery);

            String[] dlv_text = deliveryText.split("<br\\s*/?>", 2);

            if (dlv_text.length > 0) {
                dlvInWorkHeader.setText(Html.fromHtml(dlv_text[0]));
            }
            if (dlv_text.length > 1) {
                dlvInWorkView.setText(Html.fromHtml(dlv_text[1]));
            }

            if (mCurrentDelivery.hasCoordinates()) {
                dlvInWorkGpsButton.setVisibility(View.VISIBLE);
            } else {
                dlvInWorkGpsButton.setVisibility(View.GONE);
            }

            if (mCurrentDelivery.isSchemaExist()) {
                dlvInWorkSchemaButton.setVisibility(View.VISIBLE);
                dlvInWorkSchemaImageView.setVisibility(View.VISIBLE);
                setImageAlpha(dlvInWorkSchemaImageView, 255);
                dlvInWorkSchemaProgress.setVisibility(View.GONE);
            } else {
                dlvInWorkSchemaButton.setVisibility(View.GONE);
                dlvInWorkSchemaImageView.setVisibility(View.GONE);
                dlvInWorkSchemaProgress.setVisibility(View.GONE);
            }

        } else {
            dlvInWorkHeader.setText("");
            dlvInWorkView.setText("");
            dlvInWorkGpsButton.setVisibility(View.GONE);
            dlvInWorkSchemaButton.setVisibility(View.GONE);
            dlvInWorkSchemaProgress.setVisibility(View.GONE);
            dlvInWorkSchemaImageView.setVisibility(View.GONE);
        }
    }

    //Обновление экрана со списком назанченных доставок
    private void updateCurrentDeliveryListView() {
        currentDeliveryList4View.clear();
        double total = 0;
        String set = "";
        for (Delivery currentDelivery : mAssignedDeliveries) {
            currentDeliveryList4View.add(currentDelivery);
            total += currentDelivery.getTotalCost();
            if (StringUtils.isNullOrEmpty(set))
                set = currentDelivery.getSetNumber();
            else if (!set.equals(currentDelivery.getSetNumber()))
                set = "";
        }
        currAdapter.notifyDataSetChanged();
        if (!StringUtils.isNullOrEmpty(set)) {
            currInSet = true;
            setNumberTextView.setText(Html.fromHtml(String.format("<b>Комплект:</b> %s", set)));
            setNumberTextView.setVisibility(View.VISIBLE);
            totalTextView.setText(Html.fromHtml(String.format("<b>Итого по комплекту:</b> %.2f руб.", total)));
            totalTextView.setVisibility(View.VISIBLE);
            setHeaderView.setVisibility(View.VISIBLE);
        } else {
            currInSet = false;
            setNumberTextView.setVisibility(View.GONE);
            totalTextView.setVisibility(View.GONE);
            setHeaderView.setVisibility(View.GONE);
        }
    }

    //Обновление экрана со списком доставок
    private void updateDeliveryListView() {
        deliveryList4View.clear();
        Collections.sort(deliveryList, (delivery, delivery2) ->
                delivery.getStartDate().compareTo(delivery2.getStartDate()));
        for (Delivery d : deliveryList) {
            if (!showDelivery(d)) continue;
            if (!d.inSet())
                deliveryList4View.add(d);
            else {
                boolean add = true;
                for (Delivery d4v : deliveryList4View)
                    if (d4v.inSameSet(d)) {
                        add = false;
                        break;
                    }
                if (add)
                    deliveryList4View.add(d);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "INC_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public void getIncidentPhoto() {
        try {
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                File _photoFile = createImageFile();
                photoFilePath = _photoFile.getAbsolutePath();
                Intent intent = CameraActivity.newIntent(this,
                        photoFilePath,
                        Constants.RECIPIENT_PHOTO_INCIDENT);
                startActivityForResult(intent, Constants.REQUEST_TAKE_PHOTO);
            } else {
                CommonUtils.ShowErrorMessage(this, "Для работы приложения требуется установленная SD карта");
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
            CommonUtils.ShowErrorMessage(this, "Не удалось запустить приложение-камеру");
        }
    }

    private void sendIncident(final String tknum, final String photoFilePath) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    double targetW = 800;
                    double targetH = 800;

                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(photoFilePath, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    int scaleFactor = (int) Math.ceil(Math.max(photoW / targetW, photoH / targetH));

                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap b = null;
                    try {
                        b = BitmapFactory.decodeFile(photoFilePath, bmOptions);
                    } catch (OutOfMemoryError e1) {
                        Log.e(e1.getMessage());
                        System.gc();

                        try {
                            bmOptions.inSampleSize *= 2;
                            b = BitmapFactory.decodeFile(photoFilePath, bmOptions);
                        } catch (OutOfMemoryError e2) {
                            Log.e(e2.getMessage());
                        }
                    }

                    if (b != null) {
                        File f = new File(photoFilePath);
                        f.delete();

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                        b.recycle();

                        byte[] array = bos.toByteArray();
                        bos.close();
                        String json = "{ TKNUM : \"" + tknum + "\", PHOTO_BASE64: \"" + Base64.encodeToString(array, Base64.DEFAULT) + "\"}";
                        array = null; //это длс GC
                        final String res = ZMotoService.getInstance().callService(ServiceFunction.Incident, json);
                        if (StringUtils.isNullOrEmpty(res))
                            msg = "Отправка фото успешно завершена";
                        else
                            msg = "Отправка фото не выполнена";
                    } else {
                        msg = "Отправка фото не выполнена";
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    msg = "Отправка фото не выполнена";
                }
                final String msg_ = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, msg_, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        Toast.makeText(this, "Отправка фото запущена", Toast.LENGTH_SHORT).show();
        t.start();
    }

    private void doWaisting(final Delivery finishedDelivery, String location, final boolean waist) {
        if (finishedDelivery == null) return;
        showProgress(getString(waist ? R.string.start_waisting_progress_message : R.string.end_waisting_progress_message));
        ServiceCallback events = new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onCancelled() {
                hideProgress();
                unsuspend();
            }

            @Override
            public void onFinished(String evParams) {
                try {
                    JWaistOut jout = new Gson().fromJson(evParams, JWaistOut.class);
                    if (StringUtils.isNullOrEmpty(jout.err)) {
                        finishedDelivery.setIdling(waist);
                        Toast.makeText(MainActivity.this, waist ? R.string.waist_start_succ : R.string.waist_end_succ, Toast.LENGTH_SHORT).show();
                    } else
                        CommonUtils.ShowErrorMessage(MainActivity.this, jout.err);
                } catch (Exception e) {
                    CommonUtils.ShowErrorMessage(MainActivity.this, waist ? R.string.start_waisting_error : R.string.end_waisting_error);
                    if (god_mode)
                        CommonUtils.ShowErrorMessage(MainActivity.this, e.getMessage());
                    Log.e(e.getMessage());
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                if (god_mode)
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
            }
        };
        WaistRequest request = new WaistRequest(finishedDelivery.getNumber(), location,
                finishedDelivery.getFirstPhone());
        if (waist) {
            SapRequestUtils.startWaisting(request, events);
        } else {
            SapRequestUtils.endWaisting(request, events);
        }
    }

    private void showGpsOptions() {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    private boolean isGPSEnabled() {
        boolean isEnabled = false;
        try {
            isEnabled = ((LocationManager) getSystemService(LOCATION_SERVICE)).
                    isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e(ex);
        }
        return isEnabled;
    }

    public void doFind(DeliverySearch.SearchOptions options) {
        showProgress(R.string.find_deliveries);
        DeliverySearch.doSearch(options, this);
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void sendTTNScan() {
        Toast.makeText(this, "Выберите/сделайте фото ТТН", Toast.LENGTH_SHORT).show();
        startGalleryActivity();
    }

    private void onScansFromGalleryGet(Intent data) {
        try {
            if (mCurrentDelivery == null) return;
            Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.TAG_IMAGE_URI);
            Uri[] uris = new Uri[parcelableUris.length];
            System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
            showProgress(R.string.send_data_progress);
            final ScanSendAsyncTaskParams params = new ScanSendAsyncTaskParams();
            params.tknum = mCurrentDelivery.getNumber();
            params.uris = uris;
            new ScanSendAsyncTask().execute(params);
        } catch (Exception e) {
            CommonUtils.ShowErrorMessage(this, "Что-то пошло не так...");
        }
    }

    @Override
    public void sendChatMessage(final String room_id, final ChatMessageSentCallback callback) {
        final Activity activity = this;
        final EditText input = new EditText(activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.send_message_text)
                .setView(input)
                .setPositiveButton(R.string.dialog_send_comment_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comment = input.getText() != null ? input.getText().toString() : "";
                        if (StringUtils.isNullOrEmpty(comment)) return;
                        showProgress(R.string.send_data_progress);
                        final JChatMessage message = new JChatMessage();
                        message.room_id = room_id;
                        message.text = comment;
                        message.ernam_text = "Я";
                        Time t = new Time();
                        t.setToNow();
                        message.erdat = t.format("%Y%m%d");
                        message.erzet = t.format("%H%M%S");

                        ChatMessageRequest request = new ChatMessageRequest(message);
                        SapRequestUtils.addChatMessage(request, new ServiceCallback() {
                            @Override
                            public void onEndedRequest() {
                                hideProgress();
                            }

                            @Override
                            public void onCancelled() {
                                hideProgress();
                            }

                            @Override
                            public void onFinished(String evParams) {
                                if (callback != null)
                                    callback.onSentComplete(message, evParams);
                                if ("0000000000".equals(evParams))
                                    CommonUtils.ShowErrorMessage(activity, "Не удалось отправить сообщение!");
                            }

                            @Override
                            public void onFinishedWithException(WSException ex) {
                                CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                                if (god_mode)
                                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                            }
                        });
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void markRoomAsRead(final JRoom room) {
        try {
            showProgress();
            RoomRequest request = new RoomRequest(room.room_id, mDataRepository.getSessionId());
            SapRequestUtils.markRoomAsRead(request, new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    hideProgress();
                }

                @Override
                public void onFinished(String evParams) {
                    if (mState == MainActivityState.InCabinet) {
                        Fragment current = getSupportFragmentManager().findFragmentById(R.id.container);
                        switch (current_fragment_code) {
                            case R.id.pcab_action_swift_room_list: //в списке комнат - подсветим ту, в котрой новое сообщение
                                ((SwiftRoomListFragment) current).markRoomAsRead(room);
                                break;
                            case R.id.pcab_action_swift_room: //покажем новое сообщение
                                ((SwiftRoomListFragment) sections.get(R.id.pcab_action_swift_room_list)).markRoomAsRead(room);
                                ((SwiftRoomFragment) current).markRoomAsReadComplete(room);
                                break;
                        }
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    CommonUtils.ShowErrorMessage(MainActivity.this, ex.getMessage());
                    if (god_mode)
                        CommonUtils.ShowErrorMessage(MainActivity.this, ex.getFullMessage());
                }

                @Override
                public void onCancelled() {
                    hideProgress();
                }
            });
        } catch (Exception ignored) {
        }
    }

    //меню терминала
    public void showTerminalMenu() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.SdvorTheme_AlertDialog))
                .setTitle(R.string.action_terminal)
                .setIcon(R.drawable.ic_action_terminal_light)
                .setPositiveButton(R.string.action_terminal_test, (dialog, which) -> doPosTerminalOperation(0))
                .setNegativeButton(R.string.action_terminal_pay, (dialog, which) -> doPosTerminalOperation(1));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    protected void openGpsLocation() {
        if (mCurrentDelivery.hasCoordinates()) {
            List<GeoObject> addresses = mCurrentDelivery.getAddresses();
            if (addresses != null) {
                startActivity(DeliveryMapActivity.newIntent(this, new ArrayList<>(addresses)));
            }
        }
    }

    protected void openSchemaLocation() {
        if (mCurrentDelivery.isSchemaExist()) {
            setImageAlpha(dlvInWorkSchemaImageView, 64);
            dlvInWorkSchemaProgress.setVisibility(View.VISIBLE);
            new GetLocationSchemaAsyncTask(dlvInWorkSchemaImageView, dlvInWorkSchemaProgress).execute(mCurrentDelivery.getNumber());
        }
    }

    private void setImageAlpha(ImageView imageView, int alpha) {
        if (imageView == null) return;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            imageView.setImageAlpha(alpha);
        } else {
            imageView.setAlpha(alpha);
        }
    }

    public void showDeliveryDocsList(String tknum) {
        Intent i = new Intent(this, DocumentsDialogActivity.class);
        i.putExtra("tknum", tknum);
        startActivity(i);
        return;
    }

    //region Tasks

    private class GetLocationSchemaAsyncTask extends AsyncTask<String, Void, File> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<ProgressBar> progressBarWeakReference;

        public GetLocationSchemaAsyncTask(ImageView view, ProgressBar progressBar) {
            imageViewWeakReference = new WeakReference<>(view);
            progressBarWeakReference = new WeakReference<>(progressBar);
        }

        private void progressBarStop() {
            if (progressBarWeakReference != null) {
                ProgressBar progressBar = progressBarWeakReference.get();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }

        @Override
        protected File doInBackground(String... params) {
            String tknum = params[0];
            return DocumentsUtils.getLocationSchemaFile(tknum);
        }

        @Override
        protected void onPostExecute(File file) {
            if (isCancelled()) {
                file = null;
            }

            if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();
                setImageAlpha(imageView, 255);
            }
            progressBarStop();

            if (file != null) {
                startActivity(SupportUtils.getIntentView(file));
            }
        }
    }

    private class ScanSendAsyncTask extends AsyncTask<ScanSendAsyncTaskParams, Integer, Boolean> {
        private int mLength;

        @Override
        protected Boolean doInBackground(ScanSendAsyncTaskParams... params) {
            mLength = params[0].uris.length;
            try {
                double targetW = 800;
                double targetH = 800;
                String path;
                for (int i = 0; i < mLength; i++) {
                    publishProgress(i + 1);
                    path = params[0].uris[i].toString();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    int scaleFactor = (int) Math.ceil(Math.max(photoW / targetW, photoH / targetH));

                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    Bitmap b = null;
                    try {
                        b = BitmapFactory.decodeFile(path, bmOptions);
                    } catch (OutOfMemoryError e1) {
                        Log.e(e1.getMessage());
                        System.gc();

                        try {
                            bmOptions.inSampleSize *= 2;
                            b = BitmapFactory.decodeFile(path, bmOptions);
                        } catch (OutOfMemoryError e2) {
                            Log.e(e2.getMessage());
                        }
                    }

                    if (b != null) {
                        //File f = new File(path);
                        //f.delete();

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        b.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                        b.recycle();

                        byte[] array = bos.toByteArray();
                        bos.close();
                        String descr = "Скан-копия ТТН " + Integer.toString(i + 1);
                        String json = "{ DESCR: \"" + descr + "\", TKNUM : \"" + params[0].tknum + "\", PHOTO_BASE64: \"" + Base64.encodeToString(array, Base64.DEFAULT) + "\"}";
                        array = null; //for GC
                        final String res = ZMotoService.getInstance().callService(ServiceFunction.Incident, json);
                        if (!StringUtils.isNullOrEmpty(res))
                            return false;
                    }
                }
                return true;
            } catch (Exception e) {
                Log.e(e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            String msg = "Отправляем " + Integer.toString(values[0]) + "/" + Integer.toString(mLength);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            hideProgress();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideProgress();
            if (aBoolean)
                Toast.makeText(MainActivity.this, R.string.scan_succeed, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private class ScanSendAsyncTaskParams {
        public Uri[] uris;
        public String tknum;
    }

    //Таск для логина
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private WSException wsException;

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                mQueueService.startNewSession();
                return true;
            } catch (WSException ws) {
                wsException = ws;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideProgress();
            if (success) {
                goOnlineSucceeded();
            } else
                goOnlineFailed(wsException);
        }

        @Override
        protected void onCancelled() {
            hideProgress();
        }
    }

    //Таск для логина в кабинет
    public class CabinetLoginTask extends AsyncTask<Void, Void, Boolean> {

        private WSException wsException;

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                mQueueService.startNewCabinetSession();
                return true;
            } catch (WSException ws) {
                wsException = ws;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideProgress();
            if (success)
                goCabinetOnlineSucceeded();
            else
                goOnlineFailed(wsException);
        }

        @Override
        protected void onCancelled() {
            hideProgress();
        }
    }

    //Таск для логина
    public class LogoutTask extends AsyncTask<Void, Void, Boolean> {
        private final WeakReference<MainActivity> contextWeakReference;
        private WSException wsException;

        public LogoutTask(MainActivity context) {
            contextWeakReference = new WeakReference<MainActivity>(context);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                mQueueService.endCurrentSession();
                return true;
            } catch (WSException ws) {
                wsException = ws;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideProgress();
            if (success)
                goOfflineSucceeded();
            else {
                final MainActivity context = contextWeakReference.get();
                if (context != null && !context.isFinishing())
                    goOfflineFailed(context, wsException);
            }
        }

        @Override
        protected void onCancelled() {
            hideProgress();
        }
    }

    //Таск для логина
    public class CabinetLogoutTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showProgress(R.string.go_cabinet_offline_progress);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                mQueueService.endCurrentSession();
                return true;
            } catch (WSException ws) {
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            hideProgress();
            goOfflineSucceeded();
        }

        @Override
        protected void onCancelled() {
            hideProgress();
        }
    }


    //region ============================== Deliveries ==============================

    public void setCurrentDelivery(Delivery currentDelivery) {
        mCurrentDelivery = currentDelivery;
    }

    //endregion

}