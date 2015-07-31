package com.uxxu.konashi.test_all_functions;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiryu on 7/27/15.
 */
public final class PwmFragment extends Fragment {

    public static final String TITLE = "PWM";

    private final KonashiManager mKonashiManager = Konashi.getManager();

    private Spinner mOptionPinSpinner;
    private EditText mOptionPeriodEditText;
    private EditText mOptionDutyEditText;
    private Button mOptionSubmitButton;
    private TableLayout mTableLayout;
    private List<PwmTableRow> mRows = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pwm, container, false);

        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        mTableLayout.addView(new Utils.HeaderTableRowBuilder(getActivity())
                .column("PIN", 1)
                .column("PWN", 1)
                .column("Duty", 6)
                .build());
        for (int pinNumber : Utils.PWM_PINS) {
            PwmTableRow row = PwmTableRow.createWithPinNumber(getActivity(), pinNumber);
            mTableLayout.addView(row);
            mRows.add(row);
        }

        initOptionViews(view);

        return view;
    }

    @Override
    public void onDestroy() {
        if (mKonashiManager.isReady()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int pinNumber : Utils.PWM_PINS) {
                        mKonashiManager.pwmMode(pinNumber, Konashi.PWM_DISABLE);
                    }
                }
            }).start();
        }
        super.onDestroy();
    }

    private void initOptionViews(View parent) {
        mOptionPinSpinner = (Spinner) parent.findViewById(R.id.optionPinSpinner);
        List<String> pinLabels = new ArrayList<>();
        for (int pin : Utils.PWM_PINS) {
            pinLabels.add(String.valueOf(pin));
        }
        mOptionPinSpinner.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                pinLabels));
        mOptionPinSpinner.setSelection(0);

        mOptionPeriodEditText = (EditText) parent.findViewById(R.id.optionPeriodEditText);

        mOptionDutyEditText = (EditText) parent.findViewById(R.id.optionDutyEditText);

        mOptionSubmitButton = (Button) parent.findViewById(R.id.optionSubmitButton);
        mOptionSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pinNumber = Integer.valueOf((String) mOptionPinSpinner.getSelectedItem());
                if (pinNumber < 0 || Utils.PWM_PINS.length <= pinNumber) {
                    return;
                }
                mRows.get(pinNumber).setValues(
                        Integer.valueOf(mOptionPeriodEditText.getText().toString()),
                        Integer.valueOf(mOptionDutyEditText.getText().toString()));
            }
        });
    }

    public static final class PwmTableRow extends TableRow {

        private final TextView mPinTextView;
        private final Switch mPwmSwitch;
        private final SeekBar mDutySeekBar;
        private final KonashiManager mKonashiManager = Konashi.getManager();
        private int mPinNumber;

        public static PwmTableRow createWithPinNumber(Context context, final int pinNumber) {
            PwmTableRow row = new PwmTableRow(context);
            row.setPinNumber(pinNumber);
            return row;
        }

        public PwmTableRow(final Context context) {
            super(context);

            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            setPadding(0, 20, 0, 20);

            mPinTextView = new TextView(context);
            mPinTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            addView(mPinTextView, Utils.createTableRowLayoutParamsWithWeight(1));

            mPwmSwitch = new Switch(context);
            mPwmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    final int pwmMode = b ? Konashi.PWM_ENABLE_LED_MODE : Konashi.PWM_DISABLE;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mKonashiManager.pwmMode(mPinNumber, pwmMode);
                            if (pwmMode == Konashi.PWM_ENABLE_LED_MODE) {
                                mKonashiManager.pwmLedDrive(mPinNumber, mDutySeekBar.getProgress());
                            }
                        }
                    }).start();
                    mDutySeekBar.setEnabled(b);
                }
            });
            addView(mPwmSwitch, Utils.createTableRowLayoutParamsWithWeight(1));

            mDutySeekBar = new SeekBar(context);
            mDutySeekBar.setMax(100);
            mDutySeekBar.setEnabled(false);
            mDutySeekBar.setProgress(mDutySeekBar.getMax() / 2);
            mDutySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    final int drive = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mKonashiManager.pwmLedDrive(mPinNumber, drive);
                        }
                    }).start();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                }
            });
            addView(mDutySeekBar, Utils.createTableRowLayoutParamsWithWeight(6));
        }

        public void setPinNumber(int pinNumber) {
            this.mPinNumber = pinNumber;
            mPinTextView.setText(String.valueOf(pinNumber));
        }

        public void setValues(final int period, final int duty) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mKonashiManager.pwmPeriod(mPinNumber, period);
                    Utils.sleepShort();
                    mKonashiManager.pwmDuty(mPinNumber, duty);
                }
            }).start();
        }
    }
}
