package com.uxxu.konashi.lib.util;

import com.uxxu.konashi.lib.Konashi;

/**
 * Created by izumin on 9/19/15.
 */
public final class AioUtils {
    private AioUtils() {
        throw new AssertionError("constructor of the utility class should not be called");
    }

    public static boolean isValidPin(int pin) {
        return (pin == Konashi.AIO0) || (pin == Konashi.AIO1) || (pin == Konashi.AIO2);
    }

    public static int getAnalogValue(int pin, byte[] value) {
        switch (pin) {
            case Konashi.AIO0:
            case Konashi.AIO1:
            case Konashi.AIO2:
                return (value[0] << 8 & 0xff00) | (value[1] & 0xff);
            default:
                throw new IllegalArgumentException();
        }
    }
}