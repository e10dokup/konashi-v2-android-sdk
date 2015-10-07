package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.store.UartStore;
import com.uxxu.konashi.lib.util.UartUtils;

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
 * Created by e10dokup on 2015/09/23
 **/
public class UartWriteActionTest {
    private static final String TAG = UartWriteActionTest.class.getSimpleName();
    private final UartWriteActionTest self = this;

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;
    @Mock private UartStore mStore;

    private UartWriteAction mAction;

    private ArgumentCaptor<byte[]> mValueCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mValueCaptor = ArgumentCaptor.forClass(byte[].class);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
        when(mStore.isEnabled()).thenReturn(true);
    }

    @Test
    public void hasValidParams_ForValidValue_String() throws Exception {
        mAction = new UartWriteAction(mService, "Test", mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void hasValidParams_ForInvalidValue_TooLong() throws Exception {
        mAction = new UartWriteAction(mService, "TooLongStringHogeHogeHoge", mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.DATA_SIZE_TOO_LONG);
    }

    @Test
    public void setValue() throws Exception {
        byte[] testByteArray = UartUtils.toFormattedByteArray("Test");

        mAction = new UartWriteAction(mService, "Test", mStore);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        assertThat(mValueCaptor.getValue()).isEqualTo(testByteArray);
    }
}