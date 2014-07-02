package com.svitla.viciniti.receivers;

import com.svitla.viciniti.beans.Device;
import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DevicesController;
import com.svitla.viciniti.controllers.PreferencesController;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.LevelsMonitor;

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
			if (DevicesMonitor.isSupervisedDevice(bDevice.getName())) {
				Device device = new Device(bDevice, rssi, true);
				DevicesMonitor.getDevices().add(device);
			} else {
				Device device = new Device(bDevice, rssi, false);
				DevicesMonitor.getDevices().add(device);
			}
			PreferencesController.save();
			mArrayAdapter.add(bDevice.getName() + " - " + " RSSI: " + rssi + "dBm");
			bluetoothListView.setAdapter(mArrayAdapter);
			if (DevicesMonitor.getSupervisedDevices().size() > 0 && LevelsMonitor.getLevels().size() > 0)
				DevicesController.checkSupervisedDevices(context, rssi);
		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			Toast.makeText(context, "Discovery round finished", Toast.LENGTH_SHORT).show();
			mArrayAdapter.clear();
			DevicesMonitor.getDevices().clear();
		}
		if (BluetoothController.isScanning())
			BluetoothController.scanBluetooth();
	}

}
