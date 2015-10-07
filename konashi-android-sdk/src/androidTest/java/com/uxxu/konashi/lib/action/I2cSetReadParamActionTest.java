package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.store.I2cStore;

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
 * Created by izumin on 9/21/15.
 */
public class I2cSetReadParamActionTest {

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;
    @Mock private I2cStore mStore;

    private I2cSetReadParamAction mAction;

    private ArgumentCaptor<byte[]> mValueCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mValueCaptor = ArgumentCaptor.forClass(byte[].class);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
        when(mStore.isEnabled()).thenReturn(true);
    }

    @Test
    public void hasValidParams_WithOverLength() throws Exception {
        mAction = new I2cSetReadParamAction(mService, 20, (byte) 0x53, mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.DATA_SIZE_TOO_LONG);
    }

    @Test
    public void hasValidParams_WithUnderValidLength() throws Exception {
        mAction = new I2cSetReadParamAction(mService, -1, (byte) 0x53, mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.DATA_SIZE_TOO_SHORT);
    }

    @Test
    public void hasValidParams_WithValidParams() throws Exception {
        mAction = new I2cSetReadParamAction(mService, 5, (byte) 0x53, mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void setValue_WithValidParams() throws Exception {
        mAction = new I2cSetReadParamAction(mService, 5, (byte) 0x53, mStore);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()[0]).isEqualTo((byte) 5);
        assertThat(mValueCaptor.getValue()[1]).isEqualTo((byte) 0xa7);
    }
}
