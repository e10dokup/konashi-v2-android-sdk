package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.KonashiUtils;
import com.uxxu.konashi.lib.store.UartStore;
import com.uxxu.konashi.lib.util.UartUtils;

import java.util.UUID;

import info.izumin.android.bletia.BletiaErrorType;

/**
 * Created by e10dokup on 2015/09/22
 **/
public class UartBaudrateAction extends UartAction {
    private static final UUID UUID= KonashiUUID.UART_BAUDRATE_UUID;

    private int mBaudrate;

    public UartBaudrateAction(BluetoothGattService service, int baudrate, UartStore store) {
        super(service, UUID, store, false);
        mBaudrate = baudrate;
    }

    @Override
    protected void setValue() {
        byte[] baseValue = KonashiUtils.int2bytes(mBaudrate);
        getCharacteristic().setValue(new byte[]{baseValue[1], baseValue[0]});
    }

    @Override
    protected BletiaErrorType validate() {
        if (!UartUtils.isValidBaudrate(mBaudrate)) return KonashiErrorType.INVALID_BAUDRATE;
        else return KonashiErrorType.NO_ERROR;
    }
}