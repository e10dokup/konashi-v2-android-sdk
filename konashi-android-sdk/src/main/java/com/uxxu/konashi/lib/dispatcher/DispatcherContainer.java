package com.uxxu.konashi.lib.dispatcher;

import com.uxxu.konashi.lib.store.AioStore;
import com.uxxu.konashi.lib.store.I2cStore;
import com.uxxu.konashi.lib.store.PioStore;
import com.uxxu.konashi.lib.store.PwmStore;
import com.uxxu.konashi.lib.store.SpiStore;
import com.uxxu.konashi.lib.store.UartStore;

/**
 * Created by izumin on 9/23/15.
 */
public class DispatcherContainer {
    private CharacteristicDispatcher<PioStore, PioStoreUpdater> mPioDispatcher;
    private CharacteristicDispatcher<PwmStore, PwmStoreUpdater> mPwmDispatcher;
    private CharacteristicDispatcher<AioStore, AioStoreUpdater> mAioDispatcher;
    private CharacteristicDispatcher<I2cStore, I2cStoreUpdater> mI2cDispatcher;
    private CharacteristicDispatcher<UartStore, UartStoreUpdater> mUartDispatcher;
    private CharacteristicDispatcher<SpiStore, SpiStoreUpdater> mSpiDispatcher;

    public DispatcherContainer() {
        mPioDispatcher = new CharacteristicDispatcher<>(PioStoreUpdater.class);
        mPwmDispatcher = new CharacteristicDispatcher<>(PwmStoreUpdater.class);
        mAioDispatcher = new CharacteristicDispatcher<>(AioStoreUpdater.class);
        mUartDispatcher = new CharacteristicDispatcher<>(UartStoreUpdater.class);
        mI2cDispatcher = new CharacteristicDispatcher<>(I2cStoreUpdater.class);
        mSpiDispatcher = new CharacteristicDispatcher<>(SpiStoreUpdater.class);
    }

    public CharacteristicDispatcher<PioStore, PioStoreUpdater> getPioDispatcher() {
        return mPioDispatcher;
    }

    public CharacteristicDispatcher<PwmStore, PwmStoreUpdater> getPwmDispatcher() {
        return mPwmDispatcher;
    }

    public CharacteristicDispatcher<AioStore, AioStoreUpdater> getAioDispatcher() {
        return mAioDispatcher;
    }

    public CharacteristicDispatcher<UartStore, UartStoreUpdater> getUartDispatcher() {
        return mUartDispatcher;
    }

    public CharacteristicDispatcher<I2cStore, I2cStoreUpdater> getI2cDispatcher() {
        return mI2cDispatcher;
    }

    public CharacteristicDispatcher<SpiStore, SpiStoreUpdater> getSpiDispatcher() {
        return mSpiDispatcher;
    }
}
