package com.uxxu.konashi.sample.spisample;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.Arrays;

import info.izumin.android.bletia.BletiaException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final MainActivity self = this;

    private KonashiManager mKonashiManager;

    private EditText mSendEdit;
    private TextView mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_find).setOnClickListener(this);
        mSendEdit = (EditText) findViewById(R.id.edit_send);
        mResultText = (TextView) findViewById(R.id.text_read);

        mKonashiManager = new KonashiManager(getApplicationContext());
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

    private void refreshViews() {
        boolean isReady = mKonashiManager.isReady();
        findViewById(R.id.btn_find).setVisibility(!isReady ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_send).setVisibility(isReady ? View.VISIBLE : View.GONE);
        mSendEdit.setVisibility(isReady ? View.VISIBLE : View.GONE);
        mResultText.setVisibility(isReady ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find:
                mKonashiManager.find(this);
                break;
            case R.id.btn_send:
                mKonashiManager.digitalWrite(Konashi.PIO2, Konashi.LOW)
                        .then(new DonePipe<BluetoothGattCharacteristic, BluetoothGattCharacteristic, BletiaException, Void>() {
                            @Override
                            public Promise<BluetoothGattCharacteristic, BletiaException, Void> pipeDone(BluetoothGattCharacteristic result) {
                                return mKonashiManager.spiWrite(mSendEdit.getText().toString().getBytes());
                            }
                        })
                        .then(new DonePipe<BluetoothGattCharacteristic, BluetoothGattCharacteristic, BletiaException, Void>() {
                            @Override
                            public Promise<BluetoothGattCharacteristic, BletiaException, Void> pipeDone(BluetoothGattCharacteristic result) {
                                return mKonashiManager.digitalWrite(Konashi.PIO2, Konashi.HIGH);
                            }
                        })
                        .then(new DonePipe<BluetoothGattCharacteristic, BluetoothGattCharacteristic, BletiaException, Void>() {
                            @Override
                            public Promise<BluetoothGattCharacteristic, BletiaException, Void> pipeDone(BluetoothGattCharacteristic result) {
                                return mKonashiManager.spiRead();
                            }
                        })
                        .then(new DoneCallback<BluetoothGattCharacteristic>() {
                            @Override
                            public void onDone(BluetoothGattCharacteristic result) {
                                byte data[] = new byte[result.getValue().length];
                                for (int i = 0; i < result.getValue().length; i++) {
                                    data[i] = (byte) ((result.getValue()[i] & 0xff) );
                                }
                                mResultText.setText(Arrays.toString(data));
                            }
                        })
                        .fail(new FailCallback<BletiaException>() {
                            @Override
                            public void onFail(BletiaException result) {
                                Toast.makeText(self, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
        }
    }

    private final KonashiListener mKonashiListener = new KonashiListener() {
        @Override
        public void onConnect(KonashiManager manager) {
            refreshViews();
            mKonashiManager.spiConfig(Konashi.SPI_MODE_ENABLE_CPOL0_CPHA0,
                    Konashi.SPI_BIT_ORDER_LITTLE_ENDIAN, Konashi.SPI_SPEED_1M)
                    .then(new DonePipe<BluetoothGattCharacteristic, BluetoothGattCharacteristic, BletiaException, Void>() {
                        @Override
                        public Promise<BluetoothGattCharacteristic, BletiaException, Void> pipeDone(BluetoothGattCharacteristic result) {
                            return mKonashiManager.pinMode(Konashi.PIO2, Konashi.OUTPUT);
                        }
                    })
                    .then(new DonePipe<BluetoothGattCharacteristic, BluetoothGattCharacteristic, BletiaException, Void>() {
                        @Override
                        public Promise<BluetoothGattCharacteristic, BletiaException, Void> pipeDone(BluetoothGattCharacteristic result) {
                            return mKonashiManager.digitalWrite(Konashi.PIO2, Konashi.HIGH);
                        }
                    })
                    .fail(new FailCallback<BletiaException>() {
                        @Override
                        public void onFail(BletiaException result) {
                            Toast.makeText(self, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
        public void onUpdateSpiMiso(KonashiManager manager, byte[] value) {
            Log.d("onUpdateSpiMiso()", Arrays.toString(value));
        }

        @Override
        public void onUpdateBatteryLevel(KonashiManager manager, int level) {

        }
    };
}
