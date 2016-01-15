package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiUUID;

import java.util.UUID;

import info.izumin.android.bletia.action.ReadCharacteristicAction;

/**
 * Created by e10dokup on 2016/01/13
 **/
public class SoftwareRevisionReadAction extends ReadCharacteristicAction {

    private static final UUID UUID = KonashiUUID.SOFTWARE_REVISION_UUID;

    public SoftwareRevisionReadAction(BluetoothGattService service) {
        super(service.getCharacteristic(UUID));
    }
}