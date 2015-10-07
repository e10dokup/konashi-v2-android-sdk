package com.uxxu.konashi.lib.util;

import com.uxxu.konashi.lib.Konashi;

/**
 * Created by e10dokup on 2015/09/22
 **/
public final class UartUtils {
    private UartUtils() {
        throw new AssertionError("constructor of the utility class should not be called");
    }

    public static boolean isValidBaudrate(int baudrate) {
        return baudrate == Konashi.UART_RATE_9K6 || baudrate == Konashi.UART_RATE_19K2 ||
                baudrate == Konashi.UART_RATE_38K4 || baudrate == Konashi.UART_RATE_57K6 ||
                baudrate == Konashi.UART_RATE_76K8 || baudrate == Konashi.UART_RATE_115K2;
    }

    public static boolean isValidMode(int mode) {
        return mode == Konashi.UART_DISABLE || mode == Konashi.UART_ENABLE;
    }

    public static byte[] toFormattedByteArray(String baseString) {
        return toFormattedByteArray(baseString.getBytes());
    }

    public static byte[] toFormattedByteArray(byte[] baseArray) {
        int length = baseArray.length;
        byte[] byteArray = new byte[length + 1];

        byteArray[0] = (byte) length;
        for(int i=0; i<length; i++){
            byteArray[i+1] = baseArray[i];
        }

        return byteArray;
    }

    /**
     * 送信するbyte[]の長さが許容範囲内か判定
     * 先頭1byteは送信するbyte列の長さなのでそれを含めてUART_DATA_MAX_LENGTH + 1 byteに収まるか判定
     * @param length
     * @return
     */
    public static boolean isValidLength(int length) {
        return length > 0 && length <= (Konashi.UART_DATA_MAX_LENGTH + 1);
    }

    public static boolean isLengthTooShort(int length) {
        return length < 0;
    }

    public static boolean isLengthTooLong(int length) {
        return length > (Konashi.UART_DATA_MAX_LENGTH + 1);
    }
}