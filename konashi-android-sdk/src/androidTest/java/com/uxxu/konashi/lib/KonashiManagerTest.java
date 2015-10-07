package com.uxxu.konashi.lib;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.uxxu.konashi.lib.action.AioAnalogReadAction;
import com.uxxu.konashi.lib.action.BatteryLevelReadAction;
import com.uxxu.konashi.lib.action.HardwareResetAction;
import com.uxxu.konashi.lib.action.I2cModeAction;
import com.uxxu.konashi.lib.action.I2cSendConditionAction;
import com.uxxu.konashi.lib.action.I2cSetReadParamAction;
import com.uxxu.konashi.lib.action.I2cWriteAction;
import com.uxxu.konashi.lib.action.PioDigitalWriteAction;
import com.uxxu.konashi.lib.action.PioPinModeAction;
import com.uxxu.konashi.lib.action.PioPinPullupAction;
import com.uxxu.konashi.lib.action.PwmDutyAction;
import com.uxxu.konashi.lib.action.PwmLedDriveAction;
import com.uxxu.konashi.lib.action.PwmPeriodAction;
import com.uxxu.konashi.lib.action.PwmPinModeAction;
import com.uxxu.konashi.lib.action.UartBaudrateAction;
import com.uxxu.konashi.lib.action.UartModeAction;
import com.uxxu.konashi.lib.action.UartWriteAction;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.UUID;

import info.izumin.android.bletia.Bletia;
import info.izumin.android.bletia.BletiaException;
import info.izumin.android.bletia.action.Action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by izumin on 7/31/15.
 */
@RunWith(AndroidJUnit4.class)
public class KonashiManagerTest extends AndroidTestCase {
    public static final String TAG = KonashiManagerTest.class.getSimpleName();

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;
    @Mock private Bletia mBletia;
    @Spy private KonashiManager mKonashiManager;

    private Deferred<BluetoothGattCharacteristic, BletiaException, Void> mDeferred;
    private Promise<BluetoothGattCharacteristic, BletiaException, Void> mPromise;

    @Captor private ArgumentCaptor<Action<BluetoothGattCharacteristic, UUID>> mActionCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mKonashiManager = new KonashiManager();
        mDeferred = new DeferredObject<>();
        mPromise = mDeferred.promise();
        when(mBletia.getService(any(UUID.class))).thenReturn(mService);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
        when(mCharacteristic.getUuid()).thenReturn(UUID.randomUUID());
        when(mBletia.execute(mActionCaptor.capture())).thenReturn(mPromise);
        Whitebox.setInternalState(mKonashiManager, "mBletia", mBletia);
    }

    @Test
    public void testPinMode() throws Exception {
        mKonashiManager.pinMode(0, 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PioPinModeAction.class);
    }

    @Test
    public void testPinModeAll() throws Exception {
        mKonashiManager.pinModeAll(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PioPinModeAction.class);
    }

    @Test
    public void testPinPullup() throws Exception {
        mKonashiManager.pinPullup(0, 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PioPinPullupAction.class);
    }

    @Test
    public void testPinPullupAll() throws Exception {
        mKonashiManager.pinPullupAll(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PioPinPullupAction.class);
    }

    @Test
    public void testDigitalWrite() throws Exception {
        mKonashiManager.digitalWrite(0, 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PioDigitalWriteAction.class);
    }

    @Test
    public void testDigitalWriteAll() throws Exception {
        mKonashiManager.digitalWriteAll(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PioDigitalWriteAction.class);
    }

    @Test
    public void testPwmMode_ForPwmEnable() throws Exception {
        mKonashiManager.pwmMode(0, Konashi.PWM_ENABLE);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PwmPinModeAction.class);
    }

    @Test
    public void testPwmMode_ForPwmLedEnable() throws Exception {
        // TODO: ちゃんとテストしたいけど良いやり方が思い付かない
        mKonashiManager.pwmMode(0, Konashi.PWM_ENABLE_LED_MODE);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PwmPinModeAction.class);
    }

    @Test
    public void testPwmPeriod() throws Exception {
        mKonashiManager.pwmPeriod(0, 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PwmPeriodAction.class);
    }

    @Test
    public void testPwmDuty() throws Exception {
        mKonashiManager.pwmDuty(0, 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PwmDutyAction.class);
    }

    @Test
    public void testPwmLedDrive_Float() throws Exception {
        mKonashiManager.pwmLedDrive(0, 0f);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PwmLedDriveAction.class);
    }

    @Test
    public void testPwmLedDrive_Double() throws Exception {
        mKonashiManager.pwmLedDrive(0, 0d);
        assertThat(mActionCaptor.getValue()).isInstanceOf(PwmLedDriveAction.class);
    }

    @Test
    public void testAnalogRead() throws Exception {
        mKonashiManager.analogRead(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(AioAnalogReadAction.class);
    }

    @Test
    public void testUartMode() throws Exception {
        mKonashiManager.uartMode(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(UartModeAction.class);
    }

    @Test
    public void testUartBaudrate() throws Exception {
        mKonashiManager.uartBaudrate(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(UartBaudrateAction.class);
    }

    @Test
    public void testUartWrite_Bytes() throws Exception {
        mKonashiManager.uartWrite(new byte[]{0});
        assertThat(mActionCaptor.getValue()).isInstanceOf(UartWriteAction.class);
    }

    @Test
    public void testUartWrite_String() throws Exception {
        mKonashiManager.uartWrite("test");
        assertThat(mActionCaptor.getValue()).isInstanceOf(UartWriteAction.class);
    }

    @Test
    public void testI2cMode() throws Exception {
        mKonashiManager.i2cMode(0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(I2cModeAction.class);
    }

    @Test
    public void testI2cStartCondition() throws Exception {
        // TODO: conditionの検証もしたい（VisibleForTesting annotationがほしい）
        mKonashiManager.i2cStartCondition();
        assertThat(mActionCaptor.getValue()).isInstanceOf(I2cSendConditionAction.class);
    }

    @Test
    public void testI2cRestartCondition() throws Exception {
        // TODO: conditionの検証もしたい（VisibleForTesting annotationがほしい）
        mKonashiManager.i2cRestartCondition();
        assertThat(mActionCaptor.getValue()).isInstanceOf(I2cSendConditionAction.class);
    }

    @Test
    public void testI2cStopCondition() throws Exception {
        // TODO: conditionの検証もしたい（VisibleForTesting annotationがほしい）
        mKonashiManager.i2cStopCondition();
        assertThat(mActionCaptor.getValue()).isInstanceOf(I2cSendConditionAction.class);
    }

    @Test
    public void testI2cWrite() throws Exception {
        mKonashiManager.i2cWrite(0, new byte[]{0}, (byte) 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(I2cWriteAction.class);
    }

    @Test
    public void testI2cRead() throws Exception {
        // TODO: ReadActionが呼ばれてるかどうかの検証もやりたいが，思いつかない
        mKonashiManager.i2cRead(0, (byte) 0);
        assertThat(mActionCaptor.getValue()).isInstanceOf(I2cSetReadParamAction.class);
    }

    @Test
    public void testReset() throws Exception {
        mKonashiManager.reset();
        assertThat(mActionCaptor.getValue()).isInstanceOf(HardwareResetAction.class);
    }

    @Test
    public void testGetBatteryLevel() throws Exception {
        mKonashiManager.getBatteryLevel();
        assertThat(mActionCaptor.getValue()).isInstanceOf(BatteryLevelReadAction.class);
    }

    @Test
    public void testGetSignalStrength() throws Exception {
        mKonashiManager.getSignalStrength();
        verify(mBletia, times(1)).readRemoteRssi();
    }
}
