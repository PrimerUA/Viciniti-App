package com.svitla.viciniti.controllers;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.svitla.viciniti.VicinityConstants;
import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.LevelsMonitor;

public class DevicesController {

	private static AlertDialog alertDialog;

	public static void checkSupervisedDevices(Context context, short rssi, BluetoothDevice bluetoothDevice) {
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++)
			if (rssi < LevelsMonitor.getLevels().get(i).getLevel()) {
				securityAlert(context, LevelsMonitor.getLevels().get(i), bluetoothDevice);
			}
	}

	private static void securityAlert(final Context context, Level securityLevel, BluetoothDevice bluetoothDevice) {
		Window w = ((ActionBarActivity) context).getWindow();
		w.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		w.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		w.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		String message = "Device with name - " + bluetoothDevice.getName() + " has breached the zone named - " + securityLevel.getName() + " ("
				+ securityLevel.getLevel() + " dBm)";

		Intent intent = new Intent();
		intent.setAction("com.example.Broadcast");
		intent.putExtra(VicinityConstants.ALERT_MESSAGE, message);
		context.sendBroadcast(intent);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Security alert!");
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setCancelable(false).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(0);
			}
		});
		if (alertDialog != null)
			if (alertDialog.isShowing())
				alertDialog.dismiss();
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		LogController.appendFile(message);
	}

	public static void superviseDevice(final Context context, int number) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		final BluetoothDevice bluetoothDevice = DevicesMonitor.getDevices().get(number);
		if (!DevicesMonitor.getSupervisedDevices().contains(bluetoothDevice)) {
			alertDialogBuilder.setTitle("Start supervising this device?");
			alertDialogBuilder.setMessage(bluetoothDevice.getName());
			alertDialogBuilder.setCancelable(false).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					DevicesMonitor.getSupervisedDevices().add(bluetoothDevice);
					Toast.makeText(context, "Started supervising " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
					LogController.appendFile("Started supervising " + bluetoothDevice.getName());
				}
			}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} else {
			alertDialogBuilder.setTitle("Stop supervising this device?");
			alertDialogBuilder.setMessage(bluetoothDevice.getName());
			alertDialogBuilder.setCancelable(false).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					DevicesMonitor.getSupervisedDevices().remove(bluetoothDevice);
					Toast.makeText(context, "Stopped supervising " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
					LogController.appendFile("Stopped supervising " + bluetoothDevice.getName());
				}
			}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

	}
}
