package com.svitla.viciniti.controllers;

import com.svitla.viciniti.beans.Device;
import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.LevelsMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesController {

	public final static String PREFERENCES_INIT = "vicinity-preferences";
	public final static String LEVEL_NAMES = "level-name-#";
	public final static String LEVEL_LEVELS = "level-level-#";
	public final static String DEVICE_NAMES = "device-name-#";

	private static SharedPreferences prefs;

	public static void init(Context context) {
		prefs = context.getSharedPreferences(PREFERENCES_INIT, Context.MODE_PRIVATE);
		load();
	}

	public static void save() {
		Editor editor = prefs.edit();
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++) {
			editor.putString(LEVEL_NAMES + i, LevelsMonitor.getLevels().get(i).getName());
			editor.putInt(LEVEL_LEVELS + i, LevelsMonitor.getLevels().get(i).getLevel());
		}
		for (int i = 0; i < DevicesMonitor.getSupervisedDevices().size(); i++) {
			editor.putString(DEVICE_NAMES + i, DevicesMonitor.getSupervisedDevices().get(i).getName());
		}
		editor.commit();
	}

	public static void load() {
		Level level = new Level(prefs.getString(LEVEL_NAMES + 0, null), prefs.getInt(LEVEL_LEVELS + 0, 0));
		int i = 0;
		while (level.getLevel() != 0) {
			LevelsMonitor.getLevels().add(level);
			i++;
			level = new Level(prefs.getString(LEVEL_NAMES + i, null), prefs.getInt(LEVEL_LEVELS + i, 0));
		}
		Device device = new Device(prefs.getString(LEVEL_NAMES + 0, ""), Short.MIN_VALUE, true);
		int j = 0;
		while (!"".equals(device.getName())) {
			DevicesMonitor.getDevices().add(device);
			j++;
			device = new Device(prefs.getString(LEVEL_NAMES + j, ""), Short.MIN_VALUE, true);
		}
	}
}
