package com.rosa.swift.mvp.ratings.documents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.ImageUtils;
import com.rosa.swift.core.business.utils.PermissionManager;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.network.json.sap.photoBase.PhotoDocumentRequest;
import com.rosa.swift.core.network.json.sap.photoBase.PhotoDocumentResponse;
import com.rosa.swift.core.network.requests.rating.DocumentPhotoRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.CameraActivity;
import com.rosa.swift.core.ui.activities.LogonlessActivity;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// Активность в которой происходит фотографирование документов, для личных данных водителя
public class PhotoDocumentActivity extends LogonlessActivity {
    private static final String EXTRA_TASK_INDEX = "EXTRA_TASK_INDEX";
    //private static final int CUP_VIEW_PHOTO_SIZE_DP = 160; // размер фото для превью

    private Button mSendPhotoBtn; //кнопка на отправку фотографии
    private Button mOpenCameraBtn;   //включить камеру для фото
    private TextView mDocumentTypeTxt; // вид фотографии
    private TextView mDatePhotoTxt; // дата съемки
    private TextView mActDatePhotoTxt; // актуально до текущей даты
    private ImageView mPreviewImageView; // превью фотографии которое будет отправленно

    @Nullable
    private PhotoDocumentTaskDto mCurrentTask;

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private DataRepository mDataRepository;

    public static Intent newInstance(Context context, int taskIndex) {
        Intent intent = new Intent(context, PhotoDocumentActivity.class);
        intent.putExtra(EXTRA_TASK_INDEX, taskIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_document);

        mDataRepository = DataRepository.getInstance();

        mDocumentTypeTxt = (TextView) findViewById(R.id.photo_document_type_txt);
        mDatePhotoTxt = (TextView) findViewById(R.id.photo_document_date_txt);
        mActDatePhotoTxt = (TextView) findViewById(R.id.photo_document_actual_txt);
        mPreviewImageView = (ImageView) findViewById(R.id.photo_base_send);
        mOpenCameraBtn = (Button) findViewById(R.id.photo_base_do_photo);
        mSendPhotoBtn = (Button) findViewById(R.id.photo_base_goto_next_view);

        int taskIndex = getIntent().getExtras().getInt(EXTRA_TASK_INDEX,
                PhotoDocumentTaskDto.EMPTY_TASK);
        if (taskIndex != PhotoDocumentTaskDto.EMPTY_TASK) {
            mCurrentTask = mDataRepository.getPhotoDocumentTask(taskIndex);
            updateView(mCurrentTask);
        }

        mOpenCameraBtn.setOnClickListener(view -> checkPermissions());
        mSendPhotoBtn.setOnClickListener(view -> sendDocumentItem());
        mSendPhotoBtn.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Проверка, что если мы сделали фото, то его необходимо отправить
        if (keyCode == KeyEvent.KEYCODE_BACK && !mSendPhotoBtn.isEnabled()) {
            finish();
        } else if (keyCode == KeyEvent.KEYCODE_BACK && mSendPhotoBtn.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Необходимо отправить фотографию",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_TAKE_PHOTO:
                if (mCurrentTask != null) {
                    if (resultCode == Activity.RESULT_CANCELED) {
                        mCurrentTask.markAsCompleted();
                        return;
                    }
                    if (data != null) {
                        mCurrentTask.setDatePhoto(data.getStringExtra(Constants.EXTRA_PHOTO_DATE));
                        updateView(mCurrentTask);
                        mSendPhotoBtn.setEnabled(true);
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                showInfoMessage(R.string.error_message_permission_camera);
            }
        }
    }

    private void checkPermissions() {
        if (!PermissionManager.isCameraPermissionGranted(PhotoDocumentActivity.this)) {
            PermissionManager.requestCameraPermissions(PhotoDocumentActivity.this);
        } else {
            launchCamera();
        }
    }

    private void updateView(PhotoDocumentTaskDto currentTask) {
        if (currentTask != null) {
            mDocumentTypeTxt.setText(currentTask.getDocumentType());

            Date photoDate = currentTask.getDatePhoto();
            if (photoDate != null) {
                mDatePhotoTxt.setText(dateFormat.format(photoDate));
            } else {
                mDatePhotoTxt.setText("-");
            }

            Date actualDate = currentTask.getActDatePhoto();
            if (actualDate != null) {
                mActDatePhotoTxt.setText(dateFormat.format(actualDate));
            } else {
                mActDatePhotoTxt.setText("-");
            }

            showPhotoDocumentImage(currentTask);
        }
    }

    private void launchCamera() {
        Intent intent = CameraActivity.newIntent(this, mDocumentTypeTxt.getText().toString(),
                Constants.RECIPIENT_PHOTO_BASE);
        startActivityForResult(intent, Constants.REQUEST_TAKE_PHOTO);
    }

    private void refreshPhoto(String pathPhoto) {
        if (!isFinishing()) {
            Glide.with(this)
                    .load(pathPhoto)
                    .crossFade()
                    //.placeholder(R.drawable.img_camera)
                    .error(R.drawable.img_camera)
                    .into(mPreviewImageView);
        }
    }

    private void sendDocumentItem() {
        if (mCurrentTask != null) {
            showProgress(R.string.send_photo_progress);
            String photoPath = ImageUtils.getBitmapBase64New(mCurrentTask.getPhotoPath(), 100);
            DocumentPhotoRequest request = new DocumentPhotoRequest(mCurrentTask, photoPath);
            SapRequestUtils.sendDriverDocumentPhoto(request, new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    hideProgress();
                }

                @Override
                public void onFinished(String param) {
                    hideProgress();
                    if (param.equals(Constants.SAP_TRUE_FLAG)) {
                        CommonUtils.showToast(getApplicationContext(), R.string.send_photo_successful);
                        //TODO: ipopov 04.04.2017 проверить необходимость проверки обновления фото
                        //GlobalContext.getInstance().getSession().RefreshPhotoDocList();
                        mDataRepository.completePhotoDocumentTask(mCurrentTask);
                        setResult(RESULT_OK);
                        finish();
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

    public void showPhotoDocumentImage(PhotoDocumentTaskDto document) {
        //TODO: ipopov 05.04.2017 перевести на Rx
        if (document != null && !TextUtils.isEmpty(document.getDocumentType())) {
            String documentType = document.getDocumentType();
            String directory = DocumentsUtils.getPhotoDocumentsPath();
            if (!TextUtils.isEmpty(directory)) {
                String filePath = directory + File.separator + documentType + ".jpg";
                File file = new File(filePath);
                if (!file.exists()) {
                    showSimpleProgress();
                    PhotoDocumentRequest request = new PhotoDocumentRequest(documentType);
                    SapRequestUtils.getPhotoDocument(request, new ServiceCallback() {
                        @Override
                        public void onEndedRequest() {
                            hideSimpleProgress();
                        }

                        @Override
                        public void onFinished(String response) {
                            if (!TextUtils.isEmpty(response)) {
                                PhotoDocumentResponse responseModel = new Gson().fromJson(
                                        response, PhotoDocumentResponse.class);
                                DocumentsUtils.saveBase64File(file, responseModel.getPhoto());
                                refreshPhoto(filePath);
                                hideSimpleProgress();
                            } else {
                                //TODO: ipopov 05.04.2017 show error
                            }
                        }

                        @Override
                        public void onFinishedWithException(WSException ex) {
                            hideSimpleProgress();
                        }

                        @Override
                        public void onCancelled() {
                            hideSimpleProgress();
                        }
                    });
                } else {
                    refreshPhoto(filePath);
                }
            }
        }
    }

}
