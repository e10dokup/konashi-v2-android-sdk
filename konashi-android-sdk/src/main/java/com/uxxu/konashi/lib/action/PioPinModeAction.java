package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.util.PioUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 9/16/15.
 */
public class PioPinModeAction extends KonashiWriteCharacteristicAction {

    private static final UUID UUID = KonashiUUID.PIO_SETTING_UUID;

    private int mPin;
    private int mMode;
    private int mModes;

    private boolean mIsForAll;

    public PioPinModeAction(BluetoothGattService service, int modes) {
        super(service, UUID);
        mIsForAll = true;
        mModes = modes;
    }

    public PioPinModeAction(BluetoothGattService service, int pin, int newMode, int currentModes) {
        super(service, UUID);
        mIsForAll = false;
        mPin = pin;
        mMode = newMode;
        mModes = currentModes;
    }

    @Override
    protected void setValue() {
        if (!mIsForAll) {
            if (mMode == Konashi.OUTPUT) {
                mModes |= (0x01 << mPin);
            } else {
                mModes &= (~(0x01 << mPin) & 0xff);
            }
        }
        getCharacteristic().setValue(new byte[]{(byte) mModes});
    }

    @Override
    protected BletiaErrorType validate() {
        if (mIsForAll) {
            return PioUtils.isValidModes(mModes) ? KonashiErrorType.NO_ERROR : KonashiErrorType.INVALID_MODE;
        } else {
            if (!PioUtils.isValidPin(mPin)) return KonashiErrorType.INVALID_PIN_NUMBER;
            else if (!PioUtils.isValidMode(mMode)) return KonashiErrorType.INVALID_MODE;
            else return KonashiErrorType.NO_ERROR;
        }
    }
}
