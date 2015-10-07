package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.util.PwmUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 9/18/15.
 */
public class PwmDutyAction extends KonashiWriteCharacteristicAction {

    private static final UUID UUID = KonashiUUID.PWM_DUTY_UUID;

    private int mPin;
    private int mDuty;
    private int mPeriod;

    public PwmDutyAction(BluetoothGattService service, int pin, int duty, int period) {
        super(service, UUID);
        mPin = pin;
        mDuty = duty;
        mPeriod = period;
    }

    @Override
    protected void setValue() {
        byte[] value = new byte[5];
        value[0] = (byte) mPin;
        value[1] = (byte) ((mDuty >> 24) & 0xff);
        value[2] = (byte) ((mDuty >> 16) & 0xff);
        value[3] = (byte) ((mDuty >> 8) & 0xff);
        value[4] = (byte) (mDuty & 0xff);
        getCharacteristic().setValue(value);
    }

    @Override
    protected BletiaErrorType validate() {
        if (!PwmUtils.isValidPin(mPin)) return KonashiErrorType.INVALID_PIN_NUMBER;
        else if (!PwmUtils.isValidDuty(mDuty, mPeriod)) return KonashiErrorType.INVALID_DUTY_RATIO;
        else return KonashiErrorType.NO_ERROR;
    }
}
