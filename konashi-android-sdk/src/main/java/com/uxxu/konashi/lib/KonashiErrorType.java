package com.uxxu.konashi.lib;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 9/16/15.
 */
public enum KonashiErrorType implements BletiaErrorType{
    INVALID_PARAMETER,
    NOT_ENABLED_UART,
    NOT_ENABLED_I2C,
    INVALID_PIN_NUMBER,
    INVALID_MODE,
    INVALID_PULLUP_PARAM,
    INVALID_OUTPUT_PARAM,
    INVALID_DUTY_RATIO,
    DATA_SIZE_TOO_LONG,
    DATA_SIZE_TOO_SHORT,
    INVALID_BAUDRATE,
    INVALID_CONDITION,
    INVALID_ENDIANNESS,
    INVALID_SPEED,
    NO_ERROR;

    @Override
    public int getCode() {
        return -1;
    }

    @Override
    public String getName() {
        return name();
    }
}
