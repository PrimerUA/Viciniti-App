package com.svitla.viciniti;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.splunk.mint.Mint;
import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DataController;
import com.svitla.viciniti.controllers.LogController;
import com.svitla.viciniti.controllers.MenuController;
import com.svitla.viciniti.controllers.PlotController;
import com.svitla.viciniti.controllers.PreferencesController;
import com.svitla.viciniti.receivers.BluetoothReceiver;
import com.svitla.viciniti.ui.fragments.DeviceListFragment;
import com.svitla.viciniti.ui.fragments.LevelsListFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnRefreshListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static int currentFragment;

	private SwipeRefreshLayout mRefreshLayout;

	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Mint.initAndStartSession(this, "e29b01f9");
		Mint.enableDebug();
		Mint.enableLogging(true);
		Mint.setLogging("*:W");
		
		setContentView(R.layout.activity_main);
		PreferencesController.init(this);
		LogController.init(this);
		LogController.showBuildVersionToast();
		
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
		mRefreshLayout.setColorSchemeColors(Color.YELLOW, Color.BLACK, Color.WHITE, Color.BLACK);
		mRefreshLayout.setOnRefreshListener(this);

		BluetoothController.init(this, mRefreshLayout);
		BluetoothController.enableBluetoothAndScan();
		DataController.requestGPSData(this, null, null);
		DataController.requestAccelerometerData(this, null);

		// final ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayShowTitleEnabled(false);
		// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// actionBar.setListNavigationCallbacks(new
		// ArrayAdapter<String>(actionBar.getThemedContext(),
		// android.R.layout.simple_list_item_1, android.R.id.text1,
		// new String[] { getString(R.string.title_section1),
		// getString(R.string.title_section2),
		// getString(R.string.title_section3), }), this);

		showFragment(VicinityConstants.FRAGMENT_MAIN, 0);

		mReceiver = new BluetoothReceiver();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		IntentFilter discoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, discoveryFinished);
		IntentFilter rssiChanged = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
		registerReceiver(mReceiver, rssiChanged);
		// IntentFilter scanModeChanged = new
		// IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		// registerReceiver(mReceiver, scanModeChanged);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			//getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		//outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (currentFragment == VicinityConstants.FRAGMENT_MAIN)
			getMenuInflater().inflate(R.menu.main_menu, menu);
		else if (currentFragment == VicinityConstants.FRAGMENT_LEVELS)
			getMenuInflater().inflate(R.menu.add_level_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_list_level) {
			showFragment(VicinityConstants.FRAGMENT_LEVELS, 0);
		} else if (id == R.id.action_add_level) {
			MenuController.addLevel(this);
		} else if (id == R.id.action_scan_controller) {
			MenuController.toggleScan(this);
			if (BluetoothController.isScanning())
				item.setIcon(android.R.drawable.ic_media_pause);
			else
				item.setIcon(android.R.drawable.ic_media_play);
		} else if (id == R.id.action_device_status) {
			MenuController.DeviceStatus.showDeviceStatus(this);
		} else if (id == R.id.action_log_file) {
			MenuController.openLogFolder(this);
		} else {
			showFragment(VicinityConstants.FRAGMENT_MAIN, 0);
		}
		return super.onOptionsItemSelected(item);
	}

	public void showFragment(int i, int extra) {
		switch (i) {
		case VicinityConstants.FRAGMENT_LEVELS:
			PlotController.setActive(false);
			currentFragment = VicinityConstants.FRAGMENT_LEVELS;
			getSupportFragmentManager().beginTransaction().replace(R.id.container, LevelsListFragment.newInstance(VicinityConstants.FRAGMENT_LEVELS)).commit();
			supportInvalidateOptionsMenu();
			break;
		case VicinityConstants.FRAGMENT_MAIN:
			PlotController.setActive(false);
			currentFragment = VicinityConstants.FRAGMENT_MAIN;
			getSupportFragmentManager().beginTransaction().replace(R.id.container, DeviceListFragment.newInstance(VicinityConstants.FRAGMENT_MAIN)).commit();
			supportInvalidateOptionsMenu();
			break;
		default:
			Toast.makeText(this, "Not ready yet", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		showFragment(position + 1, 0);
		return true;
	}

	@Override
	public void onRefresh() {
		if (!BluetoothController.isScanning())
			Toast.makeText(this, "Tap play in the Action bar menu to start scanning", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Already scanning", Toast.LENGTH_SHORT).show();
	}

	public static int getCurrentFragment() {
		return currentFragment;
	}

	public SwipeRefreshLayout getRefreshLayout() {
		return mRefreshLayout;
	}

	@Override
	protected void onPause() {
		super.onPause();
		PreferencesController.save();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Mint.startSession(this);
		PreferencesController.load();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == VicinityConstants.DISCOVERABLE_DURATION) {
			BluetoothController.scanBluetooth();
		}
		if (requestCode == VicinityConstants.GPS_ENABLED_CODE) {
			DataController.requestGPSData(this, null, null);
			Toast.makeText(this, "GPS enabled, waiting for location...", Toast.LENGTH_SHORT).show();
		}
		if (requestCode != VicinityConstants.GPS_ENABLED_CODE && resultCode == Activity.RESULT_CANCELED) {
			BluetoothController.enableBluetoothAndScan();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentFragment != VicinityConstants.FRAGMENT_MAIN)
			showFragment(VicinityConstants.FRAGMENT_MAIN, 0);
		else
			showExitDialog();
	}

	private void showExitDialog() {
		Builder exitDialog = new AlertDialog.Builder(this);
		exitDialog.setTitle("Exit application?");
		exitDialog.setCancelable(true);
		exitDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		});
		exitDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		if (BluetoothController.isScanning()) {
			exitDialog
					.setMessage("Device discovery is running - it will continue working in background. Press 'Stop scan and exit' to dismiss all background operations");
			exitDialog.setNeutralButton("Stop scan and exit", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					BluetoothController.stopBluetooth();
					dialog.cancel();
					finish();
				}
			});
		} else {
			exitDialog.setMessage("Discovery is off. You may exit");
		}

		exitDialog.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		LogController.appendFile("Application closed", false);
	}
	
	    // Stop the session
	    public void onStop() {
	        super.onStop();
	        Mint.closeSession(this);
	    }

}
