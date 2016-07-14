package com.uxxu.konashi.lib.test;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.action.KonashiWriteCharacteristicAction;
import com.uxxu.konashi.lib.util.SpiUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.action.WriteCharacteristicAction;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

public class TestKonashiWriteCharacteristicAction extends KonashiWriteCharacteristicAction {

    public TestKonashiWriteCharacteristicAction(BluetoothGattService service, UUID uuid) {
        super(service, uuid);
    }

    @Override
    public boolean execute(BluetoothGattWrapper mWrapper) {
        return super.execute(mWrapper);
    }

    @Override
    public void rejectIfParamsAreInvalid(BletiaErrorType errorType) {
        getDeferred().reject(new BletiaException(this, errorType));
    }

    @Override
    public void setValue() {

    }

    @Override
    public BletiaErrorType validate() {
        return KonashiErrorType.NO_ERROR;
    }
}