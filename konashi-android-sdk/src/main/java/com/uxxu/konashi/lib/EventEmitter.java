package com.uxxu.konashi.lib;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import info.izumin.android.bletia.BletiaException;

/**
 * Created by izumin on 9/23/15.
 */
class EventEmitter extends ArrayList<KonashiListener> {

    private final EventEmitter self = this;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void emitConnect(final KonashiManager manager) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onConnect(manager);
                }
            }
        });
    }

    public void emitDisconnect(final KonashiManager manager) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onDisconnect(manager);
                }
            }
        });
    }

    public void emitError(final KonashiManager manager, final BletiaException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onError(manager, e);
                }
            }
        });
    }

    public void emitUpdatePioOutput(final KonashiManager manager, final int value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onUpdatePioOutput(manager, value);
                }
            }
        });
    }

    public void emitUpdateUartRx(final KonashiManager manager, final byte[] value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onUpdateUartRx(manager, value);
                }
            }
        });
    }

    public void emitUpdateSpiMiso(final KonashiManager manager, final byte[] value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onUpdateSpiMiso(manager, value);
                }
            }
        });
    }

    public void emitUpdateBatteryLevel(final KonashiManager manager, final int level) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (KonashiListener listener : self) {
                    listener.onUpdateBatteryLevel(manager, level);
                }
            }
        });
    }
}
