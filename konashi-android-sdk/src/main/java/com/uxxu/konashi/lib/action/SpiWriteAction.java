package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.util.SpiUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 11/14/15.
 */
public class SpiWriteAction extends KonashiWriteCharacteristicAction {
    public static final String TAG = SpiWriteAction.class.getSimpleName();

    private static final UUID UUID = KonashiUUID.SPI_DATA_UUID;

    private byte[] mData = new byte[Konashi.SPI_DATA_MAX_LENGTH];

    public SpiWriteAction(BluetoothGattService service, String data) {
        super(service, UUID);
        mData = data.getBytes();
    }

    public SpiWriteAction(BluetoothGattService service, byte[] data) {
        super(service, UUID);
        mData = data;
    }

    @Override
    protected void setValue() {
        getCharacteristic().setValue(mData);
    }

    @Override
    protected BletiaErrorType validate() {
        int length = mData.length;
        if (SpiUtils.isTooLongData(length)) return KonashiErrorType.DATA_SIZE_TOO_LONG;
        if (SpiUtils.isTooShortData(length)) return KonashiErrorType.DATA_SIZE_TOO_SHORT;
        return KonashiErrorType.NO_ERROR;
    }
}
