package com.uxxu.konashi.lib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.uxxu.konashi.lib.entities.KonashiReadMessage;
import com.uxxu.konashi.lib.entities.KonashiWriteMessage;
import com.uxxu.konashi.lib.events.KonashiAnalogEvent;
import com.uxxu.konashi.lib.events.KonashiConnectionEvent;
import com.uxxu.konashi.lib.events.KonashiDeviceInfoEvent;
import com.uxxu.konashi.lib.events.KonashiDigitalEvent;
import com.uxxu.konashi.lib.events.KonashiEvent;
import com.uxxu.konashi.lib.events.KonashiUartEvent;
import com.uxxu.konashi.lib.ui.BleDeviceListAdapter;
import com.uxxu.konashi.lib.ui.BleDeviceSelectionDialog;
import com.uxxu.konashi.lib.ui.BleDeviceSelectionDialog.OnBleDeviceSelectListener;

import java.util.UUID;

/**
 * konashiのベース部分(BLEまわり)を管理するクラス
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
public class KonashiBaseManager implements BluetoothAdapter.LeScanCallback, OnBleDeviceSelectListener {
    
    /*************************
     * konashi constants
     *************************/
      
    private static final long SCAN_PERIOD = 3000;
    private static final String KONAHSHI_DEVICE_NAME = "konashi2";
    private static final long KONASHI_SEND_PERIOD = 10;
    
    
    /*****************************
     * BLE constants
     *****************************/
    
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_STATE_CHANGE_BT = 2;
    
    private enum BleStatus {
        DISCONNECTED,
        SCANNING,
        SCAN_END,
        DEVICE_FOUND,
        CONNECTED,
        SERVICE_NOT_FOUND,
        SERVICE_FOUND,
        CHARACTERISTICS_NOT_FOUND,
        CHARACTERISTICS_FOUND,
        READY,
        CLOSED;
        
        public Message message() {
            Message message = new Message();
            message.obj = this;
            return message;
        }
    }
    
    
    /*****************************
     * Members
     *****************************/
    
    // FIFO buffer
    private KonashiMessageHandler mKonashiMessageHandler;

    // BLE members
    private BleStatus mStatus = BleStatus.DISCONNECTED;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private Handler mFindHandler;
    private Runnable mFindRunnable;
    private BleDeviceListAdapter mBleDeviceListAdapter;
    private boolean mIsShowKonashiOnly = true;
    private boolean mIsSupportBle = false;
    private boolean mIsInitialized = false;
    private String mKonashiName;
    
    // konashi event listenr
    protected KonashiNotifier mNotifier;
    
    // UI members
    private Activity mActivity;
    private BleDeviceSelectionDialog mDialog;

    
    /////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////// 
    
    public KonashiBaseManager(){
        mNotifier = new KonashiNotifier();
    }
    
    /**
     * 初期化
     * @param context コンテキスト(activityよりgetApplicationContext()が良い)
     */
    public void initialize(Context context){
        mIsSupportBle = isSupportBle(context);
        if(!mIsSupportBle){
            // BLE not supported. can't initialize
            Toast.makeText(context, R.string.konashi_lib_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // initialize BLE
        mBleDeviceListAdapter = new BleDeviceListAdapter(context);
        
        mBluetoothManager = (BluetoothManager)context.getSystemService(context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        
        mDialog = new BleDeviceSelectionDialog(mBleDeviceListAdapter, this);
        
        mFindHandler = new Handler();
        mFindRunnable = new Runnable() {
            @Override
            public void run() {
                if(mStatus.equals(BleStatus.SCANNING)){
                    mBluetoothAdapter.stopLeScan(KonashiBaseManager.this);
                    setStatus(BleStatus.SCAN_END);
                    
                    if(mKonashiName!=null){
                        // called findWithName. dispatch PERIPHERAL_NOT_FOUND event
                        notifyKonashiEvent(KonashiConnectionEvent.PERIPHERAL_NOT_FOUND);
                    } else {
                        mDialog.finishFinding();
                        
                        if(mBleDeviceListAdapter.getCount()==0){
                            notifyKonashiEvent(KonashiConnectionEvent.PERIPHERAL_NOT_FOUND);
                        }
                    }
                }
            }
        };
        
        mIsInitialized = true;
    }
    
    /**
     * konashiを見つける(konashiのみBLEデバイスリストに表示する)
     * @param activity BLEデバイスリストを表示する先のActivity
     */
    public void find(Activity activity){
        find(activity, true, null);
    }
    
    /**
     * konashiを見つける
     * @param activity BLEデバイスリストを表示する先のActivity
     * @param isShowKonashiOnly konashiだけを表示するか、すべてのBLEデバイスを表示するか
     */
    public void find(Activity activity, boolean isShowKonashiOnly){
        find(activity, isShowKonashiOnly, null);
    }
    
    /**
     * 名前を指定してkonashiを探索。
     * @param activity BLEデバイスリストを表示する先のActivity
     * @param name konashiの緑色のチップに貼られているシールに書いている数字(例: konashi#0-1234)
     */
    public void findWithName(Activity activity, String name){
        find(activity, true, name);
    }
    
    private void find(Activity activity, boolean isShowKonashiOnly, String name){
        // check initialized
        if(!mIsInitialized || mStatus.equals(BleStatus.READY)){
            notifyKonashiError(KonashiErrorReason.ALREADY_READY);
            return;
        }
        
        KonashiUtils.log("find start");
        
        mActivity = activity;
        
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        
        mIsShowKonashiOnly = isShowKonashiOnly;
        
        mFindHandler.postDelayed(mFindRunnable, SCAN_PERIOD);
        
        mBleDeviceListAdapter.clearDevices();
        
        mBluetoothAdapter.stopLeScan(this);
        mBluetoothAdapter.startLeScan(this);
        setStatus(BleStatus.SCANNING);
        
        mKonashiName = name;
        
        if(mKonashiName==null){
            mDialog.show(activity);
        }

    }
    
    /**
     * konashiとの接続を解除する
     */
    public void disconnect(){
        if(mBluetoothGatt!=null){
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            setStatus(BleStatus.DISCONNECTED);
        }

        mKonashiMessageHandler.stop();
        mKonashiMessageHandler = null;
    }
    
    /**
     * disconnectし、変数をリセットする
     */
    public void close(){
        if(mStatus.equals(BleStatus.CLOSED)){
            return;
        }
        
        // disconnect if not-disconnected & close
        if(!mStatus.equals(BleStatus.DISCONNECTED)){
            disconnect();
        }

        // remove all observers
        mNotifier.removeAllObservers();
        
        setStatus(BleStatus.CLOSED);
    }
    
    /**
     * konashiと接続済みかどうか
     * @return konashiと接続済みだったらtrue
     */
    public boolean isConnected(){
        return mStatus.equals(BleStatus.CONNECTED) ||
               mStatus.equals(BleStatus.CHARACTERISTICS_FOUND) ||
               mStatus.equals(BleStatus.SERVICE_FOUND) ||
               mStatus.equals(BleStatus.READY)
        ;
    }
    
    /**
     * konashiを使える状態になっているか
     * @return konashiを使えるならtrue
     */
    public boolean isReady(){
        return mStatus.equals(BleStatus.READY);
    }
    
    /**
     * 接続しているkonashiの名前を取得する
     * @return konashiの名前
     */
    public String getPeripheralName(){
        if(mBluetoothGatt!=null && mBluetoothGatt.getDevice()!=null){
            return mBluetoothGatt.getDevice().getName();
        } else {
            return "";
        }
    }
    
    protected void readRemoteRssi(){
        if(!isEnableAccessKonashi() || mBluetoothGatt==null){
            notifyKonashiError(KonashiErrorReason.NOT_READY);
            return;
        }
        
        mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Konashiへのアクセスが可能かどうか
     * @return
     */
    public boolean isEnableAccessKonashi(){
        return mIsSupportBle && mIsInitialized && mStatus.equals(BleStatus.READY);
    }

    /****************************
     * BLE Override methods
     ****************************/
    
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        KonashiUtils.log("DeviceName: " + device.getName());
        
        if(mKonashiName!=null){
            // called findWithName
            if(device.getName().equals(mKonashiName)){
                onSelectBleDevice(device);
            }
            
            return;
        }

        // runOnUiThread to be able to tap list element on BLE DeviceList dialog
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(mIsShowKonashiOnly==false || device.getName().startsWith(KONAHSHI_DEVICE_NAME)){
                        mBleDeviceListAdapter.addDevice(device);
                        mBleDeviceListAdapter.notifyDataSetChanged();
                    }
                }catch(NullPointerException e){

                }
            }
        });
    }

    @Override
    public void onSelectBleDevice(BluetoothDevice device) {    
        KonashiUtils.log("Selected device: " + device.getName());
        
        if(mStatus.equals(BleStatus.SCANNING)){
            stopFindHandler();
            mBluetoothAdapter.stopLeScan(KonashiBaseManager.this);        
        }
        setStatus(BleStatus.DEVICE_FOUND);
        
        connect(device);
    }

    @Override
    public void onCancelSelectingBleDevice() {
        notifyKonashiEvent(KonashiConnectionEvent.CANCEL_SELECT_KONASHI);

        if(mStatus.equals(BleStatus.SCANNING)){
            stopFindHandler();
            mBluetoothAdapter.stopLeScan(KonashiBaseManager.this);
            setStatus(BleStatus.DISCONNECTED);            
        }
    }
    
    
    /////////////////////////////////////////////////////////////
    // Private methods
    /////////////////////////////////////////////////////////////
    
    private boolean isSupportBle(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private void stopFindHandler(){
        mFindHandler.removeCallbacks(mFindRunnable);
    }
    
    private void setStatus(BleStatus status) {
        mStatus = status;
        KonashiUtils.log("konashi_status: " + mStatus.name());

        if (status == BleStatus.READY) {
            notifyKonashiEvent(KonashiConnectionEvent.READY);
        }
    }
    
    
    /******************************************
     * BLE methods
     ******************************************/
    
    private void connect(BluetoothDevice device){
        mBluetoothGatt = device.connectGatt(mActivity.getApplicationContext(), false, mBluetoothGattCallback);
        HandlerThread thread = new HandlerThread(device.getName());
        thread.start();
        mKonashiMessageHandler = new KonashiMessageHandler(thread);
        mKonashiMessageHandler.setBluetoothGatt(mBluetoothGatt);
    }
    
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            KonashiUtils.log("onCharacteristicChanged: " + characteristic.getUuid());

            KonashiCharacteristic
                    .valueOf(characteristic.getUuid())
                    .handle(characteristic, mNotifier);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            KonashiUtils.log("onCharacteristicRead: " + characteristic.getUuid());
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                KonashiCharacteristic
                        .valueOf(characteristic.getUuid())
                        .handle(characteristic, mNotifier);
            } else {
                KonashiUtils.log("onCharacteristicRead GATT_FAILURE");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            KonashiCharacteristic
                    .valueOf(characteristic.getUuid())
                    .handle(characteristic, mNotifier);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            KonashiUtils.log("onConnectionStateChange: " + connectionStatus2string(status) + " -> " + connectionStatus2string(newState));
            
            if(newState == BluetoothProfile.STATE_CONNECTED){
                setStatus(BleStatus.CONNECTED);
                
                notifyKonashiEvent(KonashiConnectionEvent.CONNECTED);
                
                gatt.discoverServices();
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                setStatus(BleStatus.DISCONNECTED);
                
                notifyKonashiEvent(KonashiConnectionEvent.DISCONNECTED);
                
                mBluetoothGatt = null;
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            // TODO Auto-generated method stub
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if(status==BluetoothGatt.GATT_SUCCESS){
                onUpdateSignalSrength(rssi);
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            KonashiUtils.log("onServicesDiscovered start");
            
            if(status == BluetoothGatt.GATT_SUCCESS){
                BluetoothGattService service = gatt.getService(KonashiUUID.KONASHI_SERVICE_UUID);

                // Check konashi service
                if (service == null) {
                    setStatus(BleStatus.SERVICE_NOT_FOUND);
                    return;
                }
                
                setStatus(BleStatus.SERVICE_FOUND);
                    
                // Check the characteristics
                if(!isAvailableCharacteristic(KonashiUUID.BATTERY_SERVICE_UUID, KonashiUUID.BATTERY_LEVEL_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PIO_SETTING_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PIO_PULLUP_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PIO_OUTPUT_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PIO_INPUT_NOTIFICATION_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PWM_CONFIG_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PWM_PARAM_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.PWM_DUTY_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.ANALOG_DRIVE_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.ANALOG_READ0_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.ANALOG_READ1_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.ANALOG_READ2_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.I2C_CONFIG_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.I2C_START_STOP_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.I2C_WRITE_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.I2C_READ_PARAM_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.I2C_READ_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.UART_CONFIG_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.UART_BAUDRATE_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.UART_TX_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.UART_RX_NOTIFICATION_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.HARDWARE_RESET_UUID) ||
                   !isAvailableCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, KonashiUUID.HARDWARE_LOW_BAT_NOTIFICATION_UUID)
                ){
                    setStatus(BleStatus.CHARACTERISTICS_NOT_FOUND);
                    return;
                }
        
                // available all konashi characteristics
                setStatus(BleStatus.CHARACTERISTICS_FOUND);
                
                // enable notifications
                if(!enableNotification(KonashiUUID.PIO_INPUT_NOTIFICATION_UUID)){
                    setStatus(BleStatus.CHARACTERISTICS_NOT_FOUND);
                    return;
                }
                
                if(!enableNotification(KonashiUUID.UART_RX_NOTIFICATION_UUID)){
                    setStatus(BleStatus.CHARACTERISTICS_NOT_FOUND);
                    return;
                }
                
                if(!enableNotification(KonashiUUID.HARDWARE_LOW_BAT_NOTIFICATION_UUID)){
                    setStatus(BleStatus.CHARACTERISTICS_NOT_FOUND);
                    return;
                }
                
                setStatus(BleStatus.READY);
            }
        }
    };
    
    private BluetoothGattCharacteristic getCharacteristic(UUID serviceUuid, UUID characteristicUuid){
        if(mBluetoothGatt!=null){
            BluetoothGattService service = mBluetoothGatt.getService(serviceUuid);
            return service.getCharacteristic(characteristicUuid);
        } else {
            return null;
        }        
    }
    
    private boolean isAvailableCharacteristic(UUID serviceUuid, UUID characteristicUuid){
        KonashiUtils.log("check characteristic service: " + serviceUuid.toString() + ", chara: " + characteristicUuid.toString());

        return getCharacteristic(serviceUuid, characteristicUuid) != null;
    }
    
    private boolean enableNotification(UUID uuid){
        KonashiUtils.log("try enable notification: " + uuid.toString());
        
        BluetoothGattCharacteristic characteristic = getCharacteristic(KonashiUUID.KONASHI_SERVICE_UUID, uuid);
        if(mBluetoothGatt!=null && characteristic!=null){
            boolean registered = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(KonashiUUID.CLIENT_CHARACTERISTIC_CONFIG);
            // HACK descriptorを呼び出すのはいいけど対象のcharacteristicがdescriptorを備えていない（処理スキップで動くっちゃ動く）
            if(descriptor!=null){
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }else{
                KonashiUtils.log("Descriptor is null!");
            }
            return registered;
        } else {
            return false;
        }
    }
    
    private String connectionStatus2string(int status){
        switch(status){
            case BluetoothProfile.STATE_CONNECTED:
                return "CONNECTED";
            case BluetoothProfile.STATE_CONNECTING:
                return "CONNECTING";
            case BluetoothProfile.STATE_DISCONNECTED:
                return "DISCONNECTED";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "DISCONNECTING";
            default:
                return "UNKNOWN";
        }
    }
    
    
    /*********************************
     * FIFO send buffer
     *********************************/

    protected void addWriteMessage(UUID uuid, byte[] value){
        addWriteMessage(new KonashiWriteMessage(uuid, value));
    }

    protected void addWriteMessage(KonashiWriteMessage message){
        mKonashiMessageHandler.sendMessage(message.getMessage(mKonashiMessageHandler));
    }

    protected void addReadMessage(UUID characteristicUuid){
        addReadMessage(KonashiUUID.KONASHI_SERVICE_UUID, characteristicUuid);
    }
    
    protected void addReadMessage(UUID serviceUuid, UUID characteristicUuid){
        addReadMessage(new KonashiReadMessage(serviceUuid, characteristicUuid));
    }

    protected void addReadMessage(KonashiReadMessage message) {
        mKonashiMessageHandler.sendMessage(message.getMessage(mKonashiMessageHandler));
    }

    /******************************
     * Konashi observer methods
     ******************************/
    
    /**
     * オブザーバにイベントを通知する（パラメータ2つ）
     * @param event 通知するイベント名
     * @param param0 パラメータその1
     * @param param1 パラメータその2
     */
    protected void notifyKonashiEvent(KonashiEvent event, Object param0, Object param1){
        mNotifier.notifyKonashiEvent(event, param0, param1);
    }
    
    /**
     * オブザーバにイベントを通知する（パラメータ1つ）
     * @param event 通知するイベント名
     * @param param0 パラメータその1
     */
    protected void notifyKonashiEvent(KonashiEvent event, Object param0){
        mNotifier.notifyKonashiEvent(event, param0, null);
    }
    
    /**
     * オブザーバにイベントを通知する（パラメータなし）
     * @param event 通知するイベント名
     */
    protected void notifyKonashiEvent(KonashiEvent event){
        mNotifier.notifyKonashiEvent(event, null, null);
    }
    
    /**
     * オブザーバにエラーイベントを通知する
     * @param errorReason 通知するエラーの原因
     */
    protected void notifyKonashiError(KonashiErrorReason errorReason){
        mNotifier.notifyKonashiError(errorReason);
    }
    
    
    /***************************************
     * Konashi notification event handler
     ***************************************/
        
    /**
     * PIOの入力の状態が変更された時
     * @param value PIO8bitで表現
     */
    protected void onUpdatePioInput(byte value){
        notifyKonashiEvent(KonashiDigitalEvent.UPDATE_PIO_INPUT, value);
    }
    
    /**
     * AIOの特定の値が取得できた時
     * @param pin ピン番号 AIO0〜AIO2
     * @param value アナログ値
     */
    protected void onUpdateAnalogValue(int pin, int value){
        notifyKonashiEvent(KonashiAnalogEvent.UPDATE_ANALOG_VALUE, pin, value);
        
        if(pin==Konashi.AIO0)
            notifyKonashiEvent(KonashiAnalogEvent.UPDATE_ANALOG_VALUE_AIO0, value);
        else if(pin==Konashi.AIO1)
            notifyKonashiEvent(KonashiAnalogEvent.UPDATE_ANALOG_VALUE_AIO1, value);
        else
            notifyKonashiEvent(KonashiAnalogEvent.UPDATE_ANALOG_VALUE_AIO2, value);
    }
    
    /**
     * のRxからデータを受信した時
     * @param data 受信データ
     */
    protected void onRecieveUart(byte[] data){
        notifyKonashiEvent(KonashiUartEvent.UART_RX_COMPLETE, data);
    }

//     for konashi v1 (old codes)
//    protected void onRecieveUart(byte data){
//        notifyKonashiEvent(KonashiEvent.UART_RX_COMPLETE, data);
//    }
    
    /**
     * konashiのバッテリーのレベルを取得できた時
     * @param level バッテリー(%)
     */
    protected void onUpdateBatteryLevel(int level){
        notifyKonashiEvent(KonashiDeviceInfoEvent.UPDATE_BATTERY_LEVEL, level);
    }
    
    /**
     * konashiの電波強度を取得できた時
     * @param rssi 電波強度(db) 距離が近いと-40db, 距離が遠いと-90db程度になる
     */
    protected void onUpdateSignalSrength(int rssi){
        notifyKonashiEvent(KonashiDeviceInfoEvent.UPDATE_SIGNAL_STRENGTH, rssi);
    }
}