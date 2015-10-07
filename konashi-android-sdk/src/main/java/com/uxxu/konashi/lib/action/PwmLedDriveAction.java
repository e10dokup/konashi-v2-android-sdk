package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.util.PwmUtils;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 9/18/15.
 */
public class PwmLedDriveAction extends PwmDutyAction {

    private float mDutyRatio;

    private PwmLedDriveAction(BluetoothGattService service, int pin, int duty, int period) {
        super(service, pin, duty, period);
    }

    public PwmLedDriveAction(BluetoothGattService service, int pin, float dutyRatio, int period) {
        this(service, pin, (int) (Konashi.PWM_LED_PERIOD * dutyRatio / 100), period);
        mDutyRatio = dutyRatio;
    }

    @Override
    protected BletiaErrorType validate() {
        if (!PwmUtils.isValidDutyRatio(mDutyRatio)) return KonashiErrorType.INVALID_DUTY_RATIO;
        else return super.validate();
    }
}
