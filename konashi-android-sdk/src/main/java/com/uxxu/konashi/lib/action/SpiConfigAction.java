package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.store.SpiStore;
import com.uxxu.konashi.lib.util.KonashiUtils;
import com.uxxu.konashi.lib.util.SpiUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by izumin on 11/14/15.
 */
public class SpiConfigAction extends KonashiWriteCharacteristicAction {
    public static final String TAG = SpiConfigAction.class.getSimpleName();

    private static final UUID UUID = KonashiUUID.SPI_CONFIG_UUID;
    private final SpiStore mStore;

    private int mMode;
    private int mEndianness;
    private int mSpeed;

    public SpiConfigAction(BluetoothGattService service, int mode, int endianness, int speed, SpiStore store) {
        super(service, UUID);
        mMode = mode;
        mEndianness = endianness;
        mSpeed = speed;
        mStore = store;
    }

    @Override
    protected void setValue() {
        byte speedInBytes[] = KonashiUtils.int2bytes(mSpeed);
        getCharacteristic().setValue(new byte[] {
                (byte) mMode, (byte) mEndianness, speedInBytes[1], speedInBytes[0]
        });
    }

    @Override
    protected BletiaErrorType validate() {
        if (!SpiUtils.isValidMode(mMode)) return KonashiErrorType.INVALID_MODE;
        if (!SpiUtils.isValidEndianness(mEndianness)) return KonashiErrorType.INVALID_ENDIANNESS;
        if (!SpiUtils.isValidSpeed(mSpeed)) return KonashiErrorType.INVALID_SPEED;
        return KonashiErrorType.NO_ERROR;
    }
}
