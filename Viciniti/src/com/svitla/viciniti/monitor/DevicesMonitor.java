package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import com.svitla.viciniti.beans.Device;

public class DevicesMonitor {
	private static ArrayList<Device> devices = new ArrayList<Device>();

	public static ArrayList<Device> getDevices() {
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
		for (int i = 0; i < devices.size(); i++)
			if (devices.get(i).getName().equals(name))
				return true;
		return false;
	}
	
}
