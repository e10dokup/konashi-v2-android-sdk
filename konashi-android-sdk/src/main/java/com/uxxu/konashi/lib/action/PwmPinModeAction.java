package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.util.PwmUtils;

import java.util.UUID;

/**
 * Created by izumin on 9/18/15.
 */
public class PwmPinModeAction extends KonashiWriteCharacteristicAction {

    private static final UUID UUID = KonashiUUID.PWM_CONFIG_UUID;

    private int mPin;
    private int mMode;
    private int mModes;

    public PwmPinModeAction(BluetoothGattService service, int pin, int newMode, int currentModes) {
        super(service, UUID);
        mPin = pin;
        mMode = newMode;
        mModes = currentModes;
    }

    @Override
    protected void setValue() {
        if (mMode == Konashi.PWM_ENABLE || mMode == Konashi.PWM_ENABLE_LED_MODE) {
            mModes |= 0x01 << mPin;
        } else {
            mModes &= ~(0x01 << mPin) & 0xFF;
        }
        getCharacteristic().setValue(new byte[]{(byte) mModes});
    }

    @Override
    protected boolean hasValidParams() {
        return PwmUtils.isValidPin(mPin) && PwmUtils.isValidMode(mMode);
    }
}
