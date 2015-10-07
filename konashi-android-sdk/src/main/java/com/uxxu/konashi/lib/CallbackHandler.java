package com.uxxu.konashi.lib;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.dispatcher.DispatcherContainer;

import java.util.UUID;

import info.izumin.android.bletia.Bletia;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.BletiaListener;

/**
 * Created by izumin on 9/23/15.
 */
class CallbackHandler implements BletiaListener {

    private final EventEmitter mEmitter;
    private final KonashiManager mManager;
    private final DispatcherContainer mDispatcherContainer;

    public CallbackHandler(KonashiManager manager, EventEmitter emitter, DispatcherContainer dispatcherContainer) {
        mManager = manager;
        mEmitter = emitter;
        mDispatcherContainer = dispatcherContainer;
    }

    @Override
    public void onConnect(Bletia bletia) {
        bletia.discoverServices();
    }

    @Override
    public void onDisconnect(Bletia bletia) {
        mEmitter.emitDisconnect(mManager);
    }

    @Override
    public void onError(BletiaException e) {
        KonashiUtils.log(e.getType().getName());
        mEmitter.emitError(mManager, e);
    }

    @Override
    public void onServicesDiscovered(Bletia bletia, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService service = bletia.getService(KonashiUUID.KONASHI_SERVICE_UUID);
            bletia.enableNotification(service.getCharacteristic(KonashiUUID.PIO_INPUT_NOTIFICATION_UUID), true);
            bletia.enableNotification(service.getCharacteristic(KonashiUUID.UART_RX_NOTIFICATION_UUID), true);
            bletia.enableNotification(service.getCharacteristic(KonashiUUID.HARDWARE_LOW_BAT_NOTIFICATION_UUID), true);
            mEmitter.emitConnect(mManager);
        }
    }

    @Override
    public void onCharacteristicChanged(Bletia bletia, BluetoothGattCharacteristic characteristic) {
        UUID uuid = characteristic.getUuid();

        if (KonashiUUID.PIO_INPUT_NOTIFICATION_UUID.equals(uuid)) {
            mDispatcherContainer.getPioDispatcher().onDone(characteristic);
            mEmitter.emitUpdatePioOutput(mManager, characteristic.getValue()[0]);
        } else if (KonashiUUID.UART_RX_NOTIFICATION_UUID.equals(uuid)) {
            mDispatcherContainer.getUartDispatcher().onDone(characteristic);
            mEmitter.emitUpdateUartRx(mManager, characteristic.getValue());
        } else if (KonashiUUID.HARDWARE_LOW_BAT_NOTIFICATION_UUID.equals(uuid)) {
            mEmitter.emitUpdateBatteryLevel(mManager, KonashiUtils.getBatteryLevel(characteristic));
        }
    }
}
