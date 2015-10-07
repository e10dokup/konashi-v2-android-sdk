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
public class PwmPinModeActionTest {

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;

    private PwmPinModeAction mAction;

    private ArgumentCaptor<byte[]> mValueCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mValueCaptor = ArgumentCaptor.forClass(byte[].class);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
    }

    @Test
    public void hasValidParams_WithInvalidPin() throws Exception {
        mAction = new PwmPinModeAction(mService, 3, 1, 0x02);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_PIN_NUMBER);
    }

    @Test
    public void hasValidParams_WithInvalidMode() throws Exception {
        mAction = new PwmPinModeAction(mService, 2, 3, 0x02);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_MODE);
    }

    @Test
    public void hasValidParams_WithValidParams() throws Exception {
        mAction = new PwmPinModeAction(mService, 2, 1, 0x02);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void setValue_ToOutput() throws Exception {
        mAction = new PwmPinModeAction(mService, 2, 1, 0x01);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()[0]).isEqualTo((byte) 0x05);
    }

    @Test
    public void setValue_ToInput() throws Exception {
        mAction = new PwmPinModeAction(mService, 2, 0, 0x05);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()[0]).isEqualTo((byte) 0x01);
    }
}
