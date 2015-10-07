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
public class PioPinPullupAction extends KonashiWriteCharacteristicAction {

    private static final UUID UUID = KonashiUUID.PIO_PULLUP_UUID;

    private int mPin;
    private int mPullup;
    private int mPullups;

    private final boolean mIsForAll;

    public PioPinPullupAction(BluetoothGattService service, int pullups) {
        super(service, UUID);
        mIsForAll = true;
        mPullups = pullups;
    }

    public PioPinPullupAction(BluetoothGattService service, int pin, int newPullup, int currentPullups) {
        super(service, UUID);
        mIsForAll = false;
        mPin = pin;
        mPullup = newPullup;
        mPullups = currentPullups;
    }

    @Override
    protected void setValue() {
        if (!mIsForAll) {
            if (mPullup == Konashi.PULLUP) {
                mPullups |= (0x01 << mPin);
            } else {
                mPullups &= (~(0x01 << mPin) & 0xff);
            }
        }
        getCharacteristic().setValue(new byte[]{(byte) mPullups});
    }

    @Override
    protected BletiaErrorType validate() {
        if (mIsForAll) {
            return PioUtils.isValidPullups(mPullups) ? KonashiErrorType.NO_ERROR : KonashiErrorType.INVALID_PIN_NUMBER;
        } else {
            if (!PioUtils.isValidPin(mPin)) return KonashiErrorType.INVALID_PIN_NUMBER;
            else if (!PioUtils.isValidPullup(mPullup)) return KonashiErrorType.INVALID_PULLUP_PARAM;
            else return KonashiErrorType.NO_ERROR;
        }
    }
}
