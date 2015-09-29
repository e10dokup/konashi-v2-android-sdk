package com.uxxu.konashi.lib.js;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kiryu on 9/25/15.
 */
public class UriActivity extends Activity {

    private static final String TAG = UriActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data == null) {
            finish();
            return;
        }
        Log.d(TAG, String.format("onCreate: %s", data));
        Intent intent = new Intent(this, KonashiJsService.class);
        intent.setData(data);
        startService(intent);
        finish();
    }
}
