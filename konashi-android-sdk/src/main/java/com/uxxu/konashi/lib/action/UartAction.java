package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.store.UartStore;

import java.util.UUID;

import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

/**
 * Created by e10dokup on 2015/09/24
 **/
public abstract class UartAction extends KonashiWriteCharacteristicAction{
    private static final String TAG = UartAction.class.getSimpleName();
    private final UartAction self = this;

    private final UartStore mStore;
    private final boolean mIsTypeMode;

    public UartAction(BluetoothGattService service, UUID uuid, UartStore store, boolean isTypeMode) {
        super(service, uuid);
        mStore = store;
        mIsTypeMode = isTypeMode;
    }

    @Override
    public void execute(BluetoothGattWrapper gattWrapper) {
        if(mStore.getMode() == Konashi.UART_ENABLE || mIsTypeMode) {
            super.execute(gattWrapper);
        } else {
            getDeferred().reject(new BletiaException(this, KonashiErrorType.NOT_ENABLED_UART));
        }
    }
}