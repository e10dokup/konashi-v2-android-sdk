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
public class PwmLedDriveActionTest {

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;

    private PwmLedDriveAction mAction;

    private ArgumentCaptor<byte[]> mValueCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mValueCaptor = ArgumentCaptor.forClass(byte[].class);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
    }

    @Test
    public void hasValidParams_WithInvalidDutyRatio() throws Exception {
        mAction = new PwmLedDriveAction(mService, 2, 400, 3);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_DUTY_RATIO);
    }

    @Test
    public void hasValidParams_WithDutyOverPeriod() throws Exception {
        mAction = new PwmLedDriveAction(mService, 2, 400, 3);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_DUTY_RATIO);
    }

    @Test
    public void hasValidParams_WithValidDutyRatio() throws Exception {
        mAction = new PwmLedDriveAction(mService, 2, 30, 4000);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void setValue_WithValidParams() throws Exception {
        mAction = new PwmLedDriveAction(mService, 2, 30, 4000);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()).isEqualTo(
                new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x0b, (byte) 0xb8}
        );
    }
}
