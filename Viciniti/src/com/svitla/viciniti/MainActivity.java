package com.svitla.viciniti;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.controllers.MenuController;
import com.svitla.viciniti.controllers.PreferencesController;
import com.svitla.viciniti.monitor.LevelsMonitor;
import com.svitla.viciniti.ui.fragments.LevelsListFragment;
import com.svitla.viciniti.ui.fragments.PlaceholderFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static int currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PreferencesController.init(this);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
				new String[] { getString(R.string.title_section1), getString(R.string.title_section2), getString(R.string.title_section3), }), this);

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
			showFragment(VicinityConstants.FRAGMENT_LEVELS);
		} else if (id == R.id.action_add_level) {
			MenuController.addLevel(this);
		} else if (id == R.id.action_scan_controller) {
			MenuController.toggleScan(this);
		} else {
			showFragment(VicinityConstants.FRAGMENT_MAIN);
		}
		return super.onOptionsItemSelected(item);
	}

	public void showFragment(int i) {
		switch (i) {
		case VicinityConstants.FRAGMENT_LEVELS:
			currentFragment = VicinityConstants.FRAGMENT_LEVELS;
			getSupportFragmentManager().beginTransaction().replace(R.id.container, LevelsListFragment.newInstance(VicinityConstants.FRAGMENT_LEVELS)).commit();
			supportInvalidateOptionsMenu();
			break;
		case VicinityConstants.FRAGMENT_MAIN:
			currentFragment = VicinityConstants.FRAGMENT_MAIN;
			getSupportFragmentManager().beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(VicinityConstants.FRAGMENT_MAIN)).commit();
			supportInvalidateOptionsMenu();
			break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		showFragment(position + 1);
		return true;
	}

	public static int getCurrentFragment() {
		return currentFragment;
	}

}
