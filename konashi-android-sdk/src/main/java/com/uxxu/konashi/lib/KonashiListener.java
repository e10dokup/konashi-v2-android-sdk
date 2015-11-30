package com.uxxu.konashi.lib;

import info.izumin.android.bletia.BletiaException;

/**
 * Created by izumin on 9/23/15.
 */
public interface KonashiListener {
    void onConnect(KonashiManager manager);
    void onDisconnect(KonashiManager manager);
    void onError(KonashiManager manager, BletiaException e);
    void onUpdatePioOutput(KonashiManager manager, int value);
    void onUpdateUartRx(KonashiManager manager, byte[] value);
    void onUpdateSpiMiso(KonashiManager manager, byte[] value);
    void onUpdateBatteryLevel(KonashiManager manager, int level);
}
