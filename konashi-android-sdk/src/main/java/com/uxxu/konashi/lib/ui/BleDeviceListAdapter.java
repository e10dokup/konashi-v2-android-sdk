package com.uxxu.konashi.lib.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uxxu.konashi.lib.util.KonashiUtils;
import com.uxxu.konashi.lib.R;

import java.util.ArrayList;

/**
 * 周りにあるBLEデバイスをリストに表示するためのAdapter
 *
 * @author monakaz, YUKAI Engineering
 * http://konashi.ux-xu.com
 * ========================================================================
 * Copyright 2014 Yukai Engineering Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BleDeviceListAdapter extends BaseAdapter {
    /**
     * BLEデバイスとRSSIのArray
     */
    private final ArrayList<Pair<BluetoothDevice, Integer>> mBleDevices = new ArrayList<>();
    /**
     * 表示先のContext
     */
    private Context mContext;

    /**
     * コンストラクタ
     *
     * @param context 表示先のContext
     */
    public BleDeviceListAdapter(Context context) {
        mContext = context;
    }

    /**
     * BLEデバイスをリストに追加する
     *
     * @param device BLEデバイスオブジェクト
     * @param rssi   デバイスのRSSI
     */
    public void addDevice(BluetoothDevice device, int rssi) {
        for (int i = 0; i < mBleDevices.size(); i++) {
            Pair<BluetoothDevice, Integer> pair = mBleDevices.get(i);
            if (pair.first.equals(device)) {
                mBleDevices.set(i, Pair.create(device, rssi));
                notifyDataSetChanged();
                return;
            }
        }
        KonashiUtils.log("Device name: " + device.getName());
        mBleDevices.add(Pair.create(device, rssi));
    }

    /**
     * BLEデバイスのリストの中身をすべてクリアする
     */
    public void clearDevices() {
        mBleDevices.clear();
    }

    /**
     * リストの指定のポジションのBLEデバイスオブジェクトを取得する
     *
     * @param position 取得したいポジション
     * @return BLEデバイスオブジェクトとRSSIのPair
     */
    public Pair<BluetoothDevice, Integer> getDevice(int position) {
        return mBleDevices.get(position);
    }

    /**
     * BLEデバイスの個数を取得する
     */
    @Override
    public int getCount() {
        return mBleDevices.size();
    }

    /**
     * 指定のポジションのBLEデバイスオブジェクトを取得する
     *
     * @param position 取得したいBLEデバイスのポジション
     */
    @Override
    public Object getItem(int position) {
        return mBleDevices.get(position);
    }

    /**
     * アイテムIDを取得する
     *
     * @param position 取得したいポジション
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * BLEデバイスリストの要素Viewを返す
     *
     * @param position    生成するViewのポジション
     * @param convertView そのポジションのView
     * @param parent      返すViewが所属するViewGroup
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.device_list_element, parent, false);
        }

        Pair<BluetoothDevice, Integer> pair = mBleDevices.get(position);
        BluetoothDevice device = pair.first;
        int rssi = pair.second;

        String deviceName = device.getName();
        TextView name = (TextView) v.findViewById(R.id.konashi_lib_device_name);
        if (deviceName != null && deviceName.length() > 0) {
            name.setText(device.getName());
        } else {
            name.setText(R.string.konashi_lib_device_list_element_unknown_device);
        }

        TextView address = (TextView) v.findViewById(R.id.konashi_lib_device_address);
        address.setText(device.getAddress());

        TextView rssiTextView = (TextView) v.findViewById(R.id.konashi_lib_device_rssi);
        rssiTextView.setText(String.format("RSSI: %ddB", rssi));

        return v;
    }

}
