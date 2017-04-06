package com.giljulio.imagepicker.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.giljulio.imagepicker.model.Image;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Log;

import java.util.List;


public class CameraFragment extends Fragment implements SurfaceHolder.Callback,
        Camera.ShutterCallback, Camera.PictureCallback {

    private static final String TAG = CameraFragment.class.getSimpleName();

    Camera mCamera;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    ImageButton mTakePictureBtn;
    private boolean inPreview;
    private int mCameraId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        mSurfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mTakePictureBtn = (ImageButton) rootView.findViewById(R.id.take_picture);
        mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTakePictureBtn.isEnabled()) {
                    mTakePictureBtn.setEnabled(false);
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            try {
                                mCamera.takePicture(CameraFragment.this, null, CameraFragment.this);
                            } catch (Exception ignored) {
                                Toast.makeText(CameraFragment.this.getActivity(),
                                        "Не удалось сделать снимок", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            int numberOfCameras = Camera.getNumberOfCameras();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCameraId = i;
                    break;
                }
            }
            try {
                mCamera = Camera.open(0);
            } catch (Exception exception) {
                Toast.makeText(this.getActivity(), "Не удалось подключиться к камере",
                        Toast.LENGTH_SHORT).show();
                Log.e(exception);
            }
        }
        startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            mCamera.stopPreview();
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        inPreview = false;

        super.onPause();
    }

    private void startPreview() {
        if (cameraConfigured && mCamera != null) {
            try {
                mCamera.startPreview();
                inPreview = true;
                mTakePictureBtn.setEnabled(true);
            } catch (Exception exception) {
                mTakePictureBtn.setEnabled(false);
                Toast.makeText(this.getActivity(), "Не удалось подключиться к камере", Toast.LENGTH_SHORT).show();
                Log.e(exception);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (inPreview) {
            mCamera.stopPreview();
            cameraConfigured = false;
        }
        initPreview(width, height);
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    private Camera.Size getBestPreviewSize(int width, int height,
            Camera.Parameters parameters) {
        return getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
    }

    private Camera.Size getBestPictureSize(Camera.Parameters parameters) {
        return getOptimalPreviewSize(parameters.getSupportedPictureSizes(), 1600, 1600);
    }

    boolean cameraConfigured;

    private void initPreview(int width, int height) {
        if (mCamera != null && mSurfaceHolder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (Throwable t) {
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);
                Camera.Size pictureSize = getBestPictureSize(parameters);

                if (size != null && pictureSize != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(pictureSize.width,
                            pictureSize.height);
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    mCamera.setParameters(parameters);
                    mOrientationResult = getCameraDisplayOrientation(getActivity(), mCameraId);
                    mCamera.setDisplayOrientation(mOrientationResult);
                    cameraConfigured = true;
                }
            }
        }
    }

    private int mOrientationResult;

    public int getCameraDisplayOrientation(Activity activity,
            int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        mTakePictureBtn.setEnabled(true);
        mCamera.startPreview();
        Bitmap picture = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //rotates the image to portrate
        Matrix matrix = new Matrix();
        matrix.postRotate(mOrientationResult);
        picture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);
        //        picture = ajustContrast(picture);
        picture = toGrayscale(picture);

        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), picture, "", "");
        if (path != null) {
            Uri contentUri = Uri.parse(path);
            Image image = getImageFromContentUri(contentUri);
            ((ImagePickerActivity) getActivity()).addImage(image);
        }
    }

    public Image getImageFromContentUri(Uri contentUri) {

        String[] cols = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.ImageColumns.ORIENTATION
        };
        // can post image
        Cursor cursor = getActivity().getContentResolver().query(contentUri, cols, null, null, null);
        cursor.moveToFirst();
        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
        int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
        return new Image(uri, orientation);
    }

    @Override
    public void onShutter() {

    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);

        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap ajustContrast(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpContrast = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpContrast);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();

        float contrast = 1;
        float brightness = -1;
        cm.set(new float[]{contrast, 0, 0, 0, brightness, 0,
                contrast, 0, 0, brightness, 0, 0, contrast, 0,
                brightness, 0, 0, 0, 1, 0});

        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpContrast;
    }
}

