package com.uxxu.konashi.lib.js;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;

/**
 * Created by kiryu on 9/25/15.
 */
public class KonashiJsService extends Service {

    private static final String TAG = KonashiJsService.class.getSimpleName();

    public static ServiceConnection bindServiceTo(final Activity activity) {
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                KonashiJsService service = ((KonashiJsBinder) iBinder).getService();
                service.setBridge(new KonashiJsBridge(activity));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        activity.bindService(
                new Intent(activity, KonashiJsService.class),
                connection,
                Context.BIND_AUTO_CREATE);
        return connection;
    }

    private KonashiJsBridge bridge;

    private IBinder binder = new KonashiJsBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent);
        KonashiJsEvent event = null;
        try {
            event = KonashiJsEvent.fromUri(intent.getData());
        } catch (JSONException e) {
            Log.d(TAG, "Failed to parse event data", e);
        }
        if (bridge != null && event != null) {
            bridge.handleEvent(event);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void setBridge(KonashiJsBridge bridge) {
        this.bridge = bridge;
    }

    public class KonashiJsBinder extends Binder {
        KonashiJsService getService() {
            return KonashiJsService.this;
        }
    }
}
