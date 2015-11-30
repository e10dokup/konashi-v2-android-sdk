package com.uxxu.konashi.lib.util;

import com.uxxu.konashi.lib.Konashi;

/**
 * Created by izumin on 11/14/15.
 */
public final class SpiUtils {
    public static final String TAG = SpiUtils.class.getSimpleName();

    private SpiUtils() {
        throw new AssertionError("constructor of the utility class should not be called");
    }

    public static boolean isValidMode(int mode) {
        return isEnabled(mode) || (mode == Konashi.SPI_MODE_DISABLE);
    }

    public static boolean isEnabled(int mode) {
        return (mode == Konashi.SPI_MODE_ENABLE_CPOL0_CPHA0)
                || (mode == Konashi.SPI_MODE_ENABLE_CPOL0_CPHA1)
                || (mode == Konashi.SPI_MODE_ENABLE_CPOL1_CPHA0)
                || (mode == Konashi.SPI_MODE_ENABLE_CPOL1_CPHA1);
    }

    public static boolean isValidSpeed(int speed) {
        return (speed == Konashi.SPI_SPEED_200K)
                || (speed == Konashi.SPI_SPEED_500K)
                || (speed == Konashi.SPI_SPEED_1M) || (speed == Konashi.SPI_SPEED_2M)
                || (speed == Konashi.SPI_SPEED_3M) || (speed == Konashi.SPI_SPEED_6M);
    }

    public static boolean isValidEndianness(int endianness) {
        return (endianness == Konashi.SPI_BIT_ORDER_BIG_ENDIAN)
                || (endianness == Konashi.SPI_BIT_ORDER_LITTLE_ENDIAN);
    }

    public static boolean isTooLongData(int length) {
        return Konashi.SPI_DATA_MAX_LENGTH < length;
    }

    public static boolean isTooShortData(int length) {
        return length <= 0;
    }
}