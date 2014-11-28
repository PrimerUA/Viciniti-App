package com.svitla.viciniti.receivers;

import java.util.ArrayList;

import com.svitla.viciniti.R;
import com.svitla.viciniti.beans.Signal;
import com.svitla.viciniti.beans.SignalArray;
import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DevicesController;
import com.svitla.viciniti.controllers.LogController;
import com.svitla.viciniti.controllers.MenuController;
import com.svitla.viciniti.controllers.PlotController;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.SignalMonitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {

	private static final int ARRAY_SIZE = 20;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (!DevicesMonitor.getDevices().contains(bDevice)) { // new
																		// device
																		// found
					DevicesMonitor.getDevices().add(bDevice);
					LogController.appendFile("New device detected:" + bDevice.getName() + " (" + bDevice.getAddress() + ") RSSI: " + rssi, true);
				} else {
					LogController.appendFile("Device: " + bDevice.getName() + " (" + bDevice.getAddress() + ") RSSI update: " + rssi, true);
				}
				if (SignalMonitor.getMonitorArray().size() == 0) { // initialize
																	// signal
																	// array
																	// (first
																	// scan)
					SignalArray newSignalArray = new SignalArray();
					newSignalArray.setBluetoothDevice(bDevice);
					ArrayList<Signal> signals = new ArrayList<Signal>();
					signals.add(new Signal(rssi));
					newSignalArray.setSignalArray(signals);
					SignalMonitor.getMonitorArray().add(newSignalArray);
				} else
					// not first scan
					for (int i = 0; i < SignalMonitor.getMonitorArray().size(); i++)
						if (SignalMonitor.getMonitorArray().get(i).getBluetoothDevice().equals(bDevice)) // if
																											// device
																											// exists
																											// in
																											// signal
																											// array
							SignalMonitor.getMonitorArray().get(i).getSignalArray().add(new Signal(rssi)); // add
																											// new
																											// signal
																											// strength
																											// for
																											// the
																											// device
						else { // if signal array doesnot exist - initialize it
							SignalArray newSignalArray = new SignalArray();
							newSignalArray.setBluetoothDevice(bDevice);
							ArrayList<Signal> signals = new ArrayList<Signal>();
							signals.add(new Signal(rssi));
							newSignalArray.setSignalArray(signals);
							if (SignalMonitor.getMonitorArray().size() < ARRAY_SIZE)
								SignalMonitor.getMonitorArray().add(newSignalArray);
							else {
								for (int i1 = 0; i1 < ARRAY_SIZE / 2; i1++)
									SignalMonitor.getMonitorArray().remove(i1);
								SignalMonitor.getMonitorArray().add(newSignalArray);
							}
						}
				if (PlotController.isActive()) // user is on the selected device
												// plot
					PlotController.updateData(); // show him new data
				BluetoothController.updateDeviceList(bDevice.getName() + " - " + " RSSI: " + rssi + "dBm"); // update
																											// rssi
																											// on
																											// the
																											// main
																											// screen
				if (MenuController.DeviceStatus.getAlarmLayout() != null)
					MenuController.DeviceStatus.getAlarmLayout().setBackgroundResource(R.color.dialog_background);
				if (DevicesMonitor.getSupervisedDevices().size() > 0 && DevicesMonitor.getSupervisedDevices().contains(bDevice)) // if
																																	// this
																																	// device
																																	// is
																																	// supervised
					DevicesController.checkSupervisedDevices(context, rssi, bDevice); // perform
																						// rssi
																						// level
																						// check
				Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show();

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Toast.makeText(context, "Discovery round finished", Toast.LENGTH_SHORT).show();
				BluetoothController.getArrayAdapter().clear();
				DevicesMonitor.getDevices().clear();
				LogController.appendFile("Discovery round finished", false);
			}
			if (BluetoothController.isScanning())
				BluetoothController.enableBluetoothAndScan();
		} catch (Exception e) {
			LogController.appendFile("Crash aborted. Cause: " + e.getMessage(), false);
			Toast.makeText(context, "Crash aborted. Cause: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
}
