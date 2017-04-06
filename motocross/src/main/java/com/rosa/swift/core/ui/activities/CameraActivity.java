package com.rosa.swift.core.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private final String ROTATE_PHOTO = "rotate_photo";
    private final String CAMERA_ID = "camera_id";
    private final String MODE = "mode";
    private final String FLASH_LIGHT = "flash_light";
    private final String IS_FULL_PATH = "is_full_path";
    private final String PHOTO_NAME = "photo_name";
    private final String DATE_PHOTO = "date_photo";

    //инструмент для отображения картинки с камеры
    private SurfaceView mSurfaceView;

    private ImageButton mChangeCameraBtn;
    private ImageButton mTakePhotoBtn;

    //ссылка на камеру
    private Camera mCamera;

    //вид фотографии
    private String mPhotoName;
    //дата съемки
    private String mDatePhoto = "";

    //флаг включения вспышки;
    private boolean mFlashLight;
    private boolean mAutoFocus;
    //переключатель полного пути или без разширения;
    private boolean mIsFullPath;

    private int mCameraId = 0;
    //режим съемки;
    private int mMode;
    //ключ для определния поворота фотографии
    private int mRotatePhoto;

    public static Intent newIntent(Context packageContext, String photoName, int mode) {
        Intent intent = new Intent(packageContext, CameraActivity.class);
        intent.putExtra(Constants.EXTRA_PHOTO_NAME, photoName);
        intent.putExtra(Constants.EXTRA_CAMERA_MODE, mode);
        return intent;
    }

    //region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mChangeCameraBtn = (ImageButton) findViewById(R.id.btnChangeCamera);
        mTakePhotoBtn = (ImageButton) findViewById(R.id.btnTakePhoto);

        mChangeCameraBtn.setOnClickListener(view -> onChangeCamera());
        mTakePhotoBtn.setOnClickListener(view -> onTakePhoto());

        mPhotoName = getIntent().getStringExtra(Constants.EXTRA_PHOTO_NAME);
        mMode = getIntent().getIntExtra(Constants.EXTRA_CAMERA_MODE, mMode);

        setCameraMode();

        if (mFlashLight) {
            mChangeCameraBtn.setVisibility(View.INVISIBLE);
        }

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    protected void onPause() {
        // GlobalContext.getInstance().updateCupRequestInWork(currentCupRequestItem);
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //endregion

    //region Bundle

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ROTATE_PHOTO, mRotatePhoto);
        outState.putInt(CAMERA_ID, mCameraId);
        outState.putInt(MODE, mMode);
        outState.putBoolean(FLASH_LIGHT, mFlashLight);
        outState.putBoolean(IS_FULL_PATH, mIsFullPath);
        outState.putString(PHOTO_NAME, mPhotoName);
        outState.putString(DATE_PHOTO, mDatePhoto);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRotatePhoto = savedInstanceState.getInt(ROTATE_PHOTO);
        mCameraId = savedInstanceState.getInt(CAMERA_ID);
        mMode = savedInstanceState.getInt(MODE);
        mFlashLight = savedInstanceState.getBoolean(FLASH_LIGHT);
        mIsFullPath = savedInstanceState.getBoolean(IS_FULL_PATH);
        mPhotoName = savedInstanceState.getString(PHOTO_NAME);
        mDatePhoto = savedInstanceState.getString(DATE_PHOTO);
    }

    //endregion

    //region Events

    public void onChangeCamera() {
        mCamera.stopPreview();
        mCamera.release();
        if (mCameraId == 0) mCameraId = 1;
        else mCameraId = 0;
        this.recreate();
    }

    public void onTakePhoto() {
        if (mCamera != null) {
            //определяем, поддерживает ли камера устройства автофокусирование
            List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
            boolean hasAutoFocus = supportedFocusModes != null &&
                    supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
            //если да, то фотографируем только после того, как камера настроит фокус,
            //иначе простой захват изображения
            if (mFlashLight) {
                if (mAutoFocus && hasAutoFocus) {
                    takeFocusedPicture(mCamera);
                } else {
                    takePicture(mCamera);
                }
            } else {
                takePicture(mCamera);
            }
        }
    }

    //endregion

    private void startCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(mCameraId);
            } catch (Exception exception) {
                Toast.makeText(this, "Не удалось подключиться к камере",
                        Toast.LENGTH_SHORT).show();
                Log.e(exception);
            }
        }

        setPreviewSize();
        Camera.Parameters parameters = mCamera.getParameters();

        //region Определение допустимых разрешений экранов
        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        // Сортировка поддерживаемых разрешений
        Collections.sort(allSizes, (a, b) -> a.width * a.height - b.width * b.height);
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            // Выбор разрешения в пределах по ширине (1200-1400) по высоте  (700-1000)
            if ((allSizes.get(i).width >= 1200 && allSizes.get(i).width < 1400) &&
                    (allSizes.get(i).height >= 700 && allSizes.get(i).height < 1000)) {
                size = allSizes.get(i);
                break;
            }
        }

        parameters.setPictureSize(size.width, size.height);

        //задать параметр автовыспышки, если поддерживается
        if (mFlashLight) {
            if (parameters.getSupportedFlashModes() != null && parameters.getSupportedFlashModes()
                    .contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
        }

        //задать параметр автофокуса, если поддерживается
        if (parameters.getSupportedFocusModes() != null &&
                parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        mCamera.setParameters(parameters);
        //endregion

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mCamera.startPreview();
    }

    private void stopCamera() {
        mCamera.release();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setCameraMode() {
        switch (mMode) {
            case Constants.RECIPIENT_CUP_SESSION:
                mAutoFocus = false;
                mFlashLight = true;
                mIsFullPath = false;
                break;
            case Constants.RECIPIENT_PHOTO_BASE:
                mAutoFocus = true;
                mFlashLight = false;
                mIsFullPath = false;
                break;
            case Constants.RECIPIENT_PHOTO_INCIDENT:
                mAutoFocus = true;
                mFlashLight = true;
                mIsFullPath = true;
                break;
        }
    }

    private void setCameraDisplayOrientation() {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 180 * mCameraId;
                mRotatePhoto = 90 + 180 * mCameraId;
                break;
            case Surface.ROTATION_90:
                degrees = 90 + 180 * mCameraId;
                mRotatePhoto = 0;
                break;
            case Surface.ROTATION_180:
                degrees = 180 + 180 * mCameraId;
                mRotatePhoto = 270 + 180 * mCameraId;
                break;
            case Surface.ROTATION_270:
                degrees = 270 + 180 * mCameraId;
                mRotatePhoto = 180;
                break;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int result = (info.orientation - degrees + 360) % 360;
        Camera.Parameters parameters = mCamera.getParameters(); //6

        parameters.set("orientation", "portrait");
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(result);
    }

    private void setPreviewSize() {
        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        // определяем размеры превью камеры
        Camera.Size size = mCamera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.height, size.width);
        }

        //экран будет "втиснут" в превью
        Matrix matrix = new Matrix();
        matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START);
        matrix.invert(matrix);

        // преобразование
        matrix.mapRect(rectPreview);

        // установка размеров surface из получившегося преобразования
        mSurfaceView.getLayoutParams().height = (int) (rectPreview.bottom);
        mSurfaceView.getLayoutParams().width = (int) (rectPreview.right);
    }

    private void sendResult(int resultCode) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(Constants.EXTRA_PHOTO_DATE, mDatePhoto);
        setResult(resultCode, intent);
        finish();
    }

    private void takeFocusedPicture(Camera camera) {
        if (camera != null) {
            camera.autoFocus((success, camera1) -> {
                if (success) {
                    takePicture(camera1);
                }
            });
        }
    }

    private void takePicture(Camera camera) {
        if (camera != null) {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        File pictures = mFlashLight ? DocumentsUtils.getCupPhotosDir() :
                                DocumentsUtils.getPhotoDocumentsDirectory();
                        File photo = mIsFullPath ? new File(mPhotoName) :
                                new File(pictures, mPhotoName + ".jpg");

                        mDatePhoto = DateFormat.format("dd.MM.yyyy", new Date()).toString();

                        FileOutputStream outputStream = new FileOutputStream(photo);

                        Matrix matrix = new Matrix();
                        matrix.postRotate(mRotatePhoto);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), matrix, true);

                        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
                        data = arrayOutputStream.toByteArray();
                        outputStream.write(data);
                        outputStream.close();

                        stopCamera();
                        sendResult(RESULT_OK);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    //region Implementation SurfaceHolder.Callback

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setCameraDisplayOrientation();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    //endregion

}