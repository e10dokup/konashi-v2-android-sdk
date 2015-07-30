package com.uxxu.konashi.lib.delegators;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorReason;
import com.uxxu.konashi.lib.KonashiManager;
import com.uxxu.konashi.lib.KonashiNotifier;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.entities.KonashiWriteMessage;

/**
 * Created by izumin on 7/30/15.
 */
public class I2cHelper {
    public static final String TAG = I2cHelper.class.getSimpleName();

    private KonashiManager mManager;
    private KonashiNotifier mNotifier;

    private byte mI2cMode;
    private byte[] mI2cReadData;
    private int mI2cReadDataLength;
    private byte mI2cReadAddress;

    private static final int[] CONDITIONS = {
            Konashi.I2C_START_CONDITION,
            Konashi.I2C_RESTART_CONDITION,
            Konashi.I2C_STOP_CONDITION
    };

    private static final int[] MODES = {
            Konashi.I2C_DISABLE,
            Konashi.I2C_ENABLE,
            Konashi.I2C_ENABLE_100K,
            Konashi.I2C_ENABLE_400K
    };

    public I2cHelper(KonashiManager manager, KonashiNotifier notifier) {
        mManager = manager;
        mNotifier = notifier;
        initValues();
    }

    // ****************************************************************
    // public methods
    // ****************************************************************

    /**
     * I2Cを有効/無効を設定するためのメッセージを発行する
     * @param mode 設定するI2Cのモード。Konashi.I2C_DISABLE , Konashi.I2C_ENABLE, Konashi.I2C_ENABLE_100K, Konashi.I2C_ENABLE_400Kを指定。
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public KonashiWriteMessage getI2cModeMessage(int mode) {
        if (!shouldEnableAccessKonashi()) return null;

        if (isI2cMode(mode)) {
            mI2cMode = (byte) mode;
            byte[] val = new byte[1];
            val[0] = mI2cMode;

            return new KonashiWriteMessage(KonashiUUID.I2C_CONFIG_UUID, val);
        } else {
            mNotifier.notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return null;
        }
    }

    /**
     * I2Cのスタートコンディションを発行する
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public KonashiWriteMessage getI2cStartConditionMessage() {
        return i2cSendCondition(Konashi.I2C_START_CONDITION);
    }

    /**
     * I2Cのリスタートコンディションを発行する
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public KonashiWriteMessage getI2cRestartConditionMessage() {
        return i2cSendCondition(Konashi.I2C_RESTART_CONDITION);
    }

    /**
     * I2Cのストップコンディションを発行する
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public KonashiWriteMessage getI2cStopConditionMessage() {
        return i2cSendCondition(Konashi.I2C_STOP_CONDITION);
    }

    /**
     * I2Cで指定したアドレスにデータを書き込むためのメッセージを発行する
     * @param length 書き込むデータ(byte)の長さ。最大 Konashi.I2C_DATA_MAX_LENGTH (19)byteまで
     * @param data 書き込むデータの配列
     * @param address 書き込み先アドレス
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public KonashiWriteMessage getI2cWriteMessage(int length, byte[] data, byte address) {
        if (!shouldEnableAccessKonashi() && !shouldEnableI2c()) return null;

        if (isValidLength(length)) {
            byte[] val = new byte[20];
            val[0] = (byte)(length + 1);
            val[1] = (byte)((address << 1) & 0xFE);
            for (int i=0; i < length; i++){
                val[i+2] = data[i];
            }

            return new KonashiWriteMessage(KonashiUUID.I2C_WRITE_UUID, val);
        } else {
            mNotifier.notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return null;
        }
    }

    /**
     * I2Cで指定したアドレスからデータを読み込むリクエストを送るためのメッセージを発行する
     * @param length 読み込むデータの長さ。最大 Konashi.I2C_DATA_MAX_LENGTHs (19)
     * @param address 読み込み先のアドレス
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public KonashiWriteMessage getI2cReadRequestMessage(int length, byte address) {
        if (!shouldEnableAccessKonashi() && !shouldEnableI2c()) return null;

        if(isValidLength(length)){
            mI2cReadAddress = (byte)((address<<1)|0x1);
            mI2cReadDataLength = length;

            byte[] val = {(byte)length, mI2cReadAddress};

            return new KonashiWriteMessage(KonashiUUID.I2C_READ_PARAM_UUID, val);
        } else {
            mNotifier.notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return null;
        }
    }

    /**
     * I2Cから読み込んだデータを取得するためのメッセージを発行する
     * @param length 読み込むデータの長さ。最大 Konashi.I2C_DATA_MAX_LENGTHs (19)
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    public byte[] getI2cRead(int length) {
        if (!shouldEnableAccessKonashi() && !shouldEnableI2c()) return null;

        if (isValidLength(length) && length == mI2cReadDataLength) {
            return mI2cReadData;
        } else {
            mNotifier.notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return null;
        }
    }

    // ****************************************************************
    // private methods
    // ****************************************************************

    private void initValues() {
        mI2cMode = 0;
        mI2cReadData = new byte[Konashi.I2C_DATA_MAX_LENGTH];
        for(int i = 0; i < Konashi.I2C_DATA_MAX_LENGTH; i++) {
            mI2cReadData[i] = 0;
        }
        mI2cReadDataLength = 0;
        mI2cReadAddress = 0;
    }

    /**
     * I2Cのコンディションを発行する
     * @param condition コンディション。Konashi.I2C_START_CONDITION, Konashi.I2C_RESTART_CONDITION, Konashi.I2C_STOP_CONDITION を指定できる。
     * @return konashiに送るためのメッセージ（modeが不正な場合はnull）
     */
    private KonashiWriteMessage i2cSendCondition(int condition) {
        if (!shouldEnableAccessKonashi() && !shouldEnableI2c()) return null;

        if (isI2cCondition(condition)) {
            byte[] val = new byte[1];
            val[0] = (byte) condition;
            return new KonashiWriteMessage(KonashiUUID.I2C_START_STOP_UUID, val);
        } else {
            mNotifier.notifyKonashiError(KonashiErrorReason.INVALID_PARAMETER);
            return null;
        }
    }

    /**
     * I2Cが有効なモードに設定しているか
     * @return 有効なモードに設定されているならtrue
     */
    private boolean isEnableI2c() {
        // NOTE: Konashi.I2C_ENABLE equals Konashi.I2C_ENABLE_100K
        return (mI2cMode == Konashi.I2C_ENABLE_100K) || (mI2cMode == Konashi.I2C_ENABLE_400K);
    }

    /**
     * 正しいコンディションであるか
     * @param condition 検証したいコンディション
     * @return 有効なコンディションならtrue
     */
    private boolean isI2cCondition(int condition) {
        return (condition == CONDITIONS[0]) || (condition == CONDITIONS[1]) || (condition == CONDITIONS[2]);
    }

    /**
     * 正しいI2Cのモードの値であるか
     * @param mode 検証したいモードの値
     * @return 有効なモードならtrue
     */
    private boolean isI2cMode(int mode) {
        // NOTE: Konashi.I2C_ENABLE equals Konashi.I2C_ENABLE_100K
        return (mode == MODES[0]) || (mode == MODES[2]) || (mode == MODES[3]);
    }

    /**
     * Konashiへのアクセスが有効になっているかどうか
     * 無効である場合は{@link com.uxxu.konashi.lib.KonashiErrorReason#NOT_READY}を発行する
     * @return アクセスが有効ならtrue
     */
    private boolean shouldEnableAccessKonashi() {
        if (!mManager.isEnableAccessKonashi()) {
            mNotifier.notifyKonashiError(KonashiErrorReason.NOT_READY);
            return false;
        }
        return true;
    }

    /**
     * I2Cが有効になっているかどうか
     * 無効である場合は{@link com.uxxu.konashi.lib.KonashiErrorReason#NOT_ENABLED_I2C}を発行する
     * @return I2Cが有効ならtrue
     */
    private boolean shouldEnableI2c() {
        if (!isEnableI2c()) {
            mNotifier.notifyKonashiError(KonashiErrorReason.NOT_ENABLED_I2C);
            return false;
        }
        return true;
    }

    /**
     * データ長が範囲内に収まっているかどうか
     * @param length データ長
     * @return 範囲内であればtrue
     */
    private boolean isValidLength(int length) {
        return (length > 0) && (length <= Konashi.I2C_DATA_MAX_LENGTH);
    }
}
