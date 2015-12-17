package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiUUID;

import java.util.UUID;

import info.izumin.android.bletia.action.ReadCharacteristicAction;

/**
 * Created by izumin on 11/14/15.
 */
public class SpiReadAction extends ReadCharacteristicAction {
    public static final String TAG = SpiReadAction.class.getSimpleName();

    private static final UUID UUID = KonashiUUID.SPI_DATA_UUID;

    private byte[] mData = new byte[Konashi.SPI_DATA_MAX_LENGTH];

    public SpiReadAction(BluetoothGattService service) {
        super(service.getCharacteristic(UUID));
    }
}
