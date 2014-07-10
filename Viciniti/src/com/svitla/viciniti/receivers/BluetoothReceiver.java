package com.svitla.viciniti.receivers;

import java.util.ArrayList;

import com.svitla.viciniti.beans.Signal;
import com.svitla.viciniti.beans.SignalArray;
import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DevicesController;
import com.svitla.viciniti.controllers.PlotController;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.SignalMonitor;

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
	    for (int i = 0; i < SignalMonitor.getMonitorArray().size(); i++)
		if (SignalMonitor.getMonitorArray().get(i).getBluetoothDevice().equals(bDevice))
		    SignalMonitor.getMonitorArray().get(i).getSignalArray().add(new Signal(rssi));
		else {
		    SignalArray newSignalArray = new SignalArray();
		    newSignalArray.setBluetoothDevice(bDevice);
		    ArrayList<Signal> signals = new ArrayList<Signal>();
		    signals.add(new Signal(rssi));
		    newSignalArray.setSignalArray(signals);
		    SignalMonitor.getMonitorArray().add(newSignalArray);
		}
	    if (PlotController.isActive())
		PlotController.updateData();
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
