package com.uxxu.konashi.test_all_functions;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorReason;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiObserver;
import com.uxxu.konashi.lib.KonashiUtils;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private KonashiManager mKonashiManager = Konashi.getManager();
    private KonashiObserver mKonashiObserver;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private View mOverlay;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKonashiManager.initialize(getApplicationContext());
        mNavigationDrawerFragment =
                (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mOverlay = findViewById(R.id.overlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mKonashiObserver = new KonashiObserver(this) {

            @Override
            public void onReady() {
                KonashiUtils.log("onReady");
                refreshActionBarMenu();
                mOverlay.setVisibility(View.GONE);

                Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDisconnected() {
                KonashiUtils.log("onDisconnected");
                refreshActionBarMenu();
                mOverlay.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(KonashiErrorReason errorReason, String message) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        };
        mKonashiManager.addObserver(mKonashiObserver);
        mOverlay.setVisibility(mKonashiManager.isReady() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPause() {
        mKonashiManager.removeObserver(mKonashiObserver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mKonashiManager != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mKonashiManager.reset();
                    mKonashiManager.disconnect();
                    mKonashiManager.close();
                    mKonashiManager = null;
                }
            }).start();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.close();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new KonashiInfoFragment();
                break;
            case 1:
                fragment = new PioFragment();
                break;
            case 2:
                fragment = new PwmFragment();
                break;
            case 3:
                fragment = new AioFragment();
                break;
            case 4:
                fragment = new CommunicationFragment();
                break;
        }
        if (fragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        refreshActionBarMenu();
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mNavigationDrawerFragment.isDrawerOpen()) {
                    mNavigationDrawerFragment.close();
                } else {
                    mNavigationDrawerFragment.open();
                }
                break;
            case R.id.action_find_konashi:
                mKonashiManager.find(this);
                return true;
            case R.id.action_disconnect:
                mKonashiManager.disconnect();
                refreshActionBarMenu();
                mOverlay.setVisibility(View.VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshActionBarMenu() {
        if (mKonashiManager.isConnected()) {
            mMenu.findItem(R.id.action_find_konashi).setVisible(false);
            mMenu.findItem(R.id.action_disconnect).setVisible(true);
        } else {
            mMenu.findItem(R.id.action_find_konashi).setVisible(true);
            mMenu.findItem(R.id.action_disconnect).setVisible(false);
        }
    }
}
