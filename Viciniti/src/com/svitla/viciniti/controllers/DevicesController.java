package com.svitla.viciniti.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.svitla.viciniti.beans.Device;
import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.LevelsMonitor;

public class DevicesController {

	public static void checkSupervisedDevices(Context context, short rssi) {
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++)
			if (rssi < LevelsMonitor.getLevels().get(i).getLevel()) {
				securityAlert(context, LevelsMonitor.getLevels().get(i).getLevel());
			}
	}

	private static void securityAlert(Context context, Integer securityLevel) {
		Level level = null;
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++)
			if (LevelsMonitor.getLevels().get(i).getLevel().equals(securityLevel))
				level = LevelsMonitor.getLevels().get(i);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Security alert!");
		for (int i = 0; i < DevicesMonitor.getSupervisedDevices().size(); i++)
			if (securityLevel == LevelsMonitor.getLevels().get(i).getLevel())
				alertDialogBuilder.setMessage("Device with name - " + DevicesMonitor.getSupervisedDevices().get(i).getName() + "has breached zone with name - "
						+ level.getName() + " (" + level.getLevel() + " dBm)");
		alertDialogBuilder.setCancelable(false).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public static void superviseDevice(final Context context, final ArrayAdapter<String> arrayAdapter, final int number) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Supervise this device?");
		alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Device device = null;
				for (int i = 0; i < DevicesMonitor.getDevices().size(); i++) {
					device = DevicesMonitor.getDevices().get(i);
					String compare = device.getName() + " - " + " RSSI: " + device.getRssi() + "dBm";
					if (compare.equals(arrayAdapter.getItem(number))) {
						device.setMonitored(true);
						PreferencesController.save();
						Toast.makeText(context, "Now supervising " + device.getName(), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
