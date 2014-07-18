package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

import com.svitla.viciniti.beans.SignalArray;

public class SignalMonitor {
	private static ArrayList<SignalArray> monitorArray = new ArrayList<SignalArray>();

	public static ArrayList<SignalArray> getMonitorArray() {
		return monitorArray;
	}

	public static SignalArray getArrayByDevice(BluetoothDevice bluetoothDevice) {
		for (int i = 0; i < monitorArray.size(); i++)
			if (monitorArray.get(i).getBluetoothDevice().equals(bluetoothDevice))
				return monitorArray.get(i);
		return null;
	}
}
