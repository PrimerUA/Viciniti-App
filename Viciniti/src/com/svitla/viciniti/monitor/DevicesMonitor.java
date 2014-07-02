package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import com.svitla.viciniti.beans.Device;

public class DevicesMonitor {
	private static ArrayList<Device> devices;

	public static ArrayList<Device> getDevices() {
		if (devices == null)
			devices = new ArrayList<Device>();
		return devices;
	}

	public static ArrayList<Device> getSupervisedDevices() {
		ArrayList<Device> supervisedDevices = new ArrayList<Device>();
		for (int i = 0; i < devices.size(); i++)
			if (devices.get(i).isMonitored())
				supervisedDevices.add(devices.get(i));
		return supervisedDevices;
	}

	public static boolean isSupervisedDevice(String name) {
		for (int i = 0; i < devices.size(); i++) {
			String deviceName = devices.get(i).getName();
			if (deviceName.equals(name))
				return true;
		}
		return false;
	}

}
