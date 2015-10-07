package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.util.PwmUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 9/18/15.
 */
public class PwmPeriodAction extends KonashiWriteCharacteristicAction {

    private static final UUID UUID = KonashiUUID.PWM_PARAM_UUID;

    private int mPin;
    private int mPeriod;
    private int mDuty;

    public PwmPeriodAction(BluetoothGattService service, int pin, int period, int duty) {
        super(service, UUID);
        mPin = pin;
        mPeriod = period;
        mDuty = duty;
    }

    @Override
    protected void setValue() {
        byte[] value = new byte[5];
        value[0] = (byte) mPin;
        value[1] = (byte) ((mPeriod >> 24) & 0xff);
        value[2] = (byte) ((mPeriod >> 16) & 0xff);
        value[3] = (byte) ((mPeriod >> 8) & 0xff);
        value[4] = (byte) (mPeriod & 0xff);
        getCharacteristic().setValue(value);
    }

    @Override
    protected BletiaErrorType validate() {
        if (!PwmUtils.isValidPin(mPin)) return KonashiErrorType.INVALID_PIN_NUMBER;
        else if (!PwmUtils.isValidPeriod(mPeriod, mDuty)) return KonashiErrorType.INVALID_MODE;
        else return KonashiErrorType.NO_ERROR;
    }
}
