package com.rosa.swift.core.terminal;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclutilities.PclUtilities;
import com.ru.ingenico.jarcus2.OperationList;
import com.ru.ingenico.jarcus2.ResponseCode;
import com.ru.ingenico.jarcus2.TeliumSWType;
import com.ru.ingenico.jarcus2.UserAuth;
import com.ru.ingenico.jarcus2.android.ArcusService;
import com.ru.ingenico.jarcus2.exception.OperationNotAvailableException;
import com.ru.ingenico.jarcus2.exception.TerminalNotConnectedException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yalang on 07.04.2015.
 */
public class Terminal {
    private final static String TAG = "POS_TERMINAL";
    private static final String PACKAGE_NAME = "com.rosa.motocross";
    private static final String PCL_FILE_NAME = "pairing_addr.txt";
    private static final long WAIT_TO_CONNECT_MILISECONDS = 10000L;
    private static final int WAIT_ARCUS_TO_CONNECT_SECONDS = 10;
    private boolean mBtReceiverRegistered;
    private boolean mBtDeviceConnected;
    private boolean mConnecting;
    private UserAuth mUserAuth = new UserAuth();
    private Context mContext;
    private String mBtDeviceAddress;
    private BluetoothHelper mBtHelper;
    private OperationList operationList;
    private POSOperationListener mOpListener;

    private void onDeviceConnected(BluetoothDevice device) {
        if (!device.getAddress().equals(mBtDeviceAddress)) return;
        if (mBtDeviceConnected) return;
        Log.d(TAG, "Device connected: " + device.getAddress());
        mBtDeviceConnected = true;
        mConnecting = false;
        if (mConnectionTimer != null) {
            mConnectionTimer.cancel();
            mConnectionTimer = null;
        }
        doOperation();
    }

    private void onDeviceDisconnected(BluetoothDevice device) {
        if (!device.getAddress().equals(mBtDeviceAddress)) return;
        Log.d(TAG, "Device disconnected: " + device.getAddress());
        mBtDeviceConnected = false;
        mConnecting = false;
        terminalDisconnect();
    }

    private boolean allIsReady() {
        return mBtDeviceConnected && (mPclService != null) && (mArcusService != null);
    }

    public interface POSTerminalListener {
        void onTerminalConnected();

        void onTerminalConnectingException(Exception e);
    }

    public interface POSOperationListener {
        void onOperationComplete(final ResponseCode lastRc);

        void onOperationBegin();

        void onOperationCompleteWithException(final Exception e);

        void onStoreRC(final ResponseCode rc);
    }

    public Terminal(@NonNull Context context, @NonNull String bluetoothDeviceAddress) {
        this.mContext = context;
        this.mBtDeviceAddress = bluetoothDeviceAddress;
        BluetoothHelper.BluetoothListener mBtListener = new BluetoothHelper.BluetoothListener() {
            @Override
            public void onBluetoothEnabled() {
                doOperation();
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {
                if (!device.getAddress().equals(mBtDeviceAddress)) return;
                Log.d(TAG, "Device paired: " + device.getAddress());
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000L);
                            doOperation();
                        } catch (Exception ignored) {
                            if (mOpListener != null)
                                mOpListener.onOperationCompleteWithException(new TerminalNotConnectedException());
                        }
                    }
                });
                t.start();
            }

            @Override
            public void onDiscoveryFinished(boolean deviceFound) {
                if (mOpListener != null && !deviceFound)
                    mOpListener.onOperationCompleteWithException(new DeviceNotFoundException());
            }

            @Override
            public void onDiscoveryStarted() {
                Toast.makeText(mContext, "Начинаем поиск", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeviceUnPaired(BluetoothDevice device) {
                Toast.makeText(mContext, "Устройство не подключено", Toast.LENGTH_LONG).show();
            }
        };
        mBtHelper = new BluetoothHelper(context, mBtListener);
        operationList = OperationList.getInstance(TeliumSWType.UNIPAY); //in order to ArcusService works with the right operation list
    }

    public void testDeviceConnection(@NonNull POSOperationListener opListener) {
        if (mInOperation) {
            mOpListener.onOperationCompleteWithException(new TerminalIsBusyException());
            return;
        }
        mOpListener = opListener;
        mUserAuth = new UserAuth();
        mUserAuth.operType = operationList.getOperationById(203).getId();
        doOperation();
    }

    public void pay(@NonNull String amount, @NonNull POSOperationListener opListener) {
        if (mInOperation) {
            mOpListener.onOperationCompleteWithException(new TerminalIsBusyException());
            return;
        }
        mOpListener = opListener;
        mUserAuth = new UserAuth();
        mUserAuth.operType = operationList.getOperationById(1).getId();
        mUserAuth.setAmount(amount);
        doOperation();
    }

    public void connect(@NonNull final POSTerminalListener posTerminalListener) {
        mOpListener = new POSOperationListener() {
            @Override
            public void onOperationComplete(ResponseCode lastRc) {
                posTerminalListener.onTerminalConnected();
            }

            @Override
            public void onOperationBegin() {

            }

            @Override
            public void onOperationCompleteWithException(Exception e) {
                posTerminalListener.onTerminalConnectingException(e);
            }

            @Override
            public void onStoreRC(ResponseCode rc) {

            }
        };
        mUserAuth = new UserAuth();
        mUserAuth.operType = -1;
        doOperation();
    }

    private BroadcastReceiver mOpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ArcusService.ACTION_BEGIN_TRANSACTION:
                    Terminal.this.onTransactionBegin();
                    break;
                case ArcusService.ACTION_END_TRANSACTION:
                    Terminal.this.onTransactionEnd();
                    break;
                case ArcusService.ACTION_STORERC:
                    ResponseCode rc = (ResponseCode) intent.getSerializableExtra(ArcusService.EXTRA_RESPONSE_CODE);
                    Terminal.this.onStoreRC(rc);
                    break;
            }
        }
    };

    private ResponseCode mLastRc;

    private void onStoreRC(ResponseCode rc) {
        if (rc != null)
            Log.d(TAG, "RESPONSE_CODE: " + rc.originalResponseCode() + " " + rc.cashregisterResponseCode());
        mOpListener.onStoreRC(rc);
        mLastRc = rc;
    }

    private void onTransactionEnd() {
        Log.d(TAG, "Transaction ENDED " + Integer.toString(mUserAuth.operType));
        mInOperation = false;
        mOpListener.onOperationComplete(mLastRc);
        mContext.unregisterReceiver(mOpReceiver);
    }

    private void onTransactionBegin() {
        Log.d(TAG, "Transaction BEGAN " + Integer.toString(mUserAuth.operType));
        mOpListener.onOperationBegin();
    }

    private boolean mInOperation;

    private void doOperation() {
        Log.d(TAG, "Trying to start transaction " + Integer.toString(mUserAuth.operType));
        try {
            if (mBtHelper.checkBluetoothState()) { //Bluetooth is on?
                if (!allIsReady()) { //YES - check if device is already connected and services are running
                    if (mBtHelper.checkDevicePairedState(mBtDeviceAddress) == BluetoothHelper.DevicePairedState.DevicePaired) {
                        terminalConnect();
                    } else {
                        mBtHelper.requestDevicePairing(mBtDeviceAddress);
                    }
                } else {
                    startOperationThread();
                }
            } else { //NO - make it on
                mBtHelper.enableBluetooth();
            }
        } catch (BluetoothHelper.NoBluetoothException e) {
            mOpListener.onOperationCompleteWithException(e);
        }
    }

    private void startOperationThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while (!mArcusService.isTerminalConnected()) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            mOpListener.onOperationCompleteWithException(e);
                            return;
                        }
                        i++;
                        if (i == WAIT_ARCUS_TO_CONNECT_SECONDS) break;
                    }
                    try {
                        Thread.sleep(2000L); //подождем еще пару секунд до появления надписи ГОТОВ К РАБОТЕ
                    } catch (InterruptedException e) {
                        mOpListener.onOperationCompleteWithException(e);
                        return;
                    }
                    if (mUserAuth.operType != -1) {
                        IntentFilter iFilter = new IntentFilter();
                        iFilter.addAction(ArcusService.ACTION_END_TRANSACTION);
                        iFilter.addAction(ArcusService.ACTION_BEGIN_TRANSACTION);
                        iFilter.addAction(ArcusService.ACTION_STORERC);
                        mContext.registerReceiver(mOpReceiver, iFilter);
                        mInOperation = true;
                        Log.d(TAG, "Start transaction " + Integer.toString(mUserAuth.operType));
                        try {
                            mArcusService.startTransaction(mUserAuth);
                        } catch (TerminalNotConnectedException | OperationNotAvailableException e) {
                            mInOperation = false;
                            mContext.unregisterReceiver(mOpReceiver);
                            mOpListener.onOperationCompleteWithException(new TerminalNotConnectedException());
                        }
                    } else {
                        if (mArcusService.isTerminalConnected())
                            mOpListener.onOperationComplete(null);
                        else
                            mOpListener.onOperationCompleteWithException(new TerminalNotConnectedException());
                    }
                } catch (NullPointerException ignored) {
                    mOpListener.onOperationCompleteWithException(new TerminalNotConnectedException());
                }
            }
        });
        t.start();
    }

    public void interruptOperation() {
        if (mInOperation) {
            Log.d(TAG, "Trying to interrupt transaction " + Integer.toString(mUserAuth.operType));
            if (mArcusService != null)
                mArcusService.interruptTransaction();
            mInOperation = false;
            mContext.unregisterReceiver(mOpReceiver);
        }
    }

    BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Terminal.this.onDeviceConnected((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Terminal.this.onDeviceDisconnected((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    break;
            }
        }
    };
    Timer mConnectionTimer;

    private void terminalConnect() {
        if (mConnecting) return;
        Log.d(TAG, "Connecting to terminal...");
        mConnectionTimer = new Timer();
        mConnectionTimer.schedule(new TimerTask() { //10 seconds waiting for device to connect
            @Override
            public void run() {
                mOpListener.onOperationCompleteWithException(new TerminalNotConnectedException());
            }
        }, WAIT_TO_CONNECT_MILISECONDS);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mContext.registerReceiver(mBtReceiver, intentFilter);
        mBtReceiverRegistered = true;
        mConnecting = true;
        activateDevice();
        startPclService();
        startArcusService();
    }

    private void terminalDisconnect() {
        Log.d(TAG, "Disconnecting from terminal...");

        if (mBtReceiverRegistered) {
            mBtReceiverRegistered = false;
            mContext.unregisterReceiver(mBtReceiver);
        }

        boolean inOpBak = mInOperation;
        interruptOperation();

        if (mArcusService != null)
            mArcusService.onTerminalDisconnected();
        else
            Log.d(TAG, "ArcusService NULL");

        if (mPclConnection != null)
            mContext.unbindService(mPclConnection);
        else
            Log.d(TAG, "PclConnection NULL");
        if (mArcusConnection != null)
            mContext.unbindService(mArcusConnection);
        else
            Log.d(TAG, "ArcusConnection NULL");

        if (mContext.stopService(new Intent(mContext, ArcusService.class))) {
            Log.d(TAG, "ArcusService STOPPED");
        } else {
            Log.w(TAG, "ArcusService STOP FAILED");
        }
        if (mContext.stopService(new Intent(mContext, PclService.class))) {
            Log.d(TAG, "PclService STOPPED");
        } else {
            Log.w(TAG, "PclService STOP FAILED");
        }

        mArcusService = null;
        mPclConnection = null;
        mArcusConnection = null;
        mPclService = null;

        mBtDeviceConnected = false;
        mInOperation = false;
        mConnecting = false;

        if (inOpBak)
            mOpListener.onOperationCompleteWithException(new TerminalNotConnectedException());
    }

    private void activateDevice() {
        checkPayFile();
        PclUtilities pclUtilities = new PclUtilities(mContext, PACKAGE_NAME, PCL_FILE_NAME);
        pclUtilities.ActivateCompanion(mBtDeviceAddress);
    }

    private ServiceConnection mPclConnection;
    private ServiceConnection mArcusConnection;
    private ArcusService mArcusService;

    private void startArcusService() {
        if (mArcusService != null) return;

        final Intent i = new Intent(mContext, ArcusService.class);

        i.putExtra(ArcusService.EXTRA_DIALOGS_ENABLED, false);
        i.putExtra(ArcusService.EXTRA_DIALOG_TIMEOUT, 120000L);
        i.putExtra(ArcusService.EXTRA_PORT, 9301);

        mContext.startService(i);

        mContext.bindService(i, mArcusConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "ArcusService CONNECTED");
                mArcusService = ((ArcusService.ArcusServiceBinder) service).getService();
                mArcusService.enableDebug(false);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "ArcusService DISCONNECTED");
                mArcusConnection = null;
                mArcusService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private PclService mPclService;

    private void startPclService() {
        if (mPclService != null) return;

        Intent i = new Intent(mContext, PclService.class);
        i.putExtra("PACKAGE_NAME", PACKAGE_NAME);
        i.putExtra("FILE_NAME", PCL_FILE_NAME);
        mContext.startService(i);

        mContext.bindService(i, mPclConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "PCLService CONNECTED");
                mPclService = ((PclService.LocalBinder) service).getService();
                mPclService.addDynamicBridge(9301, 1);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "PCLService DISCONNECTED");
                mPclConnection = null;
                mPclService = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void checkPayFile() {
        try {
            FileOutputStream fileOutputStream = this.mContext.createPackageContext(PACKAGE_NAME, 0).openFileOutput(PCL_FILE_NAME, 0);
            fileOutputStream.close();
        } catch (IOException | PackageManager.NameNotFoundException ignored) {

        }
    }

    public void disconnect() {
        try {
            terminalDisconnect();
        } catch (NullPointerException e) {
            Log.d(TAG, "Already destoyed");
        }
    }

    public class TerminalIsBusyException extends Exception {
    }

    public class DeviceNotFoundException extends Exception {
    }
}
















