package com.uxxu.konashi.lib.action;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.uxxu.konashi.lib.KonashiErrorType;
import com.uxxu.konashi.lib.KonashiUUID;
import com.uxxu.konashi.lib.test.TestKonashiWriteCharacteristicAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import info.izumin.android.bletia.wrapper.BluetoothGattWrapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

/**
 * Created by e10dokup on 2016/01/18
 **/
public class KonashiWriteCharacteristicActionTest {
    private static final String TAG = KonashiWriteCharacteristicActionTest.class.getSimpleName();
    private final KonashiWriteCharacteristicActionTest self = this;

    @Mock private BluetoothGattService mService;
    @Mock private BluetoothGattCharacteristic mCharacteristic;
    @Mock private BluetoothGattWrapper mWrapper;

    private TestKonashiWriteCharacteristicAction mAction;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mCharacteristic.getService()).thenReturn(mService);
        when(mCharacteristic.getUuid()).thenReturn(KonashiUUID.I2C_WRITE_UUID);
    }

    @Test
    public void execute_withUnsupportedCharacteristic() throws Exception {
        mAction = new TestKonashiWriteCharacteristicAction(mService, null);
        assertThat(mAction.execute(mWrapper)).isEqualTo(false);
    }

    @Test
    public void execute_withValidateReturnError() throws Exception {
        mAction = new TestKonashiWriteCharacteristicAction(mService, null);
        mAction = spy(mAction);
        doReturn(KonashiErrorType.INVALID_CONDITION).when(mAction).validate();
        doReturn(mCharacteristic).when(mAction).getCharacteristic();
        assertThat(mAction.execute(mWrapper)).isEqualTo(false);
        verify(mAction).validate();
    }

    @Test
    public void execute_withValidateReturnNoError() throws Exception {
        mAction = new TestKonashiWriteCharacteristicAction(mService, null);
        mAction = spy(mAction);
        doReturn(KonashiErrorType.NO_ERROR).when(mAction).validate();
        doReturn(mCharacteristic).when(mAction).getCharacteristic();
        mAction.execute(mWrapper);
        verify(mAction).validate();
        verify(mAction).setValue();
    }


}