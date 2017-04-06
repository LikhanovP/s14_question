package com.rosa.swift.core.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.DialogHandler;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.ImageUtils;
import com.rosa.swift.core.business.utils.NotificationUtils;
import com.rosa.swift.core.business.utils.PermissionManager;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.network.requests.rating.CupPhotoRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.fragments.CupRequestFragment;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;

import java.io.File;
import java.util.Date;

public class CupRequestActivity extends LogonlessActivity implements View.OnClickListener {

    public static final String CUP_INPUT = "CUP_INPUT";
    /**
     * Признак того, может ли фотосессия ЦУП быть отложена
     */
    public static final String CUP_CAN_BE_DELAY = "CUP_CAN_BE_DELAY";
    //Признак того, должна ли обрабатываться кнопка Back
    public static final String CUP_IS_BLOCKED = "CUP_IS_BLOCKED";
    //Признак того, началось ли выполнение фотосесии
    public static final String CUP_IS_PERFORMING = "CUP_IS_PERFORMING";

    private static final String FILE_URI = "file_uri";
    private static final String PHOTO_FILE = "photo_file";
    private static final String PHOTO_NAME = "photo_name";

    private Button mSendBtn; //кнопка отправки фотографии
    private Button mDelayBtn; //кнопка переноса фотоссеии
    private Button mBtnCupDoPhoto;   //включить камеру для фото

    //ссылка на фрагмент - с типом фотографии и самим фото
    private CupRequestFragment mCupRequestFragment;

    @Nullable
    private PhotoSessionTaskDto mSessionTask;

    private File mPhotoFile; //фотография (объект)
    private Uri mFileUri; // Путь до фотографии на устройстве
    private String mPhotoName;

    private DataRepository mDataRepository;

    /**
     * Признак того, должна ли обрабатываться кнопка Back
     */
    private boolean mIsBlocked;
    /**
     * Признак того, может ли фотосессия ЦУП быть отложена
     */
    private boolean mCupCanBeDelayed;
    /**
     * Признак того, была ли фотосессия отложена ранее
     */
    private boolean mIsCupDelayed;
    /**
     * Признак того, началось ли выполнение фотосесии
     */
    private boolean mIsCupPerforming;

    public static Intent newIntent(
            Context context, int taskIndex, boolean isBlocked, boolean canBeDelayed, boolean isCupPerforming) {
        Intent intent = new Intent(context, CupRequestActivity.class);
        intent.putExtra(CUP_INPUT, taskIndex);
        intent.putExtra(CUP_IS_BLOCKED, isBlocked);
        intent.putExtra(CUP_CAN_BE_DELAY, canBeDelayed);
        intent.putExtra(CUP_IS_PERFORMING, isCupPerforming);
        return intent;
    }

    //region Life cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cup_session);
        mDataRepository = DataRepository.getInstance();

        mBtnCupDoPhoto = (Button) findViewById(R.id.cup_do_photo);
        mSendBtn = (Button) findViewById(R.id.cup_goto_next_view);
        mDelayBtn = (Button) findViewById(R.id.cup_delay);

        Intent intent = this.getIntent();
        mIsBlocked = intent.getBooleanExtra(CUP_IS_BLOCKED, false);
        mCupCanBeDelayed = intent.getBooleanExtra(CUP_CAN_BE_DELAY, false);
        mIsCupDelayed = mDataRepository.isCupDelayed();
        mIsCupPerforming = intent.getBooleanExtra(CUP_IS_PERFORMING, false);
        int taskIndex = intent.getIntExtra(CUP_INPUT, PhotoDocumentTaskDto.EMPTY_TASK);
        if (taskIndex != PhotoDocumentTaskDto.EMPTY_TASK) {
            mSessionTask = mDataRepository.getPhotoSessionTask(taskIndex);
        }

        switchExecuteBtn();

        mCupRequestFragment = new CupRequestFragment();
        if (!isFinishing()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.cup_request_fragment, mCupRequestFragment);
            ft.commit();
        }

        File pictures = DocumentsUtils.getCupPhotosDir();
        mPhotoName = DateFormat.format("dd-MM-yyyy hh-mm-ss", new Date()).toString();
        mPhotoFile = new File(pictures,
                mPhotoName + ".jpg");

        mBtnCupDoPhoto.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mDelayBtn.setOnClickListener(this);

        mCupRequestFragment.setCupRequestItem(mSessionTask);
    }

    @Override
    protected void onPause() {
        mDataRepository.updatePhotoSessionTask(mSessionTask);
        super.onPause();
    }

    //endregion

    //region Bundle

    @Override
    public void onBackPressed() {
        if (!mIsBlocked && !mIsCupPerforming) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cup_do_photo:
                if (!PermissionManager.isCameraPermissionGranted(CupRequestActivity.this)) {
                    PermissionManager.requestCameraPermissions(CupRequestActivity.this);
                } else {
                    launchCamera();
                }
                break;
            case R.id.cup_goto_next_view:
                sendCupItem();
                break;
            case R.id.cup_delay:
                int delayDays = mDataRepository.getCupDaysToDelay();

                String offsetMessage = delayDays > 0 ? getResources().getQuantityString(
                        R.plurals.cup_session_offset_message_with_days, delayDays, delayDays) :
                        getResources().getString(R.string.cup_session_offset_message_without_days);
                CommonUtils.ShowInfoMessageWithButtons(this,
                        R.string.cup_session_offset_title,
                        offsetMessage,
                        R.string.cup_dialog_delaying_btn_positive,
                        R.string.cup_dialog_delaying_btn_negative,
                        new DialogHandler() {
                            @Override
                            public void YesClick() {
                                delayCup();
                            }

                            @Override
                            public void NoClick() {
                                //ничего не делаем, просто закрываем диалог
                            }
                        });
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(CUP_INPUT, mSessionTask);
        bundle.putParcelable(FILE_URI, mFileUri);
        bundle.putSerializable(PHOTO_FILE, mPhotoFile);
        bundle.putString(PHOTO_NAME, mPhotoName);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mSessionTask = (PhotoSessionTaskDto) bundle.getSerializable(CUP_INPUT);
        mFileUri = bundle.getParcelable(FILE_URI);
        mPhotoFile = (File) bundle.getSerializable(PHOTO_FILE);
        mPhotoName = bundle.getString(PHOTO_NAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                showInfoMessage(R.string.error_message_permission_camera);
            }
        }
    }

    private void launchCamera() {
        Intent intent = CameraActivity.newIntent(this, mPhotoName,
                Constants.RECIPIENT_CUP_SESSION);
        startActivityForResult(intent, Constants.REQUEST_TAKE_PHOTO);
    }

    private void switchExecuteBtn() {
        if (!mIsCupDelayed && !mIsCupPerforming && mCupCanBeDelayed) {
            showDelayBtn();
        } else {
            showSendBtn();
        }
    }

    private void showSendBtn() {
        mDelayBtn.setVisibility(View.GONE);
        mSendBtn.setEnabled(false);
        mSendBtn.setVisibility(View.VISIBLE);
    }

    private void showDelayBtn() {
        mSendBtn.setVisibility(View.GONE);
        mDelayBtn.setVisibility(View.VISIBLE);
    }

    private void sendResultForPerforming() {
        sendResult(RESULT_OK, true);
    }

    private void sendResultForDelay() {
        NotificationUtils.getInstance(this).createInfoNotification(
                getString(R.string.cup_session_bar_message_body));
        sendResult(RESULT_OK, false);
    }

    private void sendResult(int resultCode, boolean isPerforming) {
        Intent intent = new Intent();
        intent.putExtra(CUP_IS_PERFORMING, isPerforming);
        setResult(resultCode, intent);
        finish();
    }

    /**
     * Перенести фотосессию
     */
    private void delayCup() {
        showProgress(R.string.cup_session_delay_progress);
        try {
            SapRequestUtils.delayCup(new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    hideProgress();
                }

                @Override
                public void onFinished(String evParams) {
                    hideProgress();
                    if (evParams.equals(Constants.SAP_TRUE_FLAG)) {
                        showInfoMessage(R.string.cup_session_delayed_successful_msg);
                        sendResultForDelay();
                    } else {
                        showErrorMessage(R.string.cup_session_delayed_unsuccessful_msg);
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
        } catch (Exception ignored) {
            hideProgress();
            showErrorMessage(R.string.cup_session_delayed_unsuccessful_msg);
        }
    }

    private void sendCupItem() {
        showProgress(R.string.send_photo_progress);
        if (mSessionTask != null) {
            String photoPath = ImageUtils.getBitmapBase64New(mSessionTask.getPhotoPath(), 100);
            CupPhotoRequest request = new CupPhotoRequest(mSessionTask, photoPath);
            SapRequestUtils.sendDriverCupPhoto(request, new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    hideProgress();
                }

                @Override
                public void onFinished(String param) {
                    hideProgress();
                    if (param.equals(Constants.SAP_TRUE_FLAG)) {
                        CommonUtils.showToast(getApplicationContext(), R.string.send_photo_successful);
                        mDataRepository.removePhotoSessionTask(mSessionTask);
                        sendResultForPerforming();
                    } else {
                        showInfoMessage(R.string.send_photo_unsuccessful);
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    CommonUtils.ShowErrorMessage(getApplicationContext(), ex.getMessage());
                }

                @Override
                public void onCancelled() {
                    hideProgress();
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_TAKE_PHOTO:
                if (data != null && mSessionTask != null) {
                    mSessionTask.setPhotoPath(mPhotoFile.getPath());
                    mCupRequestFragment.setCupRequestItem(mSessionTask);
                    mIsCupPerforming = true;
                    mSendBtn.setVisibility(View.VISIBLE);
                    mDelayBtn.setVisibility(View.GONE);
                    mSendBtn.setEnabled(true);
                }
        }
    }

}

