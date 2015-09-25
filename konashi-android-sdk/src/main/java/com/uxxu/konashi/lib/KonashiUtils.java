package com.uxxu.konashi.lib;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.util.Arrays;

/**
 * konashiライブラリの便利ツール。ライブラリ外から使うことはたぶんないかな！
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
public class KonashiUtils {
    /**
     * デバッグモードか否か。konashiのライブラリの動きを見たかったらtrueにしてみよう
     */
    private static final Boolean DEBUG = true;

    /**
     * Logのタグ
     */
    private static final String TAG = "KonashiLib";
    /**
     * StackTarceのレベル
     */
    private static final int LOG_STACK_LEVEL = 3;
    
    /**
     * logに表示する(関数名や行数も表示するよっ）
     * @param text
     */
    public static void log(String text){
        if(DEBUG){
            StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            Log.d(TAG, "[" + ste[LOG_STACK_LEVEL].getFileName() + ":" + ste[LOG_STACK_LEVEL].getMethodName() + "(" + ste[LOG_STACK_LEVEL].getLineNumber() + ")] " + text);
        }
    }

    /**
     * characteristicからAnalogReadの値を取得する
     * @param pin AIOのPIN番号
     * @param characteristic 返ってきたcharacteristic
     * @return AnalogRead値
     */
    public static int getAnalogValue(int pin, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        switch (pin) {
            case Konashi.AIO0:
            case Konashi.AIO1:
            case Konashi.AIO2:
                return (value[0] << 8 & 0xff00) | (value[1] & 0xff);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * characteristicからAnalogReadの値を取得する
     * @param characteristic 返ってきたcharacteristic
     * @return BatteryLevel値
     */
    public static int getBatteryLevel(BluetoothGattCharacteristic characteristic) {
        return characteristic.getValue()[0] & 0xff;
    }

    /**
     * characteristicからPwmのPeriodを取得する
     * @param characteristic 返ってきたcharacteristic
     * @return Period値
     */
    public static int getPwmPeriod(BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();
        bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        return bytes2int(bytes);
    }

    /**
     * characteristicからPwmのDutyを取得する
     * @param characteristic 返ってきたcharacteristic
     * @return Duty値
     */
    public static int getPwmDuty(BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();
        bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
        return bytes2int(bytes);
    }

    /**
     * characteristicからUARTのbaudrateを取得する
     * @param characteristic 返ってきたcharacteristic
     * @return baudrate
     */
    public static int getUartBaudrate(BluetoothGattCharacteristic characteristic) {
        return bytes2int(characteristic.getValue());
    }

    /**
     * characteristicからUARTの送信データを取得する
     * @param characteristic 返ってきたcharacteristic
     * @return 送信したバイト列
     */
    public static byte[] getUartWriteData(BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();
        return Arrays.copyOfRange(bytes, 1, bytes[0] + 1);
    }

    /**
     * characteristicからI2Cの送信アドレスを取得する
     * @param characteristic 返ってきたcharacteristic
     * @return 送信先アドレス
     */
    public static byte getI2cWriteAddress(BluetoothGattCharacteristic characteristic) {
        return (byte) ((characteristic.getValue()[1] >> 1) & 0x7F);
    }

    /**
     * characteristicからI2Cの送信データを取得する
     * @param characteristic 返ってきたcharacteristic
     * @return 送信したバイト列
     */
    public static byte[] getI2cWriteData(BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();
        return Arrays.copyOfRange(bytes, 2, bytes[0] + 1);
    }

    private static int bytes2int(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xff);
        }
        return value;
    }

    //TODO: pwmModeをPWM_ENABLE_LEDにした時にpwmModeが反映されなくなるので応急処置的に遅延．要変更
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
