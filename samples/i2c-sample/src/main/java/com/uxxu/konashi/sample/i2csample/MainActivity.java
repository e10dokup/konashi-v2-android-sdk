package com.uxxu.konashi.sample.i2csample;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiListener;
import com.uxxu.konashi.lib.KonashiManager;

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import info.izumin.android.bletia.BletiaException;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final MainActivity self = this;

    private static final byte I2C_ADDRESS = 0x01f;

    private KonashiManager mKonashiManager;

    private EditText mSendEdit;
    private TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_read).setOnClickListener(this);
        findViewById(R.id.btn_find).setOnClickListener(this);
        mSendEdit = (EditText) findViewById(R.id.edit_send);
        mResultText = (TextView) findViewById(R.id.text_read);

        mKonashiManager = new KonashiManager(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKonashiManager.addListener(mKonashiListener);
        refreshViews();
    }

    @Override
    protected void onPause() {
        mKonashiManager.removeListener(mKonashiListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mKonashiManager.isConnected()){
                    mKonashiManager.reset()
                            .then(new DoneCallback<BluetoothGattCharacteristic>() {
                                @Override
                                public void onDone(BluetoothGattCharacteristic result) {
                                    mKonashiManager.disconnect();
                                }
                            });
                }
            }
        }).start();
        super.onDestroy();
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void findKonashi() {
        mKonashiManager.find(this);
    }

    private void refreshViews() {
        boolean isReady = mKonashiManager.isReady();
        findViewById(R.id.btn_find).setVisibility(!isReady ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_send).setVisibility(isReady ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_read).setVisibility(isReady ? View.VISIBLE : View.GONE);
        mSendEdit.setVisibility(isReady ? View.VISIBLE : View.GONE);
        mResultText.setVisibility(isReady ? View.VISIBLE : View.GONE);
    }

    private void sendData() {

        byte[] value = mSendEdit.getText().toString().trim().getBytes();
        mKonashiManager.i2cMode(Konashi.I2C_ENABLE_100K)
                .then(mKonashiManager.<BluetoothGattCharacteristic>i2cStartConditionPipe())
                .then(mKonashiManager.<BluetoothGattCharacteristic>i2cWritePipe(value.length, value, I2C_ADDRESS))
                .then(mKonashiManager.<BluetoothGattCharacteristic>i2cStopConditionPipe())
                .fail(new FailCallback<BletiaException>() {
                    @Override
                    public void onFail(BletiaException result) {
                        Toast.makeText(self, result.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void readData() {
        mKonashiManager.i2cMode(Konashi.I2C_ENABLE_100K)
                .then(mKonashiManager.<BluetoothGattCharacteristic>i2cStartConditionPipe())
                .then(mKonashiManager.<BluetoothGattCharacteristic>i2cReadPipe(Konashi.I2C_DATA_MAX_LENGTH, I2C_ADDRESS))
                .then(new DonePipe<byte[], BluetoothGattCharacteristic, BletiaException, Void>() {
                    @Override
                    public Promise<BluetoothGattCharacteristic, BletiaException, Void> pipeDone(final byte[] result) {
                        StringBuilder builder = new StringBuilder();
                        for (byte b : result) {
                            builder.append(b).append(",");
                        }
                        mResultText.setText(builder.toString().substring(0, builder.length() - 1));
                        return mKonashiManager.i2cStopCondition();
                    }
                })
                .fail(new FailCallback<BletiaException>() {
                    @Override
                    public void onFail(BletiaException result) {
                        Toast.makeText(self, result.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find:
                MainActivityPermissionsDispatcher.findKonashiWithCheck(this);
                break;
            case R.id.btn_send:
                sendData();
                break;
            case R.id.btn_read:
                readData();
                break;
        }
    }
    private final KonashiListener mKonashiListener = new KonashiListener() {
        @Override
        public void onConnect(KonashiManager manager) {
            refreshViews();
        }

        @Override
        public void onDisconnect(KonashiManager manager) {
            refreshViews();
        }

        @Override
        public void onError(KonashiManager manager, BletiaException e) {

        }

        @Override
        public void onUpdatePioOutput(KonashiManager manager, int value) {

        }

        @Override
        public void onUpdateUartRx(KonashiManager manager, byte[] value) {

        }

        @Override
        public void onUpdateBatteryLevel(KonashiManager manager, int level) {

        }

        @Override
        public void onUpdateSpiMiso(KonashiManager manager, byte[] value) {

        }
    };
}