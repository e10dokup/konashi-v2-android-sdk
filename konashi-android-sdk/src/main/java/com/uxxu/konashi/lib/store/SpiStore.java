package com.uxxu.konashi.lib.store;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.dispatcher.CharacteristicDispatcher;
import com.uxxu.konashi.lib.dispatcher.SpiStoreUpdater;
import com.uxxu.konashi.lib.util.KonashiUtils;

/**
 * Created by izumin on 11/14/15.
 */
public class SpiStore implements Store {
    public static final String TAG = SpiStore.class.getSimpleName();

    private byte mMode = Konashi.SPI_MODE_DISABLE;
    private byte mEndianness = Konashi.SPI_BIT_ORDER_LITTLE_ENDIAN;
    private byte[] mSpeed = KonashiUtils.int2bytes(Konashi.SPI_SPEED_1M);
    private byte[] mData;

    public SpiStore(CharacteristicDispatcher<SpiStore, SpiStoreUpdater> dispatcher) {
        dispatcher.setStore(this);
    }

    public byte getMode() {
        return mMode;
    }

    public void setMode(byte mode) {
        this.mMode = mode;
    }

    public byte getEndianness() {
        return mEndianness;
    }

    public void setEndianness(byte endianness) {
        mEndianness = endianness;
    }

    public byte[] getSpeed() {
        return mSpeed;
    }

    public void setSpeed(byte[] speed) {
        mSpeed = speed;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] data) {
        mData = data;
    }
}
