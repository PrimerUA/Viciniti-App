package com.svitla.viciniti.beans;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

public class SignalArray {
	private BluetoothDevice bluetoothDevice;
	private ArrayList<Signal> signalArray = new ArrayList<Signal>();

	public ArrayList<Signal> getSignalArray() {
		return signalArray;
	}

	public void setSignalArray(ArrayList<Signal> signalArray) {
		this.signalArray = signalArray;
	}

	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
	}

}
