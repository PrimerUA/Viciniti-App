package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import com.svitla.viciniti.beans.Level;

public class LevelsMonitor {
	private static ArrayList<Level> levels = new ArrayList<Level>();

	public static ArrayList<Level> getLevels() {
		return levels;
	}

	public static ArrayList<String> getInfo() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < levels.size(); i++)
			arrayList.add("NAME: " + levels.get(i).getName() + " RSSI: " + levels.get(i).getLevel().toString() + "dBm");
		return arrayList;
	}
}
