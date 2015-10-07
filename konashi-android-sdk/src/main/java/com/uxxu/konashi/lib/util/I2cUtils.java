package com.uxxu.konashi.lib.util;

import com.uxxu.konashi.lib.Konashi;

/**
 * Created by izumin on 9/21/15.
 */
public final class I2cUtils {
    private I2cUtils() {
        throw new AssertionError("constructor of the utility class should not be called");
    }

    public static boolean isValidMode(int mode) {
        return (mode == Konashi.I2C_DISABLE) || (mode == Konashi.I2C_ENABLE)
                || (mode == Konashi.I2C_ENABLE_100K) || (mode == Konashi.I2C_ENABLE_400K);
    }

    public static boolean isValidCondition(int condition) {
        return (condition == Konashi.I2C_START_CONDITION) || (condition == Konashi.I2C_STOP_CONDITION)
                || (condition == Konashi.I2C_RESTART_CONDITION);
    }

    public static boolean isValidDataLength(int length) {
        return (length > 0) && (length <= Konashi.I2C_DATA_MAX_LENGTH);
    }

    public static boolean isTooShortDataLength(int length) {
        return length <= 0;
    }

    public static boolean isTooLongDataLength(int length) {
        return length > Konashi.I2C_DATA_MAX_LENGTH;
    }
}