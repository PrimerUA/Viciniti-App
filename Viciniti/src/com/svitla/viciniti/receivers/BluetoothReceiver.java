package com.svitla.viciniti.receivers;

import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DevicesController;
import com.svitla.viciniti.monitor.DevicesMonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {

    private ListView bluetoothListView;
    private ArrayAdapter<String> mArrayAdapter;

    public BluetoothReceiver(ListView bluetoothListView, ArrayAdapter<String> mArrayAdapter) {
	this.bluetoothListView = bluetoothListView;
	this.mArrayAdapter = mArrayAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	String action = intent.getAction();
	if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	    short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
	    BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	    if (!DevicesMonitor.getDevices().contains(bDevice)) {
		DevicesMonitor.getDevices().add(bDevice);
	    }
	    mArrayAdapter.add(bDevice.getName() + " - " + " RSSI: " + rssi + "dBm");
	    bluetoothListView.setAdapter(mArrayAdapter);
	    if (DevicesMonitor.getSupervisedDevices().size() > 0 && DevicesMonitor.getSupervisedDevices().contains(bDevice))
		DevicesController.checkSupervisedDevices(context, rssi, bDevice);
	    Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show();
	} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	    Toast.makeText(context, "Discovery round finished", Toast.LENGTH_SHORT).show();
	    mArrayAdapter.clear();
	    DevicesMonitor.getDevices().clear();
	}
	if (BluetoothController.isScanning())
	    BluetoothController.scanBluetooth();
    }

}
