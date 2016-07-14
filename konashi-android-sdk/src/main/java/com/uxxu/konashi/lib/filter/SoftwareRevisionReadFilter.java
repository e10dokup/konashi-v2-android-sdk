package com.uxxu.konashi.lib.filter;

import android.bluetooth.BluetoothGattCharacteristic;

import com.uxxu.konashi.lib.util.KonashiUtils;

import org.jdeferred.DoneFilter;

/**
 * Created by e10dokup on 2016/01/13
 **/
public class SoftwareRevisionReadFilter implements DoneFilter<BluetoothGattCharacteristic, String> {

    @Override
    public String filterDone(BluetoothGattCharacteristic result) {
        return KonashiUtils.getSoftwareRevision(result.getValue());
    }
}