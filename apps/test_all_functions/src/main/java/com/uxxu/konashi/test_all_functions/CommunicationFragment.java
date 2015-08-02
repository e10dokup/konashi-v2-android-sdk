package com.uxxu.konashi.test_all_functions;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiObserver;

/**
 * Created by kiryu on 7/27/15.
 */
public final class CommunicationFragment extends Fragment {

    public static final String TITLE = "Communication (UART, I2C)";

    private final KonashiManager mKonashiManager = Konashi.getManager();
    private KonashiObserver mCommunicationObserver;

    private Switch mUartSwitch;
    private Spinner mUartBaudrateSpinner;
    private EditText mUartDataEditText;
    private Button mUartDataSendButton;
    private EditText mUartResultEditText;
    private Button mUartResultClearButton;

    private Switch mI2cSwitch;
    private Spinner mI2cBaudrateSpinner;
    private EditText mI2cDataEditText;
    private Button mI2cDataSendButton;
    private EditText mI2cResultEditText;
    private Button mI2cResultReadButton;
    private Button mI2cResultClearButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(TITLE);

        mCommunicationObserver = new KonashiObserver(getActivity()) {
            @Override
            public void onCompleteUartRx(byte[] data) {
                mUartResultEditText.append(new String(data));
            }

            @Override
            public void onCompleteI2cRx(byte[] data) {
                mI2cResultEditText.append(new String(data));
            }
        };
        mKonashiManager.addObserver(mCommunicationObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);

        initUartViews(view);
        initI2cViews(view);

        return view;
    }

    @Override
    public void onDestroy() {
        mKonashiManager.removeObserver(mCommunicationObserver);
        super.onDestroy();
    }

    private void initUartViews(final View parent) {
        mUartBaudrateSpinner = (Spinner) parent.findViewById(R.id.uartBaudrateSpinner);
        mUartBaudrateSpinner.setSelection(1);
        mUartBaudrateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resetUart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mUartSwitch = (Switch) parent.findViewById(R.id.uartSwitch);
        mUartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                resetUart();
            }
        });

        mUartDataEditText = (EditText) parent.findViewById(R.id.uartDataEditText);

        mUartDataSendButton = (Button) parent.findViewById(R.id.uartDataSendButton);
        mUartDataSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKonashiManager.uartWrite(mUartDataEditText.getText().toString().getBytes());
            }
        });

        mUartResultEditText = (EditText) parent.findViewById(R.id.uartResultEditText);

        mUartResultClearButton = (Button) parent.findViewById(R.id.uartResultClearButton);
        mUartResultClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUartResultEditText.setText("");
            }
        });
    }

    private void initI2cViews(View parent) {
        mI2cBaudrateSpinner = (Spinner) parent.findViewById(R.id.i2cBaudrateSpinner);
        mI2cBaudrateSpinner.setSelection(0);
        mI2cBaudrateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                resetI2c();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mI2cSwitch = (Switch) parent.findViewById(R.id.i2cSwitch);
        mI2cSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                resetI2c();
            }
        });

        mI2cDataEditText = (EditText) parent.findViewById(R.id.i2cDataEditText);

        mI2cDataSendButton = (Button) parent.findViewById(R.id.i2cDataSendButton);
        mI2cDataSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKonashiManager.i2cStartCondition();
                String text = mI2cDataEditText.getText().toString().trim();
                if (Konashi.I2C_DATA_MAX_LENGTH < text.length()) {
                    text = text.substring(0, Konashi.I2C_DATA_MAX_LENGTH);
                }
                mKonashiManager.i2cWrite(
                        text.length(),
                        text.getBytes(),
                        (byte) 0x1F);
                mKonashiManager.i2cStopCondition();
            }
        });

        mI2cResultEditText = (EditText) parent.findViewById(R.id.i2cResultEditText);

        mI2cResultReadButton = (Button) parent.findViewById(R.id.i2cResultReadButton);
        mI2cResultReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mKonashiManager.i2cStartCondition();
                try {
                    Thread.sleep(200);
                    mKonashiManager.i2cReadRequest(Konashi.I2C_DATA_MAX_LENGTH, (byte) 0x1F);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mKonashiManager.i2cStopCondition();
            }
        });

        mI2cResultClearButton = (Button) parent.findViewById(R.id.i2cResultClearButton);
        mI2cResultClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mI2cResultEditText.setText("");
            }
        });
    }

    private void resetUart() {
        if (!mKonashiManager.isReady()) {
            return;
        }
        if (mUartSwitch.isChecked()) {
            mKonashiManager.uartMode(Konashi.UART_ENABLE);
            int i = mUartBaudrateSpinner.getSelectedItemPosition();
            String[] labels = getResources().getStringArray(R.array.uart_baudrates_labels);
            String label = labels[i];

            mKonashiManager.uartBaudrate(Utils.uartLabelToValue(label));

            mUartBaudrateSpinner.setEnabled(true);
            mUartDataEditText.setEnabled(true);
            mUartDataSendButton.setEnabled(true);
        } else {
            mKonashiManager.uartMode(Konashi.UART_DISABLE);

            mUartBaudrateSpinner.setEnabled(false);
            mUartDataEditText.setEnabled(false);
            mUartDataSendButton.setEnabled(false);
        }
    }

    private void resetI2c() {
        if (!mKonashiManager.isReady()) {
            return;
        }
        if (mI2cSwitch.isChecked()) {
            int i = mI2cBaudrateSpinner.getSelectedItemPosition();
            if (i == 0) {
                mKonashiManager.i2cMode(Konashi.I2C_ENABLE_100K);
            } else {
                mKonashiManager.i2cMode(Konashi.I2C_ENABLE_400K);
            }

            mI2cBaudrateSpinner.setEnabled(true);
            mI2cDataEditText.setEnabled(true);
            mI2cDataSendButton.setEnabled(true);
            mI2cResultReadButton.setEnabled(true);
        } else {
            mKonashiManager.i2cMode(Konashi.I2C_DISABLE);

            mI2cBaudrateSpinner.setEnabled(false);
            mI2cDataEditText.setEnabled(false);
            mI2cDataSendButton.setEnabled(false);
            mI2cResultReadButton.setEnabled(false);
        }
    }
}
