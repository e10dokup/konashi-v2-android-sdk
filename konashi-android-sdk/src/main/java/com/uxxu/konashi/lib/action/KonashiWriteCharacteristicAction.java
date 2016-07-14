package com.uxxu.konashi.lib.action;

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

    protected KonashiWriteCharacteristicAction(BluetoothGattService service, UUID uuid) {
        super(service, uuid);
    }

    @Override
    public boolean execute(BluetoothGattWrapper gattWrapper) {
        if (getCharacteristic() == null) {
            rejectIfParamsAreInvalid(KonashiErrorType.UNSUPPORTED_OPERATION);
            return false;
        }
        BletiaErrorType errorType = validate();
        if (errorType == KonashiErrorType.NO_ERROR) {
            setValue();
            return super.execute(gattWrapper);
        } else {
            rejectIfParamsAreInvalid(errorType);
            return false;
        }
    }

    protected void rejectIfParamsAreInvalid(BletiaErrorType errorType) {
        getDeferred().reject(new BletiaException(this, errorType));
    }

    protected abstract void setValue();
    protected abstract BletiaErrorType validate();
}
