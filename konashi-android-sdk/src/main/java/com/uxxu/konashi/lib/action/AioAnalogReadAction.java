package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;
import android.util.SparseArray;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.util.AioUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.action.ReadCharacteristicAction;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

/**
 * Created by izumin on 9/19/15.
 */
public class AioAnalogReadAction extends ReadCharacteristicAction {

    private static final SparseArray<UUID> sPin2Uuid = new SparseArray<UUID>() {{
        append(Konashi.AIO0, KonashiUUID.ANALOG_READ0_UUID);
        append(Konashi.AIO1, KonashiUUID.ANALOG_READ1_UUID);
        append(Konashi.AIO2, KonashiUUID.ANALOG_READ2_UUID);
    }};

    private final int mPin;

    public AioAnalogReadAction(BluetoothGattService service, int pin) {
        super(service, sPin2Uuid.get(pin, KonashiUUID.ANALOG_READ0_UUID));
        mPin = pin;
    }

    @Override
    public boolean execute(BluetoothGattWrapper gattWrapper) {
        BletiaErrorType errorType = validate();
        if (validate() == KonashiErrorType.NO_ERROR) {
            return super.execute(gattWrapper);
        } else {
            rejectIfParamsAreInvalid(errorType);
            return false;
        }
    }

    protected BletiaErrorType validate() {
        if (AioUtils.isValidPin(mPin)) {
            return KonashiErrorType.NO_ERROR;
        } else {
            return KonashiErrorType.INVALID_PIN_NUMBER;
        }
    }

    private void rejectIfParamsAreInvalid(BletiaErrorType errorType) {
        getDeferred().reject(new BletiaException(this, errorType));
    }
}
