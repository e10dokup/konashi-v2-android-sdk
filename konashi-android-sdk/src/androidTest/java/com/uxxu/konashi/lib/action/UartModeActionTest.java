package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUtils;
import com.uxxu.konashi.lib.store.UartStore;

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
public class UartModeActionTest {
    private static final String TAG = UartModeActionTest.class.getSimpleName();
    private final UartModeActionTest self = this;

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;
    @Mock private UartStore mStore;


    private UartModeAction mAction;

    private ArgumentCaptor<byte[]> mValueCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mValueCaptor = ArgumentCaptor.forClass(byte[].class);
        when(mService.getCharacteristic(any(UUID.class))).thenReturn(mCharacteristic);
        when(mStore.isEnabled()).thenReturn(true);
    }

    @Test
    public void hasValidParams_ForValidValue() throws Exception {
        mAction = new UartModeAction(mService, Konashi.UART_ENABLE, mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.NO_ERROR);
    }

    @Test
    public void hasValidParams_FotInvalidValue() throws Exception {
        mAction = new UartModeAction(mService, 100, mStore);
        assertThat(mAction.validate()).isEqualTo(KonashiErrorType.INVALID_MODE);
    }

    @Test
    public void setValue() throws Exception {
        mAction = new UartModeAction(mService, Konashi.UART_ENABLE, mStore);
        mAction.setValue();
        verify(mCharacteristic, times(1)).setValue(mValueCaptor.capture());
        byte[] value = mValueCaptor.getValue();
        byte[] expect = KonashiUtils.int2bytes(Konashi.UART_ENABLE);
        for(int i = 0; i < value.length; i++) {
            assertThat(value[i]).isEqualTo(expect[i]);
        }
    }
}