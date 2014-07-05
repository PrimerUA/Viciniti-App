package com.svitla.viciniti.controllers;

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

	private static SharedPreferences prefs;

	public static void init(Context context) {
		prefs = context.getSharedPreferences(PREFERENCES_INIT, Context.MODE_PRIVATE);
		// load();
	}

	public static void save() {
		prefs.edit().clear().commit();
		Editor editor = prefs.edit();
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++) {
			if (!prefs.contains(LEVEL_NAMES + i)) {
				editor.putString(LEVEL_NAMES + i, LevelsMonitor.getLevels().get(i).getName());
				editor.putInt(LEVEL_LEVELS + i, LevelsMonitor.getLevels().get(i).getLevel());
			}
		}
		editor.commit();
	}

	public static void load() {
		int i = 0;
		LevelsMonitor.getLevels().clear();
		DevicesMonitor.getDevices().clear();
		while (prefs.contains(LEVEL_NAMES + i)) {
			Level level = new Level(prefs.getString(LEVEL_NAMES + i, null), prefs.getInt(LEVEL_LEVELS + i, 0));
			LevelsMonitor.getLevels().add(level);
			i++;
		}
		ifEmptyLevels();
	}

	private static void ifEmptyLevels() {
		if (LevelsMonitor.getLevels().size() == 0) {
			Level levelA = new Level("Zone A", 200);
			LevelsMonitor.getLevels().add(levelA);
			Level levelB = new Level("Zone B", 150);
			LevelsMonitor.getLevels().add(levelB);
		}
	}
}
