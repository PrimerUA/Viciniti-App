package com.svitla.viciniti.controllers;

import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.LevelsMonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

public class PreferencesController {

	public final static String PREFERENCES_INIT = "vicinity-preferences";
	public final static String LEVEL_NAMES = "level-name-#";
	public final static String LEVEL_LEVELS = "level-level-#";
	public final static String PHOTO_URI = "photo-uri";

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
			Level levelA = new Level("Zone A", -75);
			LevelsMonitor.getLevels().add(levelA);
			Level levelB = new Level("Zone B", -100);
			LevelsMonitor.getLevels().add(levelB);
			Level levelC = new Level("Zone C", -150);
			LevelsMonitor.getLevels().add(levelC);
		}
	}

	public static class PhotoSaver {
		private static String uri;

		public static String getUri() {
			if (uri == null)
				PhotoSaver.load();
			return uri;
		}

		public static void setUri(Uri uri) {
			PhotoSaver.uri = uri.toString();
			PhotoSaver.save();
		}

		private static void save() {
			if (uri != null) {
				Editor editor = prefs.edit();
				editor.putString(PHOTO_URI, uri);
				editor.commit();
			}
		}

		private static void load() {
			uri = prefs.getString(PHOTO_URI, null);
		}
	}
}
