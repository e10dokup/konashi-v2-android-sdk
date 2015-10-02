package com.uxxu.konashi.lib.js;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiApiInterface;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiObserver;
import com.uxxu.konashi.lib.listeners.KonashiBaseListener;

/**
 * Created by kiryu on 9/29/15.
 */
public class KonashiJsBridge implements KonashiApiInterface {

    private static final String TAG = KonashiJsBridge.class.getSimpleName();

    private final KonashiManager konashiManager = Konashi.getManager();

    private final Activity activity;

    public KonashiJsBridge(Activity activity) {
        this.activity = activity;
    }

    public void handleEvent(KonashiJsEvent event) {
        Log.d(TAG, "handleEvent: " + event);
        switch (event.getEventName()) {
            case "digitalWrite":
                digitalWrite(event.getIntData("pin"), event.getIntData("value"));
                return;
            case "find":
                Boolean isShowKonashiOnly = event.getBooleanData("isShowKonashiOnly");
                if (isShowKonashiOnly == null) {
                    find(activity);
                } else {
                    find(activity, isShowKonashiOnly);
                }
                return;
            case "pinMode":
                pinMode(event.getIntData("pin"), event.getIntData("mode"));
                return;
        }
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addListener(KonashiBaseListener observer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeListener(KonashiBaseListener observer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeAllListeners() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addObserver(KonashiObserver observer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeObserver(KonashiObserver observer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void removeAllObservers() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void initialize(Context context) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void find(Activity activity) {
        konashiManager.find(activity);
    }

    @Override
    public void find(Activity activity, boolean isShowKonashiOnly) {
        konashiManager.find(activity, isShowKonashiOnly);
    }

    @Override
    public void findWithName(Activity activity, String name) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pinMode(int pin, int mode) {
        konashiManager.pinMode(pin, mode);
    }

    @Override
    public void pinModeAll(int modes) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pinPullup(int pin, int pullup) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pinPullupAll(int pullups) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int digitalRead(int pin) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int digitalReadAll() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void digitalWrite(int pin, int value) {
        konashiManager.digitalWrite(pin, value);
    }

    @Override
    public void digitalWriteAll(int value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pwmMode(int pin, int mode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pwmPeriod(int pin, int period) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pwmDuty(int pin, int duty) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pwmLedDrive(int pin, float dutyRatio) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void pwmLedDrive(int pin, double dutyRatio) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void analogReadRequest(int pin) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int analogRead(int pin) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void analogWrite(int pin, int milliVolt) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void i2cMode(int mode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void i2cStartCondition() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void i2cRestartCondition() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void i2cStopCondition() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void i2cWrite(int length, byte[] data, byte address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void i2cReadRequest(int length, byte address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public byte[] i2cRead(int length) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void uartMode(int mode) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void uartBaudrate(int baudrate) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void uartWrite(byte[] data) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void reset() {
        konashiManager.reset();
    }

    @Override
    public void batteryLevelReadRequest() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getBatteryLevel() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void signalStrengthReadRequest() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getSignalStrength() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
