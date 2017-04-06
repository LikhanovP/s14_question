package com.rosa.swift.core.terminal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by yalang on 08.04.2015.
 */
public class BluetoothHelper {
    private final static String TAG = "BT_HELPER";


    public class NoBluetoothException extends Exception {

    }

    public interface BluetoothListener {
        void onBluetoothEnabled();

        void onDevicePaired(BluetoothDevice device);

        void onDiscoveryFinished(boolean deviceFound);

        void onDiscoveryStarted();

        void onDeviceUnPaired(BluetoothDevice device);
    }

    public enum DevicePairedState {
        DevicePaired,
        DeviceNotPaired
    }

    private boolean mBtReceiverRegistered;
    private Context mContext;
    private final BluetoothAdapter mBtAdapter;
    private BluetoothListener mCallback;

    public BluetoothHelper(Context context, @NonNull BluetoothListener callback) {
        mContext = context;
        mCallback = callback;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    BroadcastReceiver mBtDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    BluetoothHelper.this.onDiscoveryStarted();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    BluetoothHelper.this.finishDiscovery();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothHelper.this.onDeviceFound((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    break;
            }
        }
    };

    BroadcastReceiver mBtPairedStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

            if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                BluetoothHelper.this.onDevicePaired((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            } else if (state == BluetoothDevice.BOND_NONE) {
                BluetoothHelper.this.onDeviceUnPaired((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            }
        }
    };

    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (state == BluetoothAdapter.STATE_ON) {
                BluetoothHelper.this.onBluetoothEnabled();
            }
        }
    };

    private void onDevicePaired(BluetoothDevice btDevice) {
        Log.d(TAG, "Pairing SUCCEED " + btDevice.getAddress());
        mContext.unregisterReceiver(mBtPairedStateReceiver);
        mCallback.onDevicePaired(btDevice);
    }

    private void onDeviceUnPaired(BluetoothDevice btDevice) {
        Log.d(TAG, "Pairing FAILED: " + btDevice.getAddress());
        mContext.unregisterReceiver(mBtPairedStateReceiver);
        mCallback.onDeviceUnPaired(btDevice);
    }

    private void onDeviceFound(BluetoothDevice btDevice) {
        if (mLookForDevice.equals(btDevice.getAddress())) {
            Log.d(TAG, "Device FOUND: " + btDevice.getAddress());
            mDeviceFound = true;
            finishDiscovery();
            pairDevice(btDevice);
        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(mBtPairedStateReceiver, intentFilter);
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean mDeviceFound;
    private String mLookForDevice;
    private boolean mDiscovering;

    public void requestDevicePairing(@NonNull String lookForDeviceAddress) {
        if (mDiscovering) return;
        mLookForDevice = lookForDeviceAddress;
        mDeviceFound = false;
        mBtAdapter.cancelDiscovery();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mBtDiscoveryReceiver, intentFilter);
        mBtAdapter.startDiscovery();
        Log.d(TAG, "Searching for device: " + lookForDeviceAddress);
    }

    private void onDiscoveryStarted() {
        mDiscovering = true;
        mCallback.onDiscoveryStarted();
    }

    private void finishDiscovery() {
        mDiscovering = false;
        mContext.unregisterReceiver(mBtDiscoveryReceiver);
        mBtAdapter.cancelDiscovery();
        mCallback.onDiscoveryFinished(mDeviceFound);
    }

    public boolean checkBluetoothState() throws NoBluetoothException {
        if (mBtAdapter == null) {
            throw new NoBluetoothException();
        } else return mBtAdapter.isEnabled();
    }

    public void enableBluetooth() {
        Log.d(TAG, "Trying to enable Bluetooth");
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBtReceiver, filter);
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mBtReceiverRegistered = true;
        //enableBtIntent.addFlags(268435456);
        this.mContext.startActivity(enableBtIntent);
    }

    private void onBluetoothEnabled() {
        Log.d(TAG, "Bluetooth enabled");
        mContext.unregisterReceiver(mBtReceiver);
        mBtReceiverRegistered = false;
        mCallback.onBluetoothEnabled();
    }

    public DevicePairedState checkDevicePairedState(String btAddress) throws IllegalArgumentException {
        if (btAddress == null)
            throw new IllegalArgumentException("Адрес устройства не может быть null");
        Set<BluetoothDevice> comps = mBtAdapter.getBondedDevices();
        if (comps != null && comps.size() > 0) {
            for (BluetoothDevice comp : comps) {
                if (btAddress.equals(comp.getAddress()))
                    return DevicePairedState.DevicePaired;
            }
        }
        return DevicePairedState.DeviceNotPaired;
    }

    public void onDestroy() {
        if (mBtReceiverRegistered) {
            mContext.unregisterReceiver(mBtReceiver);
            mBtReceiverRegistered = false;
        }
    }

}
