package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.store.UartStore;
import com.uxxu.konashi.lib.util.UartUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by e10dokup on 2015/09/23
 **/
public class UartWriteAction extends UartAction {
    private static final String TAG = UartWriteAction.class.getSimpleName();
    private final UartWriteAction self = this;

    private static final UUID UUID = KonashiUUID.UART_TX_UUID;

    private byte[] mWriteData = new byte[Konashi.UART_DATA_MAX_LENGTH + 1];

    public UartWriteAction(BluetoothGattService service, String writeDataString, UartStore store) {
        this(service, writeDataString.getBytes(), store);
    }

    public UartWriteAction(BluetoothGattService service, byte[] writeData, UartStore store) {
        super(service, UUID, store, false);
        mWriteData = UartUtils.toFormattedByteArray(writeData);
    }

    @Override
    protected void setValue() {
        getCharacteristic().setValue(mWriteData);
    }

    @Override
    protected BletiaErrorType validate() {
        int length = mWriteData.length;
        if (UartUtils.isLengthTooShort(length)) return KonashiErrorType.DATA_SIZE_TOO_SHORT;
        else if (UartUtils.isLengthTooLong(length)) return KonashiErrorType.DATA_SIZE_TOO_LONG;
        else return KonashiErrorType.NO_ERROR;
    }
}