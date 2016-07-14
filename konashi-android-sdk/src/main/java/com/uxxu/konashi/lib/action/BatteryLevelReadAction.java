package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiUUID;

import java.util.UUID;

import info.izumin.android.bletia.action.ReadCharacteristicAction;

/**
 * Created by izumin on 9/23/15.
 */
public class BatteryLevelReadAction extends ReadCharacteristicAction {

    private static final UUID UUID = KonashiUUID.BATTERY_LEVEL_UUID;

    public BatteryLevelReadAction(BluetoothGattService service) {
        super(service, UUID);
    }
}
