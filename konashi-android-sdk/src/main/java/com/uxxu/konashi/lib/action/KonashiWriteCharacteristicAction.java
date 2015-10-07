package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.action.WriteCharacteristicAction;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

/**
 * Created by izumin on 9/17/15.
 */
public abstract class KonashiWriteCharacteristicAction extends WriteCharacteristicAction {

    protected KonashiWriteCharacteristicAction(BluetoothGattCharacteristic characteristic) {
        super(characteristic);
    }

    protected KonashiWriteCharacteristicAction(BluetoothGattService service, UUID uuid) {
        this(service.getCharacteristic(uuid));
    }

    @Override
    public void execute(BluetoothGattWrapper gattWrapper) {
        BletiaErrorType errorType = validate();
        if (errorType == KonashiErrorType.NO_ERROR) {
            setValue();
            super.execute(gattWrapper);
        } else {
            rejectIfParamsAreInvalid(errorType);
        }
    }

    protected void rejectIfParamsAreInvalid(BletiaErrorType errorType) {
        getDeferred().reject(new BletiaException(this, errorType));
    }

    protected abstract void setValue();
    protected abstract BletiaErrorType validate();
}
