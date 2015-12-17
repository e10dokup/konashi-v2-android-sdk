package com.uxxu.konashi.lib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.uxxu.konashi.lib.ui.BleDeviceListAdapter;
import com.uxxu.konashi.lib.ui.BleDeviceSelectionDialog;
import com.uxxu.konashi.lib.util.BtUtils;
import com.uxxu.konashi.lib.util.KonashiUtils;

/**
 * Created by izumin on 10/7/15.
 */
class ConnectionHelper implements BleDeviceSelectionDialog.OnBleDeviceSelectListener, BluetoothAdapter.LeScanCallback {
    private final ConnectionHelper self = this;

    private static final long SCAN_PERIOD = 3000;
    private static final String KONAHSHI_DEVICE_NAME = "konashi2";

    private Callback mCallback;

    private Handler mHandler;
    private boolean mIsScanning;
    private String mKonashiName;
    private boolean mIsShowKonashiOnly;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private BleDeviceListAdapter mBleDeviceListAdapter;
    private BleDeviceSelectionDialog mDialog;

    private Activity mActivity;

    public ConnectionHelper(Callback callback, Context context) {
        mCallback = callback;
        mHandler = new Handler(Looper.getMainLooper());

        mBleDeviceListAdapter = new BleDeviceListAdapter(context);

        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        mDialog = new BleDeviceSelectionDialog(mBleDeviceListAdapter, this);
    }

    public void find(Activity activity, boolean isShowKonashiOnly, String name) {
        mActivity = activity;

        if (!BtUtils.isBluetoothSupported() || !BtUtils.isBluetoothEnabled()) {
            BtUtils.startRequestEnableBluetoothActivityForResult(activity);
        }

        mIsShowKonashiOnly = isShowKonashiOnly;

        mHandler.postDelayed(mFindRunnable, SCAN_PERIOD);
        mBleDeviceListAdapter.clearDevices();

        mBluetoothAdapter.stopLeScan(this);
        mBluetoothAdapter.startLeScan(this);
        mIsScanning = true;

        mKonashiName = name;

        if (mKonashiName == null) {
            mDialog.show(mActivity);
        }
    }

    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        KonashiUtils.log("DeviceName: " + device.getName());

        if (device.getName() == null) {
            return;
        }

        if ((mKonashiName != null) && device.getName().equals(mKonashiName)) {
            onSelectBleDevice(device);
            return;
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mIsShowKonashiOnly || device.getName().startsWith(KONAHSHI_DEVICE_NAME)) {
                    mBleDeviceListAdapter.addDevice(device);
                    mBleDeviceListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onSelectBleDevice(BluetoothDevice device) {
        KonashiUtils.log("Selected device: " + device.getName());

        stop();
        mCallback.onSelectBleDevice(device);
    }

    @Override
    public void onCancelSelectingBleDevice() {
        stop();
    }

    private void stop(){
        if (mIsScanning) {
            mHandler.removeCallbacks(mFindRunnable);
            mBluetoothAdapter.stopLeScan(self);
            mIsScanning = false;
        }
    }

    private final Runnable mFindRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsScanning) {
                mBluetoothAdapter.stopLeScan(self);
                mIsScanning = false;
                if (mKonashiName == null) {
                    mDialog.finishFinding();
                }
            }
        }
    };

    public interface Callback {
        void onSelectBleDevice(BluetoothDevice device);
    }
}
