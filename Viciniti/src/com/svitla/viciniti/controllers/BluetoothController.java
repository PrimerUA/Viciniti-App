package com.svitla.viciniti.controllers;

import com.svitla.viciniti.R;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothController {
	private static Context context;
	private static BluetoothAdapter bluetoothAdapter;
	private static SwipeRefreshLayout refreshLayout;
	private static ListView bluetoothListView;
	private static ArrayAdapter<String> mArrayAdapter;
	
	private static boolean isScanning;
	
	public static void init(Context context, BluetoothAdapter bluetoothAdapter, SwipeRefreshLayout refreshLayout) {
		BluetoothController.context = context;
		BluetoothController.bluetoothAdapter = bluetoothAdapter;
		BluetoothController.refreshLayout = refreshLayout;
		isScanning = false;
		BluetoothController.mArrayAdapter = new ArrayAdapter<String>(context, R.layout.list_item);
	}
	
	public static void setBluetoothListView(ListView bluetoothListView) {
		BluetoothController.bluetoothListView = bluetoothListView;
	}

	public static void scanBluetooth() {
		isScanning = true;
		if (!refreshLayout.isRefreshing())
			refreshLayout.setRefreshing(true);
		if (bluetoothAdapter.isDiscovering())
			return;
		Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show();
		bluetoothAdapter.startDiscovery();
	}

	public static void stopBluetooth() {
		if (bluetoothAdapter.isDiscovering()) {
			isScanning = false;
			bluetoothAdapter.cancelDiscovery();
			Toast.makeText(context, "Scanning stopped", Toast.LENGTH_SHORT).show();
			if (refreshLayout.isRefreshing())
				refreshLayout.setRefreshing(false);
		}
	}
	
	public static boolean isScanning() {
		return isScanning;
	}

	public static void updateDeviceList(String deviceInfo) {
		mArrayAdapter.add(deviceInfo);
		bluetoothListView.setAdapter(mArrayAdapter);
	}

	public static ArrayAdapter<String> getArrayAdapter() {
		return mArrayAdapter;
	}

}
