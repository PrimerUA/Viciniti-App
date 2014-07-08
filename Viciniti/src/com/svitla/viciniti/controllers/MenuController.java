package com.svitla.viciniti.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.svitla.viciniti.R;
import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.LevelsMonitor;
import com.svitla.viciniti.ui.fragments.LevelsListFragment;

public class MenuController {

    public static void deviceStatus(final ActionBarActivity activity) {
	final View deviceStatusView = activity.getLayoutInflater().inflate(R.layout.device_status_dialog, null);
	final ProgressBar progressBar = (ProgressBar) deviceStatusView.findViewById(R.id.dataProgress);
	final TextView name = (TextView) deviceStatusView.findViewById(R.id.gpsText);
	final TextView strength = (TextView) deviceStatusView.findViewById(R.id.accelerometerText);
	DataController.requestGPSData(activity, name, progressBar);
	DataController.requestAccelerometerData(activity, strength);
	final AlertDialog addDialog = new AlertDialog.Builder(activity).create();
	addDialog.setView(deviceStatusView);
	deviceStatusView.findViewById(R.id.okButton).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		addDialog.dismiss();
	    }
	});
	addDialog.show();
    }

    public static void addLevel(final ActionBarActivity activity) {
	final View addDialogView = activity.getLayoutInflater().inflate(R.layout.add_security_dialog, null);
	final EditText name = (EditText) addDialogView.findViewById(R.id.nameSecurityEdit);
	final EditText strength = (EditText) addDialogView.findViewById(R.id.levelSecurityEdit);
	final AlertDialog addDialog = new AlertDialog.Builder(activity).create();
	addDialog.setView(addDialogView);
	addDialogView.findViewById(R.id.addButton).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (("").equals(name.getText().toString()) || ("").equals(strength.getText().toString()))
		    Toast.makeText(activity, activity.getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
		else {
		    if (isUniqueLevel(Integer.valueOf(strength.getText().toString()))) {
			Level level = new Level(name.getText().toString(), Integer.valueOf(strength.getText().toString()));
			LevelsMonitor.getLevels().add(level);
			LevelsListFragment levelsListFragment = (LevelsListFragment) activity.getSupportFragmentManager().findFragmentById(R.id.container);
			levelsListFragment.refreshList();
			addDialog.dismiss();
		    } else {
			Toast.makeText(activity, "Zone strength is not unique", Toast.LENGTH_SHORT).show();
		    }
		}
	    }
	});
	addDialogView.findViewById(R.id.cancelButton).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		addDialog.dismiss();
	    }
	});
	addDialog.show();
    }

    protected static boolean isUniqueLevel(Integer level) {
	for (int i = 0; i < LevelsMonitor.getLevels().size(); i++)
	    if (LevelsMonitor.getLevels().get(i).getLevel().equals(level))
		return false;
	return true;
    }

    public static void toggleScan(Context context) {
	if (BluetoothController.isScanning())
	    BluetoothController.stopBluetooth();
	else
	    BluetoothController.scanBluetooth();
    }
}
