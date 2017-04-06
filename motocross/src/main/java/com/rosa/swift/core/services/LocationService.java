package com.rosa.swift.core.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.PermissionManager;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.DeliveryLocation;
import com.rosa.swift.core.network.requests.price.TransportLocationsRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.DialogActivity;

import java.util.List;

/**
 * Created by egorov on 19.12.2016.
 */
public class LocationService extends Service {

    private boolean mIsRunning = false;

    private DataRepository mDataRepository;
    private LocationManager mLocationManager;

    private Intent mIntentService;

    /**
     * Обработчик таймера периодической проверки доступности провайдера GPS
     */
    private Handler mTimerHandler;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkProvider();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        return intent;
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mDataRepository = DataRepository.getInstance();
        mTimerHandler = new Handler();

        sendLocations();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsRunning) {
            startLocationUpdate();

            mIntentService = intent;

            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker(getString(R.string.location_notification_message))
                    .setContentTitle(getString(R.string.location_notification_title))
                    .setContentText(getString(R.string.location_notification_message));

            Notification notification;
            if (Build.VERSION.SDK_INT < 16)
                notification = builder.getNotification();
            else
                notification = builder.build();

            startForeground(777, notification);
        }
        return START_STICKY;
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onDestroy() {
        super.onDestroy();
        if (!PermissionManager.isLocationPermissionGranted(this)) {
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
        sendLocations();
    }

    private void showLocation(Location location) {
        //логируем только данные от GPS провайдера
        if (location != null && location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            List<Delivery> currentDeliveryList = mDataRepository.getDeliveries();
            if (currentDeliveryList != null) {
                if (currentDeliveryList.size() > 0) {
                    for (Delivery delivery : currentDeliveryList) {
                        mDataRepository.addDeliveryLocation(new DeliveryLocation(delivery, location));
                    }
                } else {
                    mDataRepository.addDeliveryLocation(new DeliveryLocation(location));
                }
            }
        }

        sendLocations();
    }

    private void checkProvider() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationDialog();
        }
    }

    @SuppressWarnings("MissingPermission")
    void startLocationUpdate() {
        mIsRunning = true;
        mTimerHandler.postDelayed(updateTimerThread, 0);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!PermissionManager.isLocationPermissionGranted(this)) {
                PermissionManager.showApplicationSettingsDialog(this);
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    Constants.GPS_UPDATE_MIN_TIME,
                    Constants.GPS_UPDATE_MIN_DISTANCE,
                    mLocationListener);
        }
    }

    private void showLocationDialog() {
        startActivity(DialogActivity.newIntent(this,
                getString(R.string.location_dialog_title),
                getString(R.string.location_dialog_message),
                getString(R.string.location_dialog_positive),
                new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        ));
    }

    private void sendLocations() {
        //получаем координаты из локальной БД, если их больше 0, то отправляем на сервер,
        //если сервер вернул положительный ответ, то чистим все координаты из БД.
        //сейчас есть потеря данных, с момента получения координат и до ответа сервера в базу может
        //записатся еще одна координата, которая не была передана на сервер.
        //поэтому нужно использовать идентификаторы записей и по ним удалять.
        if (mDataRepository != null) {
            List<DeliveryLocation> locations = mDataRepository.getDeliveryLocations();
            if (locations != null && locations.size() > 0) {
                TransportLocationsRequest request = new TransportLocationsRequest(locations);
                SapRequestUtils.sendTransportLocations(request, new ServiceCallback() {
                    @Override
                    public void onEndedRequest() {
                    }

                    @Override
                    public void onFinished(String evParams) {
                        if (evParams.equals(Constants.SAP_TRUE_FLAG)) {
                            mDataRepository.removeDeliveryLocations();
                        }
                    }

                    @Override
                    public void onFinishedWithException(WSException exception) {
                        Log.e(exception);
                    }

                    @Override
                    public void onCancelled() {
                    }
                });
            }
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            checkProvider();
            mTimerHandler.postDelayed(this, Constants.DELTA_TIME_FOR_LOCATION_SERVICE);
        }
    };

}
