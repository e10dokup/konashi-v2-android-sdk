package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by izumin on 9/18/15.
 */
public class PioPinPullupActionTest {

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;

    private PioPinPullupAction mAction;

    private ArgumentCaptor<byte[]> mValueCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mValueCaptor = ArgumentCaptor.forClass(byte[].class);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
    }

    @Test
    public void hasValidParams_ForAllPinsWithInvalidPullups() throws Exception {
        mAction = new PioPinPullupAction(mService, 0x100);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_PIN_NUMBER);
    }

    @Test
    public void hasValidParams_ForAllPinsWithValidPullups() throws Exception {
        mAction = new PioPinPullupAction(mService, 0xff);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void hasValidParams_ForSinglePinWithInvalidPin() throws Exception {
        mAction = new PioPinPullupAction(mService, 8, 1, 0x02);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_PIN_NUMBER);
    }

    @Test
    public void hasValidParams_ForSinglePinWithInvalidPullup() throws Exception {
        mAction = new PioPinPullupAction(mService, 7, 2, 0x02);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_PULLUP_PARAM);
    }

    @Test
    public void hasValidParams_ForSinglePinWithValidParams() throws Exception {
        mAction = new PioPinPullupAction(mService, 7, 1, 0x02);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void setValue_ForAllPins() throws Exception {
        mAction = new PioPinPullupAction(mService, 0xff);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()[0]).isEqualTo((byte) 0xff);
    }

    @Test
    public void setValue_ForSinglePinToPullup() throws Exception {
        mAction = new PioPinPullupAction(mService, 7, 1, 0x02);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()[0]).isEqualTo((byte) 0x82);
    }

    @Test
    public void setValue_ForSinglePinToNoPulls() throws Exception {
        mAction = new PioPinPullupAction(mService, 5, 0, 0x32);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()[0]).isEqualTo((byte) 0x12);
    }
}
