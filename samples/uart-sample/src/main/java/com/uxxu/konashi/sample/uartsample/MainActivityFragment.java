package com.uxxu.konashi.sample.uartsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorReason;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.listeners.KonashiAnalogListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final int[] AIO_PINS = new int[] {
            Konashi.AIO0, Konashi.AIO1, Konashi.AIO2
    };

    private static final int[] VOLTAGE_TEXT_IDS = new int[] {
            R.id.voltage_text_aio1, R.id.voltage_text_aio2, R.id.voltage_text_aio3
    };

    private static final int[] VOLTAGE_BAR_IDS = new int[] {
            R.id.voltage_bar_aio1, R.id.voltage_bar_aio2, R.id.voltage_bar_aio3
    };

    private static final int[] READ_BUTTON_IDS = new int[] {
            R.id.btn_read_aio1, R.id.btn_read_aio2, R.id.btn_read_aio3
    };

    private ProgressBar[] mVoltageBars = new ProgressBar[3];
    private TextView[] mVoltageTexts = new TextView[3];

    private Callback mCallback;

    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Callback) {
            mCallback = (Callback) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        for (int i = 0; i < AIO_PINS.length; i++) {
            mVoltageBars[i] = (ProgressBar) v.findViewById(VOLTAGE_BAR_IDS[i]);
            mVoltageTexts[i] = (TextView) v.findViewById(VOLTAGE_TEXT_IDS[i]);
            v.findViewById(READ_BUTTON_IDS[i]).setOnClickListener(mOnClickListener);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallback.getKonashiManager().addListener(mKonashiAnalogListener);
    }

    @Override
    public void onDestroyView() {
        mCallback.getKonashiManager().removeListener(mKonashiAnalogListener);
        super.onDestroyView();
    }

    private void setVoltage(final int pin, final int value) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < AIO_PINS.length; i++) {
                    if (AIO_PINS[i] == pin) {
                        mVoltageTexts[i].setText(getActivity().getString(R.string.text_voltage_value, value));
                        mVoltageBars[i].setProgress(Math.min(100, Math.round(value / 1300f * 100f)));
                        break;
                    }
                }
            }
        });
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < READ_BUTTON_IDS.length; i++) {
                if (READ_BUTTON_IDS[i] == v.getId()) {
                    mCallback.getKonashiManager().analogReadRequest(AIO_PINS[i]);
                    break;
                }
            }
        }
    };

    private final KonashiAnalogListener mKonashiAnalogListener = new KonashiAnalogListener() {
        @Override
        public void onUpdateAnalogValue(int pin, int value) {
            setVoltage(pin, value);
        }

        @Override
        public void onUpdateAnalogValueAio0(int value) {
            setVoltage(AIO_PINS[0], value);
        }

        @Override
        public void onUpdateAnalogValueAio1(int value) {
            setVoltage(AIO_PINS[1], value);
        }

        @Override
        public void onUpdateAnalogValueAio2(int value) {
            setVoltage(AIO_PINS[2], value);
        }

        @Override public void onError(KonashiErrorReason errorReason, String message) {}
    };

    public interface Callback {
        KonashiManager getKonashiManager();
    }
}
