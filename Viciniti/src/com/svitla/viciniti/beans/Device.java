package com.svitla.viciniti.beans;

import android.bluetooth.BluetoothDevice;

public class Device {
	private String name;
	private short rssi;
	private boolean isMonitored;
	
	public Device(String name, short rssi, boolean isMonitored) {
		super();
		this.name = name;
		this.setRssi(rssi);
		this.isMonitored = isMonitored;
	}
	
	public Device(BluetoothDevice bluetoothDevice, short rssi, boolean isMonitored) {
		super();
		this.name = bluetoothDevice.getName();
		this.setRssi(rssi);
		this.isMonitored = isMonitored;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMonitored() {
		return isMonitored;
	}

	public void setMonitored(boolean isMonitored) {
		this.isMonitored = isMonitored;
	}

	public short getRssi() {
		return rssi;
	}

	public void setRssi(short rssi2) {
		this.rssi = rssi2;
	}

}
