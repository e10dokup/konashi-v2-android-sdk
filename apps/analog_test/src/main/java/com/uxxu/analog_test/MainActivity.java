package com.uxxu.analog_test;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiObserver;
import com.uxxu.konashi.lib.ui.KonashiActivity;


public class MainActivity extends KonashiActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final MainActivity self = this;

    private LinearLayout mContainer;
    private Button mFindButton;
    private Button mCheckConnectionButton;
    private Button mResetButton;
    private TextView mReadInput0Text;
    private TextView mReadInput1Text;
    private TextView mReadInput2Text;
    private Button mReadAio0Button;
    private Button mReadAio1Button;
    private Button mReadAio2Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタン全体のコンテナ
        mContainer = (LinearLayout)findViewById(R.id.container);
        mContainer.setVisibility(View.GONE);


        // 一番上に表示されるボタン。konashiにつないだり、切断したり
        mFindButton = (Button)findViewById(R.id.find_button);
        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getKonashiManager().isReady()) {
                    // konashiを探して接続。konashi選択ダイアログがでます
                    getKonashiManager().find(MainActivity.this);

                    // konashiを明示的に指定して、選択ダイアログを出さない
                    //mKonashiManager.findWithName(MainActivity.this, "konashi#4-0452");
                } else {
                    // konashiバイバイ
                    getKonashiManager().disconnect();

                    mFindButton.setText(getText(R.string.find_button));
                    mContainer.setVisibility(View.GONE);
                }
            }
        });

        mCheckConnectionButton = (Button)findViewById(R.id.check_connection_button);
        mCheckConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getKonashiManager().isConnected()){
                    Toast.makeText(self, getKonashiManager().getPeripheralName() + " is connected!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(self, "konashi isn't connected!", Toast.LENGTH_LONG).show();
                }
            }
        });

        mResetButton = (Button)findViewById(R.id.reset_button);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKonashiManager().reset();

                mFindButton.setText(getText(R.string.find_button));
                mContainer.setVisibility(View.GONE);
            }
        });

        mReadInput0Text = (TextView)findViewById(R.id.text_read_input0);
        mReadInput0Text = (TextView)findViewById(R.id.text_read_input1);
        mReadInput0Text = (TextView)findViewById(R.id.text_read_input2);

        mReadAio0Button = (Button)findViewById(R.id.button_read_aio0);
        mReadAio0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKonashiManager().analogReadRequest(Konashi.AIO0);
            }
        });

        mReadAio1Button = (Button)findViewById(R.id.button_read_aio1);
        mReadAio1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKonashiManager().analogReadRequest(Konashi.AIO1);
            }
        });

        mReadAio2Button = (Button)findViewById(R.id.button_read_aio2);
        mReadAio2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getKonashiManager().analogReadRequest(Konashi.AIO2);
            }
        });


        // konashiのイベントハンドラを設定。定義は下の方にあります
        getKonashiManager().addObserver(mKonashiObserver);
    }

    /**
     * konashiのイベントハンドラ
     */
    private final KonashiObserver mKonashiObserver = new KonashiObserver(MainActivity.this) {
        @Override
        public void onReady(){
            Log.d(TAG, "onKonashiReady");

            // findボタンのテキストをdisconnectに
            mFindButton.setText(getText(R.string.disconnect_button));
            // ボタンを表示する
            mContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onUpdatePioInput(byte value){
            Log.d(TAG, "onUpdatePioInput: " + value);
        }

        @Override
        public void onUpdateAnalogValueAio0(int value) {
            mReadInput0Text.setText(getText(R.string.text_read_input) + " 0 " + String.valueOf(value));
        }

        @Override
        public void onUpdateAnalogValueAio1(int value) {
            mReadInput1Text.setText(getText(R.string.text_read_input) + " 1 " + String.valueOf(value));
        }

        @Override
        public void onUpdateAnalogValueAio2(int value) {
            mReadInput2Text.setText(getText(R.string.text_read_input) + " 2 " + String.valueOf(value));
        }
    };
}
