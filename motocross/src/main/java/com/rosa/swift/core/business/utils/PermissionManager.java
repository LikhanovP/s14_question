package com.rosa.swift.core.business.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.rosa.motocross.R;
import com.rosa.swift.core.ui.activities.DialogActivity;

/**
 * Представялет класс-менеджер для управления разрешениями приложения
 */
public class PermissionManager {

    /**
     * Возвращает значение, показывающее, есть ли у приложения все необходимые разрешения
     *
     * @param context
     * @return True - есть все необходимые разрешения, False - одного или нескольких разрешений нет
     */
    public static boolean isPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                : true;
    }

    /**
     * Возвращает значение, показывающее, есть ли у приложения все необходимые разрешения
     * для работы с местоположением
     *
     * @param context
     * @return True - есть все необходимые разрешения, False - одного или нескольких разрешений нет
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED : true;
    }

    /**
     * Возвращает значение, показывающее, есть ли у приложения все необходимые разрешения
     * для работы с камерой
     *
     * @param context
     * @return True - есть все необходимые разрешения, False - одного или нескольких разрешений нет
     */
    public static boolean isCameraPermissionGranted(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED : true;
    }

    /**
     * Запрашивает все необходимые разрешения у пользователя для работы приложения
     *
     * @param activity
     */
    public static void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE
            }, Constants.REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * Запрашивает все необходимые разрешения у пользователя для работы с местоположением
     *
     * @param activity
     */
    public static void requestLocationPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, Constants.REQUEST_PERMISSION_LOCATION_CODE);
        }
    }

    /**
     * Запрашивает все необходимые разрешения у пользователя для работы с камерой
     *
     * @param activity
     */
    public static void requestCameraPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, Constants.REQUEST_PERMISSION_CAMERA_CODE);
        }
    }

    /**
     * Отображает диалог для вызова экрана настроек приложения
     *
     * @param context
     */
    public static void showApplicationSettingsDialog(Context context) {
        context.startActivity(DialogActivity.newIntent(context,
                context.getString(R.string.permission_location_dialog_title),
                context.getString(R.string.permission_location_dialog_message),
                context.getString(R.string.permission_location_dialog_positive),
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null))
        ));
    }

}
