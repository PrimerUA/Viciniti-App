package com.svitla.viciniti.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.svitla.viciniti.R;
import com.svitla.viciniti.VicinityConstants;
import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.LevelsMonitor;
import com.svitla.viciniti.ui.fragments.LevelsListFragment;

public class MenuController {

    public static void deviceStatus(final ActionBarActivity activity) {
	// DisplayImageOptions options = new
	// DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher).showImageForEmptyUri(R.drawable.ic_launcher)
	// .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true)
	// .displayer(new RoundedBitmapDisplayer(Integer.MAX_VALUE)).build();

	final View deviceStatusView = activity.getLayoutInflater().inflate(R.layout.device_status_dialog, null);
	final Button choosePhoto = (Button) deviceStatusView.findViewById(R.id.choosePhoto);
	final ImageView userPhoto = (ImageView) deviceStatusView.findViewById(R.id.userPhoto);
	final ProgressBar progressBar = (ProgressBar) deviceStatusView.findViewById(R.id.dataProgress);
	final TextView name = (TextView) deviceStatusView.findViewById(R.id.gpsText);
	final TextView strength = (TextView) deviceStatusView.findViewById(R.id.accelerometerText);
	DataController.requestGPSData(activity, name, progressBar);
	DataController.requestAccelerometerData(activity, strength);
	if (PreferencesController.PhotoSaver.getUri() == null)
	    userPhoto.setImageResource(R.drawable.ic_launcher);
	else {
	    Bitmap bitmap = null;
	    try {
		bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), Uri.parse(PreferencesController.PhotoSaver.getUri()));
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    userPhoto.setImageBitmap(bitmap);
	}
	// ImageLoader.getInstance().displayImage(PreferencesController.PhotoSaver.getUri(),
	// userPhoto, options);
	final AlertDialog addDialog = new AlertDialog.Builder(activity).create();
	addDialog.setView(deviceStatusView);
	deviceStatusView.findViewById(R.id.okButton).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		addDialog.dismiss();
	    }
	});
	choosePhoto.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		addDialog.dismiss();
		choosePhoto(activity);
	    }
	});
	addDialog.show();
    }

    public static void plotControll(final ActionBarActivity activity, BluetoothDevice bluetoothDevice) {
	final View plotDetailsView = activity.getLayoutInflater().inflate(R.layout.plot_details_dialog, null);
	LinearLayout contentLayout = (LinearLayout) plotDetailsView.findViewById(R.id.devicesLayout);
	PlotController.init(activity, bluetoothDevice, contentLayout);
	final AlertDialog addDialog = new AlertDialog.Builder(activity).create();
	addDialog.setView(plotDetailsView);
	plotDetailsView.findViewById(R.id.exitButton).setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		addDialog.dismiss();
	    }
	});
	addDialog.show();
    }

    protected static void choosePhoto(ActionBarActivity activity) {
	File file = new File(VicinityConstants.VICINITI_DIR + "photos/avatar.jpg");
	if (!file.exists() && file.getParentFile().mkdirs())
	    try {
		file.createNewFile();
	    } catch (IOException e) {
	    }
	PreferencesController.PhotoSaver.setUri(Uri.fromFile(file));
	Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	activity.startActivityForResult(cameraIntent, VicinityConstants.TAKE_PHOTO_CODE);
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
		if (("-").equals(strength.getText().toString())) {
		    Toast.makeText(activity, "Complete your statement", Toast.LENGTH_SHORT).show();
		} else {
		    if (("").equals(name.getText().toString()) || ("").equals(strength.getText().toString()))
			Toast.makeText(activity, activity.getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
		    else {
			if (isUniqueLevel(Integer.valueOf(strength.getText().toString()))) {
			    Level level = new Level(name.getText().toString(), Integer.valueOf(strength.getText().toString()));
			    LevelsMonitor.getLevels().add(level);
			    LogController.appendFile("New security level added: " + level.getName() + " (" + level.getLevel() + ")", true);
			    LevelsListFragment levelsListFragment = (LevelsListFragment) activity.getSupportFragmentManager().findFragmentById(R.id.container);
			    levelsListFragment.refreshList();
			    addDialog.dismiss();
			} else {
			    Toast.makeText(activity, "Zone strength is not unique", Toast.LENGTH_SHORT).show();
			}
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
	else {
	    BluetoothController.enableBluetoothAndScan();
	}
    }
}
