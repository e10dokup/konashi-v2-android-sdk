package com.uxxu.konashi.sample.uartsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uxxu.konashi.lib.KonashiErrorReason;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.listeners.KonashiConnectionListener;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    public static final String TAG = MainActivity.class.getSimpleName();
    public final MainActivity self = this;

    private Menu mMenu;
    private TextView mTextNoConnection;

    private KonashiManager mKonashiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextNoConnection = (TextView) findViewById(R.id.text_no_connection);
        mKonashiManager = new KonashiManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKonashiManager.addListener(mKonashiConnectionListener);
        mKonashiManager.initialize(this);
    }

    @Override
    protected void onPause() {
        mKonashiManager.removeListener(mKonashiConnectionListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mKonashiManager != null) {
            mKonashiManager.reset();
            mKonashiManager.disconnect();
            mKonashiManager.close();
            mKonashiManager = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_konashi:
                mKonashiManager.find(this);
                return true;
            case R.id.action_disconnect:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public KonashiManager getKonashiManager() {
        return mKonashiManager;
    }

    private void refreshViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isReady = mKonashiManager.isReady();
                mMenu.findItem(R.id.action_find_konashi).setVisible(!isReady);
                mMenu.findItem(R.id.action_find_konashi).setVisible(isReady);
                mTextNoConnection.setVisibility(isReady ? View.GONE : View.VISIBLE);
            }
        });
    }

    private final KonashiConnectionListener mKonashiConnectionListener = new KonashiConnectionListener() {
        @Override
        public void onReady() {
            refreshViews();
        }

        @Override
        public void onDisconnected() {
            refreshViews();
        }

        @Override public void onNotFoundPeripheral() {}
        @Override public void onConnected() {}
        @Override public void onCancelSelectKonashi() {}
        @Override public void onError(KonashiErrorReason errorReason, String message) {}
    };
}
