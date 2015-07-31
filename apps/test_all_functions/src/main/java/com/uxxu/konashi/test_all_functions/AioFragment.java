package com.uxxu.konashi.test_all_functions;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiryu on 7/27/15.
 */
public final class AioFragment extends Fragment {

    public static final String TITLE = "Analog I/O";

    private final KonashiManager mKonashiManager = Konashi.getManager();
    private KonashiObserver mAnalogReadObserver;

    private TableLayout mTableLayout;
    private List<AioTableRow> mRows = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(TITLE);

        mAnalogReadObserver = new KonashiObserver(getActivity()) {
            @Override
            public void onUpdateAnalogValueAio0(int value) {
                mRows.get(0).setVoltage(value / 1000.0f);
            }

            @Override
            public void onUpdateAnalogValueAio1(int value) {
                mRows.get(1).setVoltage(value / 1000.0f);
            }

            @Override
            public void onUpdateAnalogValueAio2(int value) {
                mRows.get(2).setVoltage(value / 1000.0f);
            }
        };
        mKonashiManager.addObserver(mAnalogReadObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aio, container, false);

        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout);
        mTableLayout.addView(new Utils.HeaderTableRowBuilder(getActivity())
                .column("PIN", 1)
                .column("Voltage", 5)
                .column("Read", 2).build());
        for (int pinNumber : Utils.AIO_PINS) {
            AioTableRow row = AioTableRow.createWithPinNumber(getActivity(), pinNumber);
            mTableLayout.addView(row);
            mRows.add(row);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        mKonashiManager.removeObserver(mAnalogReadObserver);
        super.onDestroy();
    }

    public static final class AioTableRow extends TableRow {

        private final TextView mPinTextView;
        private final TextView mVoltageTextView;
        private final ProgressBar mVoltageProgressBar;
        private final Button mReadButton;
        private final KonashiManager mKonashiManager = Konashi.getManager();
        private int mPinNumber;

        public static AioTableRow createWithPinNumber(Context context, final int pinNumber) {
            AioTableRow row = new AioTableRow(context);
            row.setPinNumber(pinNumber);
            return row;
        }

        public AioTableRow(final Context context) {
            super(context);

            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            mPinTextView = new TextView(context);
            mPinTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            addView(mPinTextView, Utils.createTableRowLayoutParamsWithWeight(1));

            LinearLayout voltageWrapper = new LinearLayout(context);
            voltageWrapper.setOrientation(LinearLayout.VERTICAL);
            mVoltageTextView = new TextView(context);
            mVoltageTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            voltageWrapper.addView(mVoltageTextView);
            mVoltageProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            mVoltageProgressBar.setMax(100);
            voltageWrapper.addView(mVoltageProgressBar);
            addView(voltageWrapper, Utils.createTableRowLayoutParamsWithWeight(5));

            mReadButton = new Button(context);
            mReadButton.setText("Read");
            mReadButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mKonashiManager.analogReadRequest(mPinNumber);
                }
            });
            addView(mReadButton, Utils.createTableRowLayoutParamsWithWeight(2));
        }

        public void setPinNumber(int pinNumber) {
            this.mPinNumber = pinNumber;
            mPinTextView.setText(String.valueOf(pinNumber));
        }

        public void setVoltage(float value) {
            mVoltageTextView.setText(String.format("%.3f V", value));
            mVoltageProgressBar.setProgress(Math.min(100, Math.round(value / 1.3f * 100f)));
        }
    }
}
