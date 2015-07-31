package com.uxxu.konashi.test_all_functions;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiObserver;

/**
 * Created by kiryu on 7/27/15.
 */
public final class KonashiInfoFragment extends Fragment {

    public static final String TITLE = "Konashi Info";

    private final KonashiManager mKonashiManager = Konashi.getManager();
    private KonashiObserver mInformationObserver;

    private TextView mNameTextView;
    private TextView mRssiTextView;
    private ProgressBar mRssiProgressBar;
    private TextView mBatteryTextView;
    private ProgressBar mBatteryProgressBar;
    private Button mReloadButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(TITLE);

        mInformationObserver = new KonashiObserver(getActivity()) {

            @Override
            public void onReady() {
                reload();
            }

            @Override
            public void onUpdateBatteryLevel(final int level) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBatteryTextView.setText(String.format("%d%%", level));
                        mBatteryProgressBar.setProgress(level);
                    }
                });
            }

            @Override
            public void onUpdateSignalStrength(final int rssi) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRssiTextView.setText(String.format("%ddb", rssi));
                        mRssiProgressBar.setProgress(Math.abs(rssi));
                    }
                });
            }
        };
        mKonashiManager.addObserver(mInformationObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_konashi_info, container, false);

        mNameTextView = (TextView) view.findViewById(R.id.nameTextView);

        mRssiTextView = (TextView) view.findViewById(R.id.rssiTextView);
        mRssiProgressBar = (ProgressBar) view.findViewById(R.id.rssiProgressBar);

        mBatteryTextView = (TextView) view.findViewById(R.id.batteryTextView);
        mBatteryProgressBar = (ProgressBar) view.findViewById(R.id.batteryProgressBar);

        mReloadButton = (Button) view.findViewById(R.id.reloadButton);
        mReloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });

        reload();

        return view;
    }

    @Override
    public void onDestroy() {
        mKonashiManager.removeObserver(mInformationObserver);
        super.onDestroy();
    }

    private void reload() {
        if (!mKonashiManager.isReady()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNameTextView.setText(mKonashiManager.getPeripheralName());
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                mKonashiManager.batteryLevelReadRequest();
                mKonashiManager.signalStrengthReadRequest();
            }
        }).start();
    }
}
