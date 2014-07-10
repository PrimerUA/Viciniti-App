package com.svitla.viciniti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.MenuController;
import com.svitla.viciniti.controllers.PlotController;
import com.svitla.viciniti.controllers.PreferencesController;
import com.svitla.viciniti.ui.fragments.DeviceDetailsFragment;
import com.svitla.viciniti.ui.fragments.DeviceListFragment;
import com.svitla.viciniti.ui.fragments.LevelsListFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, OnRefreshListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static int currentFragment;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	PreferencesController.init(this);

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
	mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
	mRefreshLayout.setColorScheme(android.R.color.background_light, android.R.color.black, android.R.color.white, android.R.color.black);
	mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
	// Restore the previously serialized current dropdown position.
	if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
	    getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
	// Serialize the current dropdown position.
	outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
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
	    MenuController.deviceStatus(this);
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
	case VicinityConstants.FRAGMENT_DETAILS:
	    PlotController.setActive(true);
	    currentFragment = VicinityConstants.FRAGMENT_DETAILS;
	    getSupportFragmentManager().beginTransaction().replace(R.id.container, DeviceDetailsFragment.newInstance(extra)).commit();
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
	    BluetoothController.scanBluetooth();
	else
	    Toast.makeText(this, "Already scaninng", Toast.LENGTH_SHORT).show();
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
	PreferencesController.load();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == VicinityConstants.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
	    BluetoothController.scanBluetooth();
	}
    }

}
