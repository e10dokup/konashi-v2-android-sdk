package com.uxxu.konashi.lib.dispatcher;

import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.store.SpiStore;

import java.util.UUID;

/**
 * Created by izumin on 11/14/15.
 */
public enum SpiStoreUpdater implements CharacteristicDispatcher.Updater<SpiStore> {
    MODE(KonashiUUID.SPI_CONFIG_UUID) {
        @Override
        public void update(SpiStore store, byte[] value) {
            store.setMode(value[0]);
            store.setEndianness(value[1]);
            store.setSpeed(new byte[] {value[2], value[3]});
        }
    },
    DATA(KonashiUUID.SPI_NOTIFICATION_UUID) {
        @Override
        public void update(SpiStore store, byte[] value) {
            store.setData(value);
        }
    };

    private final UUID mUuid;

    SpiStoreUpdater(UUID uuid) {
        mUuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return mUuid;
    }
}
