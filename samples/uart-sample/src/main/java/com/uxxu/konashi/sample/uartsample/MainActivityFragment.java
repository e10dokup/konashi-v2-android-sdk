package com.uxxu.konashi.sample.uartsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorReason;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiUtils;
import com.uxxu.konashi.lib.listeners.KonashiAnalogListener;
import com.uxxu.konashi.lib.listeners.KonashiUartListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final int WRITE = 0;
    private static final int READ = 1;

    private static final int[] UART_EDITTEXT_IDS = {R.id.uart_data_edittext, R.id.uart_result_edittext};
    private static final int[] UART_BUTTON_IDS = {R.id.uart_data_send_button, R.id.uart_result_clear_button};


    private EditText[] mUartEditTexts = new EditText[2];
    private Button[] mUartButtons = new Button[2];
    private Spinner mUartBaudrateSpinner;
    private Switch mUartSwitch;

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
        for (int i = 0; i < UART_EDITTEXT_IDS.length; i++) {
            mUartEditTexts[i] = (EditText) v.findViewById(UART_EDITTEXT_IDS[i]);
            mUartEditTexts[i].setEnabled(false);
            mUartButtons[i] = (Button) v.findViewById(UART_BUTTON_IDS[i]);
            mUartButtons[i].setOnClickListener(mOnClickListener);
            mUartButtons[i].setEnabled(false);
        }
        mUartBaudrateSpinner = (Spinner) v.findViewById(R.id.uart_baudrate_spinner);
        mUartBaudrateSpinner.setOnItemSelectedListener(mUartBaudrateSelectedListener);
        mUartSwitch = (Switch) v.findViewById(R.id.uart_switch);
        mUartSwitch.setOnCheckedChangeListener(mUartSwitchChangedListener);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallback.getKonashiManager().addListener(mKonashiUartListener);
    }

    @Override
    public void onDestroyView() {
        mCallback.getKonashiManager().removeListener(mKonashiUartListener);
        super.onDestroyView();
    }

    private void setText(final byte[] data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StringBuilder builder = new StringBuilder();
                for(int i=1; i < data.length ; i++){
                    builder.append((char) data[i]);
                }
                mUartEditTexts[READ].append(builder.toString());
            }
        });
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UART_BUTTON_IDS[WRITE] == v.getId()) {
                mCallback.getKonashiManager().uartWrite((mUartEditTexts[WRITE].getText().toString()).getBytes());
            }else if(UART_BUTTON_IDS[READ] == v.getId()) {
                mUartEditTexts[READ].setText("");
            }
        }
    };

    private final CompoundButton.OnCheckedChangeListener mUartSwitchChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            resetUart();
        }
    };

    private void resetUart() {
        if (mUartSwitch.isChecked()) {
            mCallback.getKonashiManager().uartMode(Konashi.UART_ENABLE);
            int p = mUartBaudrateSpinner.getSelectedItemPosition();
            mCallback.getKonashiManager().uartBaudrate(getResources().obtainTypedArray(R.array.uart_baudrates_labels).getInt(p, 9600));
            mUartBaudrateSpinner.setEnabled(true);
            for (int i = 0; i < UART_EDITTEXT_IDS.length; i++) {
                mUartEditTexts[i].setEnabled(true);
                mUartButtons[i].setEnabled(true);
            }
        } else {
            mCallback.getKonashiManager().uartMode(Konashi.UART_DISABLE);

            mUartBaudrateSpinner.setEnabled(false);
            for (int i = 0; i < UART_EDITTEXT_IDS.length; i++) {
                mUartEditTexts[i].setEnabled(false);
                mUartButtons[i].setEnabled(false);
            }
        }
    }

    private final AdapterView.OnItemSelectedListener mUartBaudrateSelectedListener = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int selectedBaudrate = getResources().obtainTypedArray(R.array.uart_baudrates_labels).getInt(i, 9600);
            KonashiUtils.log("Baudrate set " + String.valueOf(selectedBaudrate));
            mCallback.getKonashiManager().uartBaudrate(uartLabelToValue(selectedBaudrate));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private final KonashiUartListener mKonashiUartListener = new KonashiUartListener() {
        @Override
        public void onUpdateUartMode(int mode) {

        }

        @Override
        public void onUpdateUartBaudrate(int baudrate) {
        }

        @Override
        public void onWriteUart(byte[] data) {
            KonashiUtils.log(new String(data));
        }

        @Override
        public void onCompleteUartRx(byte[] data) {
            setText(data);
        }

        @Override
        public void onError(KonashiErrorReason errorReason, String message) {
            KonashiUtils.log(message);
        }
    };

    public interface Callback {
        KonashiManager getKonashiManager();
    }

    //TODO: baudrate 選択肢追加
    public static int uartLabelToValue(int selectedBaudrate) {
        switch(selectedBaudrate){
            case 9600:
                return Konashi.UART_RATE_9K6;
            default:
                return Konashi.UART_RATE_9K6;
        }
    }
}
