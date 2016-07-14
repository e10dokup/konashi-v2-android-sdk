package com.uxxu.konashi.lib.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
     * @param characteristic 返ってきたcharacteristic
     * @return BatteryLevel値
     */
    public static int getBatteryLevel(BluetoothGattCharacteristic characteristic) {
        return characteristic.getValue()[0] & 0xff;
    }

    /**
     * Revisionのvalueから最後の1バイト（0x00）を取り除く
     * @param value 返ってきたcharacteristicのvalue
     * @return RevisionのString
     */
    public static String getSoftwareRevision(byte[] value) {
        byte[] cutValue = new byte[value.length-1];
        for(int i=0; i<cutValue.length; i++) {
            cutValue[i] = value[i];
        }
        return new String(cutValue);
    }

    public static int bytes2int(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xff);
        }
        return value;
    }

    public static byte[] int2bytes(int a) {
        int arraySize = Integer.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.putInt(a).array();
    }
}
