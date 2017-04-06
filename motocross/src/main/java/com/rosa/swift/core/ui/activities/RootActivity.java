package com.rosa.swift.core.ui.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.Message;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.deliveries.repositories.DeliveryTemplateDto;
import com.rosa.swift.core.services.LocationService;
import com.rosa.swift.core.services.QueueService;

import java.util.List;

/**
 * Created by inurlikaev on 19.04.2016.
 */
public class RootActivity extends LogonlessActivity {

    protected volatile boolean suspended;
    protected QueueService mQueueService;


    private ActivityQueueReceiver mQueueReceiver;
    private ServiceConnection mConnection;

    private DataRepository mDataRepository;

    private boolean mBinding = false;

    //region Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataRepository = DataRepository.getInstance();
        mDataRepository.removeSession();
        mDataRepository.updateCustomServer();
        mQueueReceiver = new ActivityQueueReceiver();
        checkBindService();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        suspended = false;
        if (mQueueService != null)
            mQueueService.setShowNotifications(false);  //вырубаем нотификация при foreground активности
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkBindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mQueueService != null)
            mQueueService.setShowNotifications(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Unregistering receiver...");
        unregisterReceiver(mQueueReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQueueService != null) {
            unbindService(mConnection);
            mConnection = null;
        }
    }

    //endregion

    protected void bindService() {

        if (mBinding) return;

        mBinding = true;

        showProgress("Загрузка...");

        Log.i("Starting QueueService...");

        Intent service = new Intent(this, QueueService.class);
        startService(service);

        //noinspection MagicConstant
        bindService(new Intent(this, QueueService.class), mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mQueueService = ((QueueService.LocalBinder) iBinder).getService();
                mQueueService.setShowNotifications(false);
                mBinding = false;
                hideProgress();
                onServiceBinded();
                processMessages();
                //registerReceiver(_receiver, new IntentFilter(QueueService.NEW_MESSAGE));
                Log.i("QueueService bound...");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mQueueService = null;
                Log.i("QueueService unbound...");
            }
        }, Build.VERSION.SDK_INT >= 14 ? BIND_AUTO_CREATE | BIND_ABOVE_CLIENT : BIND_AUTO_CREATE);
    }

    protected void onServiceBinded() {
        //
    }

    private void checkBindService() {
        suspended = false;
        if (mQueueService != null) {

            processMessages();
        } else {
            bindService();
        }
        Log.i("Registering receiver...");
        //начинаме слушать сервис
        registerReceiver(mQueueReceiver, new IntentFilter(Constants.ACTION_NEW_MESSAGE));
    }


    protected void suspend() {
        suspended = true;
    }

    protected void unsuspend() {
        suspended = false;
        if (mQueueService != null) {
            processMessages();
        }
    }

    public void vibrate() {
        try {
            ((Vibrator) this.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        } catch (Exception ignored) {
        }
    }

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
            }
        } catch (Exception e) { //защитим от падения при обработке сообщений
            Log.e(e.getMessage(), e);
        }
    }

    protected void sessionExpired() {
        CommonUtils.ShowErrorMessage(this, R.string.session_expired_error);
        stopService(new Intent(this, LocationService.class));
    }

    protected void onConnectionLost() {
        //todo what to do?
        //CommonUtils.ShowErrorMessage(this, R.string.connection_lost_error);
    }

    protected void updateQueueNum(String number) {
        mDataRepository.setQueueNumber(number);
    }

    private void updateTemplate(String message) {
        try {
            DeliveryTemplateDto template = new DeliveryTemplateDto(message);
            mDataRepository.addOrUpdateDeliveryTemplate(template);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    protected void onStuffLoaded() {

    }

    //обработаем полученные за время простоя сообщения
    private void processMessages() {
        List<Message> messages = mQueueService.getUnreceivedMessages();
        for (Message message : messages) {
            processMessage(message);
        }
    }

    public class ActivityQueueReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Message message = (Message) intent.getSerializableExtra(
                        Constants.EXTRA_NEW_MESSAGE);
                processMessage(message);
            } catch (Exception exception) {
                Log.e(exception);
            }
        }
    }
}
