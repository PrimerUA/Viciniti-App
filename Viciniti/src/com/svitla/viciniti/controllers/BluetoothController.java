package com.svitla.viciniti.controllers;

import com.svitla.viciniti.R;
import com.svitla.viciniti.VicinityConstants;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

    public static void init(Context context, SwipeRefreshLayout refreshLayout) {
	BluetoothController.context = context;
	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothController.refreshLayout = refreshLayout;
	isScanning = false;
	BluetoothController.mArrayAdapter = new ArrayAdapter<String>(context, R.layout.list_item);
	if (bluetoothAdapter == null) {
	    Toast.makeText(context, "No bluetooth adapter on this device!", Toast.LENGTH_SHORT).show();
	    ((ActionBarActivity) context).finish();
	}
    }
    
    public static BluetoothAdapter getBluetoothAdapter() {
	return bluetoothAdapter;
    }

    public static void enableBluetoothAndScan() {
	if (!bluetoothAdapter.isEnabled()) {
	    Log.v("onCreate", "Bluetooth Adapter off");
	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	    enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, VicinityConstants.DISCOVERABLE_DURATION);
	    context.startActivity(enableBtIntent);
	    LogController.appendFile("Enabling bluetooth", true);
	    scanBluetooth();
	} else {
	    Log.v("onCreate", "Bluetooth adapter is on");
	    scanBluetooth();
	}
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
	LogController.appendFile("Discovery started", false);
	bluetoothAdapter.startDiscovery();
    }

    public static void stopBluetooth() {
	if (bluetoothAdapter.isDiscovering()) {
	    isScanning = false;
	    bluetoothAdapter.cancelDiscovery();
	    Toast.makeText(context, "Scanning stopped", Toast.LENGTH_SHORT).show();
	    LogController.appendFile("Discovery stopped", false);
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
