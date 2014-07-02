package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import com.svitla.viciniti.beans.Level;

public class LevelsMonitor {
	private static ArrayList<Level> levels = new ArrayList<Level>();

	public static ArrayList<Level> getLevels() {
		if (levels == null)
			levels = new ArrayList<Level>();
		return levels;
	}

	public static ArrayList<String> getInfo() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < levels.size(); i++)
			arrayList.add(levels.get(i).getName() + " - " + " RSSI: " + levels.get(i).getLevel().toString() + "dBm");
		return arrayList;
	}
}
