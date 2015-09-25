package com.uxxu.konashi.lib;

import android.content.Context;

import com.uxxu.konashi.lib.listeners.KonashiBaseListener;
import com.uxxu.konashi.lib.stores.KonashiAnalogStore;
import com.uxxu.konashi.lib.stores.KonashiDigitalStore;

import java.util.List;

/**
 * konashiを管理するメインクラス
 * 
 * @author monakaz, YUKAI Engineering
 * http://konashi.ux-xu.com
 * ========================================================================
 * Copyright 2014 Yukai Engineering Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
public class KonashiManager extends KonashiBaseManager implements KonashiApiInterface {
    private static final int PIO_LENGTH = 8;
    private static final int PWM_LENGTH = 8;
    private static final int AIO_LENGTH = 3;
    
    // konashi members
    // PIO
    private KonashiDigitalStore mDigitalStore;

    // PWM
    private byte mPwmSetting = 0;
    private int[] mPwmDuty;
    private int[] mPwmPeriod;
    
    // AIO
    private KonashiAnalogStore mAnalogStore;

    // I2C
    private byte mI2cSetting;
    private byte[] mI2cReadData;
    private int mI2cReadDataLength;
    private byte mI2cReadAddress;
    
    // UART
    private byte mUartSetting;
    private byte mUartBaudrate;
    
    // Hardware
    private int mBatteryLevel;
    private int mRssi;
    
    
    ///////////////////////////
    // Initialization
    ///////////////////////////
    
    private void initializeMembers(){
        int i;
        
        // PIO
        mDigitalStore = new KonashiDigitalStore();
        addListener(mDigitalStore);

        // PWM
        mPwmSetting = 0;
        mPwmDuty = new int[PWM_LENGTH];
        for(i=0; i<PWM_LENGTH; i++)
            mPwmDuty[i] = 0;
        mPwmPeriod = new int[PWM_LENGTH];
        for(i=0; i<PWM_LENGTH; i++)
            mPwmPeriod[i] = 0;
            
        // AIO
        mAnalogStore = new KonashiAnalogStore();
        addListener(mAnalogStore);

        // I2C
        mI2cSetting = 0;
        mI2cReadData = new byte[Konashi.I2C_DATA_MAX_LENGTH];
        for(i=0; i<Konashi.I2C_DATA_MAX_LENGTH; i++)
            mI2cReadData[i] = 0;
        mI2cReadDataLength = 0;
        mI2cReadAddress = 0;
            
        // UART
        mUartSetting = 0;
        mUartBaudrate = 0;
            
        // Hardware
        mBatteryLevel = 0;
        mRssi = 0;
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        
        initializeMembers();
    }
    
    
    ///////////////////////////
    // Observer
    ///////////////////////////

    /**
     * konashiのイベントのリスナーを追加する
     * @param listener 追加するリスナー
     */
    @Override
    public void addListener(KonashiBaseListener listener){
        mNotifier.addListener(listener);
    }

    /**
     * 指定したリスナーを削除する
     * @param listener 削除するリスナー
     */
    @Override
    public void removeListener(KonashiBaseListener listener){
        mNotifier.removeListener(listener);
    }

    /**
     * すべてのリスナーを削除する
     */
    @Override
    public void removeAllListeners() {
        mNotifier.removeAllListeners();
    }


    /**
     * konashiのイベントのオブザーバを追加する
     * @param observer 追加するオブザーバ
     * @deprecated This method deprecated in 0.5.0.
     * Use {@link #addListener(KonashiBaseListener)} instead.
     */
    @Deprecated
    @Override
    public void addObserver(KonashiObserver observer){
        mNotifier.addObserver(observer);
    }

    /**
     * 指定したオブザーバを削除する
     * @param observer 削除するオブザーバ
     * @deprecated This method deprecated in 0.5.0.
     * Use {@link #removeListener(KonashiBaseListener)} instead.
     */
    @Deprecated
    @Override
    public void removeObserver(KonashiObserver observer){
        mNotifier.removeObserver(observer);
    }
    
    /**
     * すべてのオブザーバを削除する
     * @deprecated This method deprecated in 0.5.0.
     * Use {@link #removeAllListeners()} instead.
     */
    @Deprecated
    @Override
    public void removeAllObservers(){
        mNotifier.removeAllObservers();
    }
    
    ///////////////////////////
    // PIO
    ///////////////////////////
    
    /**
     * PIOのピンを入力として使うか、出力として使うかの設定を行う
     * @param pin 設定するPIOのピン名。
     * @param mode ピンに設定するモード。INPUT か OUTPUT が設定できます。
     */
    @Override
    public void pinMode(int pin, int mode){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.PIO0 && pin <= Konashi.PIO7 && (mode == Konashi.OUTPUT || mode == Konashi.INPUT)){
            byte modes = mDigitalStore.getPioModes();
            if(mode == Konashi.OUTPUT){
                modes |= (byte)(0x01 << pin);
            }else{
                modes &= (byte)(~(0x01 << pin) & 0xFF);
            }

            addWriteMessage(KonashiUUID.PIO_SETTING_UUID, new byte[] {modes});
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * PIOのピンを入力として使うか、出力として使うかの設定を行う
     * @param modes PIO0 〜 PIO7 の計8ピンの設定
     */
    @Override
    public void pinModeAll(int modes){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(modes >= 0x00 && modes <= 0xFF){
            addWriteMessage(KonashiUUID.PIO_SETTING_UUID, new byte[] {(byte) modes});
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * PIOのピンをプルアップするかの設定を行う
     * @param pin 設定するPIOのピン名
     * @param pullup ピンをプルアップするかの設定。PULLUP か NO_PULLS が設定できます。
     */
    @Override
    public void pinPullup(int pin, int pullup){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.PIO0 && pin <= Konashi.PIO7 && (pullup == Konashi.PULLUP || pullup == Konashi.NO_PULLS)){
            byte pullups = mDigitalStore.getPioPullups();
            if(pullup == Konashi.PULLUP){
                pullups |= (byte)(0x01 << pin);
            }else{
                pullups &= (byte)(~(0x01 << pin) & 0xFF);
            }

            addWriteMessage(KonashiUUID.PIO_PULLUP_UUID, new byte[]{pullups});
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * PIOのピンをプルアップするかの設定を行う
     * @param pullups PIO0 〜 PIO7 の計8ピンのプルアップの設定
     */
    @Override
    public void pinPullupAll(int pullups){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pullups >= 0x00 && pullups <= 0xFF){
            addWriteMessage(KonashiUUID.PIO_PULLUP_UUID, new byte[]{(byte) pullups});
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * PIOの特定のピンの入力状態を取得する
     * @param pin PIOのピン名
     * @return HIGH(1) もしくは LOW(0)
     */
    @Override
    public int digitalRead(int pin){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return -1;
        }

        if ((pin != Konashi.PIO0) && (pin != Konashi.PIO1) && (pin != Konashi.PIO2) &&
                (pin != Konashi.PIO3) && (pin != Konashi.PIO4) && (pin != Konashi.PIO5) &&
                (pin != Konashi.PIO6) && (pin != Konashi.PIO7)) {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return -1;
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }

        return mDigitalStore.getPioInput(pin);
    }
    
    /**
     * PIOのすべてのピンの状態を取得する
     * @return PIOの状態(PIO0〜PIO7の入力状態が8bit(1byte)で表現)
     */
    @Override
    public int digitalReadAll(){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return -1;
        }
        
        return mDigitalStore.getPioInputs();
    }
    
    /**
     * PIOの特定のピンの出力状態を設定する
     * @param pin 設定するPIOのピン名
     * @param value 設定するPIOの出力状態。HIGH もしくは LOW が指定可能
     */
    @Override
    public void digitalWrite(int pin, int value){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.PIO0 && pin <= Konashi.PIO7 && (value == Konashi.HIGH || value == Konashi.LOW)){
            KonashiUtils.log("digitalWrite pin: " + pin + ", value: " + value);
            byte outputs = mDigitalStore.getPioOutputs();
            
            if(value == Konashi.HIGH){
                outputs |= 0x01 << pin;
            } else {
                outputs &= ~(0x01 << pin) & 0xFF;
            }
            
            addWriteMessage(KonashiUUID.PIO_OUTPUT_UUID, new byte[]{outputs});
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * PIOの特定のピンの出力状態を設定する
     * @param value PIOの出力状態。PIO0〜PIO7の出力状態が8bit(1byte)で表現
     */
    @Override
    public void digitalWriteAll(int value){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(value >= 0x00 && value <= 0xFF){            
            addWriteMessage(KonashiUUID.PIO_OUTPUT_UUID, new byte[]{(byte) value});
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    
    ///////////////////////////
    // PWM
    ///////////////////////////
    
    /**
     * PIO の指定のピンを PWM として使用する/しないかを設定する
     * @param pin PWMモードの設定をするPIOのピン番号。Konashi.PIO0 〜 Konashi.PIO7。
     * @param mode 設定するPWMのモード。Konashi.PWM_DISABLE, Konashi.PWM_ENABLE, Konashi.PWM_ENABLE_LED_MODE のいずれかをセットする。
     */
    @Override
    public void pwmMode(int pin, int mode){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.PIO0 && pin <= Konashi.PIO7 && (mode == Konashi.PWM_DISABLE || mode == Konashi.PWM_ENABLE || mode == Konashi.PWM_ENABLE_LED_MODE)){
            if(mode == Konashi.PWM_ENABLE || mode == Konashi.PWM_ENABLE_LED_MODE){
                mPwmSetting |= 0x01 << pin;
            } else {
                mPwmSetting &= ~(0x01 << pin) & 0xFF;
            }
            
            if (mode == Konashi.PWM_ENABLE_LED_MODE){
                pwmPeriod(pin, Konashi.PWM_LED_PERIOD);
                KonashiUtils.sleep(30);
                pwmLedDrive(pin, 0.0F);
                KonashiUtils.sleep(30);
            }
            
            byte[] val = new byte[1];
            val[0] = mPwmSetting;
            
            addWriteMessage(KonashiUUID.PWM_CONFIG_UUID, val);
        }
    }
    
    /**
     * 指定のピンのPWM周期を設定する
     * @param pin PWMモードの設定をするPIOのピン番号。Konashi.PIO0 〜 Konashi.PIO7。
     * @param period 周期。単位はマイクロ秒(us)で32bitで指定してください。最大2^(32)us = 71.5分。
     */
    @Override
    public void pwmPeriod(int pin, int period){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.PIO0 && pin <= Konashi.PIO7 && mPwmDuty[pin] <= period){
            mPwmPeriod[pin] = period;
            
            byte[] val = new byte[5];
            val[0] = (byte)pin;
            val[1] = (byte)((mPwmPeriod[pin] >> 24) & 0xFF);
            val[2] = (byte)((mPwmPeriod[pin] >> 16) & 0xFF);
            val[3] = (byte)((mPwmPeriod[pin] >> 8) & 0xFF);
            val[4] = (byte)((mPwmPeriod[pin] >> 0) & 0xFF);
            
            addWriteMessage(KonashiUUID.PWM_PARAM_UUID, val);
        }
    }
    
    /**
     * 指定のピンのPWMのデューティ(ONになっている時間)を設定する。
     * @param pin PWMモードの設定をするPIOのピン番号。Konashi.PIO0 〜 Konashi.PIO7。
     * @param duty デューティ。単位はマイクロ秒(us)で32bitで指定してください。最大2^(32)us = 71.5分。
     */
    @Override
    public void pwmDuty(int pin, int duty){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.PIO0 && pin <= Konashi.PIO7 && duty <= mPwmPeriod[pin]){
            mPwmDuty[pin] = duty;
            
            byte[] val = new byte[5];
            val[0] = (byte)pin;
            val[1] = (byte)((mPwmDuty[pin] >> 24) & 0xFF);
            val[2] = (byte)((mPwmDuty[pin] >> 16) & 0xFF);
            val[3] = (byte)((mPwmDuty[pin] >> 8) & 0xFF);
            val[4] = (byte)((mPwmDuty[pin] >> 0) & 0xFF);
            
            addWriteMessage(KonashiUUID.PWM_DUTY_UUID, val);
        }
    }
    
    /**
     * 指定のピンのLEDの明るさを0%〜100%で指定する
     * @param pin PWMモードの設定をするPIOのピン番号。Konashi.PIO0 〜 Konashi.PIO7。
     * @param dutyRatio LEDの明るさ。0.0F〜100.0F をしてしてください。
     */
    @Override
    public void pwmLedDrive(int pin, float dutyRatio){
        int duty;

        if(dutyRatio >= 0.0 && dutyRatio <= 100.0){
            duty = (int)(Konashi.PWM_LED_PERIOD * dutyRatio / 100);        
            pwmDuty(pin, duty);
        }
    }
    
    /**
     * pwmLedDrive(int pin, float dutyRatio) の doubleでdutyRatioを指定する版。
     * @param pin PWMモードの設定をするPIOのピン番号。Konashi.PIO0 〜 Konashi.PIO7。
     * @param dutyRatio LEDの明るさ。0.0〜100.0 をしてしてください。
     */
    @Override
    public void pwmLedDrive(int pin, double dutyRatio){        
        pwmLedDrive(pin, (float)dutyRatio);
    }
    
    
    ///////////////////////////
    // AIO
    ///////////////////////////

    /**
     * AIO の指定のピンの入力電圧を取得するリクエストを konashi に送る
     * @param pin AIOのピン名。指定可能なピン名は AIO0, AIO1, AIO2
     */
    @Override
    public void analogReadRequest(int pin) {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin==Konashi.AIO0){
            addReadMessage(KonashiUUID.ANALOG_READ0_UUID);
        } else if(pin==Konashi.AIO1){
            addReadMessage(KonashiUUID.ANALOG_READ1_UUID);
        } else if(pin==Konashi.AIO2) {
            addReadMessage(KonashiUUID.ANALOG_READ2_UUID);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * AIO の指定のピンの入力電圧を取得する
     * @param pin AIOのピン名。指定可能なピン名は AIO0, AIO1, AIO2
     */
    @Override
    public int analogRead(int pin) {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return -1;
        }
        
        if(pin >= Konashi.AIO0 && pin <= Konashi.AIO2){
            return mAnalogStore.getAnalogValue(pin);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return -1;
        }
    }
    
    /**
     * AIO の指定のピンに任意の電圧を出力する
     * @param pin AIOのピン名。指定可能なピン名は AIO0, AIO1, AIO2
     * @param milliVolt 設定する電圧をmVで指定。0〜1300を指定可能
     */
    @Override
    public void analogWrite(int pin, int milliVolt){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(pin >= Konashi.AIO0 && pin <= Konashi.AIO2 && milliVolt >= 0 && milliVolt <= Konashi.ANALOG_REFERENCE){
            byte[] val = new byte[3];
            val[0] = (byte)pin;
            val[1] = (byte)((milliVolt >> 8) & 0xFF);
            val[2] = (byte)((milliVolt >> 0) & 0xFF);

            KonashiUtils.log("analogWrite pin: " + pin + ", value: " + milliVolt);
            
            addWriteMessage(KonashiUUID.ANALOG_DRIVE_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    
    ///////////////////////////
    // UART
    ///////////////////////////
        
    /**
     * UART の有効/無効を設定する
     * @param mode 設定するUARTのモード。Konashi.UART_DISABLE, Konashi.UART_ENABLE を指定
     */
    @Override
    public void uartMode(int mode){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(mode==Konashi.UART_DISABLE || mode==Konashi.UART_ENABLE){
            mUartSetting = (byte)mode;
            
            byte[] val = new byte[1];
            val[0] = (byte)mode;
            
            addWriteMessage(KonashiUUID.UART_CONFIG_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * UART の通信速度を設定する
     * @param baudrate UARTの通信速度。Konashi.UART_RATE_2K4 か Konashi.UART_RATE_9K6 を指定
     */
    @Override
    public void uartBaudrate(int baudrate){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if (baudrate == Konashi.UART_RATE_9K6 || baudrate == Konashi.UART_RATE_19K2 ||
                baudrate == Konashi.UART_RATE_38K4 || baudrate == Konashi.UART_RATE_57K6 ||
                baudrate == Konashi.UART_RATE_76K8 || baudrate == Konashi.UART_RATE_115K2
                ) {
            mUartBaudrate = (byte)baudrate;
            
            byte[] val = new byte[2];
            val[0] = (byte)((baudrate >> 8) & 0xFF);
            val[1] = (byte)((baudrate >> 0) & 0xFF);
            
            addWriteMessage(KonashiUUID.UART_BAUDRATE_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * UART でデータを送信する
     * @param data 送信するデータ
     */
    @Override
    public void uartWrite(byte[] data){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }

        int length = data.length;

        if(length > 0 && length <= Konashi.UART_DATA_MAX_LENGTH){
            /**
             * 先頭1バイト目に送信バイト数を，
             * 2バイト目以降に送信データを配置
             */
            byte[] val = new byte[Konashi.UART_DATA_MAX_LENGTH + 1];
            val[0] = (byte)length;
            for(int i=0; i<length; i++){
                val[i+1] = data[i];
            }

            addWriteMessage(KonashiUUID.UART_TX_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }

    }

    /**
     * UART でデータを1バイト送信する
     * for konashi v1(old code)
     * @param data 送信するデータ
     */
//    @Override
//    public void uartWrite(byte data){
//        if(!isEnableAccessKonashi()){
//            notifyKonashiError(KonashiErrorReason.NOT_READY);
//            return;
//        }
//
//        if(mUartSetting==Konashi.UART_ENABLE){
//            byte[] val = new byte[1];
//            val[0] = data;
//
//            addWriteMessage(KonashiUUID.UART_TX_UUID, val);
//        } else {
//            notifyKonashiError(KonashiErrorReason.NOT_ENABLED_UART);
//        }
//    }
    
    
    ///////////////////////////
    // I2C
    ///////////////////////////
    
    /**
     * I2Cのコンディションを発行する
     * @param condition コンディション。Konashi.I2C_START_CONDITION, Konashi.I2C_RESTART_CONDITION, Konashi.I2C_STOP_CONDITION を指定できる。
     */
    private void i2cSendCondition(int condition) {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(!isEnableI2c()){
            notifyKonashiError(KonashiErrorReason.NOT_ENABLED_I2C);
            return;
        }
        
        if(condition==Konashi.I2C_START_CONDITION || condition==Konashi.I2C_RESTART_CONDITION || condition==Konashi.I2C_STOP_CONDITION){
            byte[] val = new byte[1];
            val[0] = (byte)condition;
            
            addWriteMessage(KonashiUUID.I2C_START_STOP_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * I2Cが有効なモードに設定しているか
     * @return 有効なモードに設定されているならtrue
     */
    public boolean isEnableI2c(){
        return (mI2cSetting==Konashi.I2C_ENABLE || mI2cSetting==Konashi.I2C_ENABLE_100K || mI2cSetting==Konashi.I2C_ENABLE_400K);
    }
    
    /**
     * I2Cを有効/無効を設定する
     * @param mode 設定するI2Cのモード。Konashi.I2C_DISABLE , Konashi.I2C_ENABLE, Konashi.I2C_ENABLE_100K, Konashi.I2C_ENABLE_400Kを指定。
     */
    @Override
    public void i2cMode(int mode) {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(mode==Konashi.I2C_DISABLE || mode==Konashi.I2C_ENABLE || mode==Konashi.I2C_ENABLE_100K || mode==Konashi.I2C_ENABLE_400K){
            mI2cSetting = (byte)mode;
            
            byte[] val = new byte[1];
            val[0] = (byte)mode;
            
            addWriteMessage(KonashiUUID.I2C_CONFIG_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }

    /**
     * I2Cのスタートコンディションを発行する
     */
    @Override
    public void i2cStartCondition() {
        i2cSendCondition(Konashi.I2C_START_CONDITION);        
    }

    /**
     * I2Cのリスタートコンディションを発行する
     */
    @Override
    public void i2cRestartCondition() {
        i2cSendCondition(Konashi.I2C_RESTART_CONDITION);
    }

    /**
     * I2Cのストップコンディションを発行する
     */
    @Override
    public void i2cStopCondition() {
        i2cSendCondition(Konashi.I2C_STOP_CONDITION);
    }

    /**
     * I2Cで指定したアドレスにデータを書き込む
     * @param length 書き込むデータ(byte)の長さ。最大 Konashi.I2C_DATA_MAX_LENGTH (19)byteまで
     * @param data 書き込むデータの配列
     * @param address 書き込み先アドレス
     */
    @Override
    public void i2cWrite(int length, byte[] data, byte address) {        
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(!isEnableI2c()){
            notifyKonashiError(KonashiErrorReason.NOT_ENABLED_I2C);
            return;
        }
        
        if(length>0 && length<=Konashi.I2C_DATA_MAX_LENGTH){
            byte[] val = new byte[20];
            val[0] = (byte)(length + 1);
            val[1] = (byte)((address << 1) & 0xFE);
            for(int i=0; i<length; i++){
                val[i+2] = data[i];
            }
            
            addWriteMessage(KonashiUUID.I2C_WRITE_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }

    /**
     * I2Cで指定したアドレスからデータを読み込むリクエストを行う
     * @param length 読み込むデータの長さ。最大 Konashi.I2C_DATA_MAX_LENGTHs (19)
     * @param address 読み込み先のアドレス
     */
    @Override
    public void i2cReadRequest(int length, byte address) {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        if(!isEnableI2c()){
            notifyKonashiError(KonashiErrorReason.NOT_ENABLED_I2C);
            return;
        }
        
        if(length>0 && length<=Konashi.I2C_DATA_MAX_LENGTH){
            mI2cReadAddress = (byte)((address<<1)|0x1);
            mI2cReadDataLength = length;
            
            byte[] val = {(byte)length, mI2cReadAddress};
            addWriteMessage(KonashiUUID.I2C_READ_PARAM_UUID, val);
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
        }
    }
    
    /**
     * I2Cから読み込んだデータを取得する
     * @param length 読み込むデータの長さ。最大 Konashi.I2C_DATA_MAX_LENGTHs (19)
     */
    @Override
    public byte[] i2cRead(int length) {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return null;
        }
        
        if(!isEnableI2c()){
            notifyKonashiError(KonashiErrorReason.NOT_ENABLED_I2C);
            return null;
        }
        
        if(length>0 && length<=Konashi.I2C_DATA_MAX_LENGTH && length==mI2cReadDataLength){
            return mI2cReadData;
        } else {
            notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return null;
        }        
    }

    ///////////////////////////
    // Hardware
    ///////////////////////////

    /**
     * konashiをリセットする
     */
    @Override
    public void reset(){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        byte[] val = new byte[1];
        val[0] = 1;
        
        addWriteMessage(KonashiUUID.HARDWARE_RESET_UUID, val);
    }
    
    /**
     * konashi のバッテリ残量を取得するリクエストを konashi に送信
     */
    @Override
    public void batteryLevelReadRequest(){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        addReadMessage(KonashiUUID.BATTERY_SERVICE_UUID, KonashiUUID.BATTERY_LEVEL_UUID);
    }
    
    /**
     * konashi のバッテリ残量を取得
     * @return 0 〜 100 のパーセント単位でバッテリ残量が返る
     */
    @Override
    public int getBatteryLevel(){
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return -1;
        }
        
        return mBatteryLevel;
    }
    
    /**
     * konashi の電波強度を取得するリクエストを行う
     */
    @Override
    public void signalStrengthReadRequest() {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        readRemoteRssi();
    }

    /**
     * konashi の電波強度を取得
     * @return 電波強度(単位はdb)
     */
    @Override
    public int getSignalStrength() {
        if(!isEnableAccessKonashi()){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return -1;
        }
        
        return mRssi;
    }

    public static byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }
    
    ////////////////////////////////
    // Notification event handler 
    ////////////////////////////////

    @Override
    protected void onRecieveUart(byte[] data) {
        int length = Integer.valueOf(data[0]);
        byte[] uartData = new byte[length];
        for(int i=0;i<length;i++){
            uartData[i] = data[i+1];
        }
        super.onRecieveUart(uartData);
    }

//    /**
//     * for konashi v1(old codes)
//     * @param data
//     */
//    @Override
//    protected void onRecieveUart(byte data) {
//        super.onRecieveUart(data);
//    }

    @Override
    protected void onUpdateBatteryLevel(int level) {
        mBatteryLevel = level;
                
        super.onUpdateBatteryLevel(level);
    }

    @Override
    protected void onUpdateSignalSrength(int rssi) {
        mRssi = rssi;
        
        super.onUpdateSignalSrength(rssi);
    }

}
