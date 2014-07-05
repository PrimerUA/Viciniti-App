package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

public class DevicesMonitor {
	private static ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
	private static ArrayList<BluetoothDevice> supervisedDevices = new ArrayList<BluetoothDevice>();

	public static ArrayList<BluetoothDevice> getDevices() {
		return devices;
	}

	public static void setDevices(ArrayList<BluetoothDevice> devices) {
		DevicesMonitor.devices = devices;
	}

	public static ArrayList<BluetoothDevice> getSupervisedDevices() {
		return supervisedDevices;
	}

	public static void setSupervisedDevices(ArrayList<BluetoothDevice> supervisedDevices) {
		DevicesMonitor.supervisedDevices = supervisedDevices;
	}

}
