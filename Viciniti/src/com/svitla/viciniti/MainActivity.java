package com.svitla.viciniti;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.svitla.viciniti.ui.fragments.PlaceholderFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	private ArrayList<String> mSupervisedDevices;
	private ArrayList<Integer> mSecurityLevels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
						new String[] { getString(R.string.title_section1), getString(R.string.title_section2),
								getString(R.string.title_section3), }), this);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_add_level) {
			addLevel();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addLevel() {
		final View addDialogView = getLayoutInflater().inflate(R.layout.add_security_dialog, null);
		final AlertDialog addDialog = new AlertDialog.Builder(this).create();
		addDialog.setView(addDialogView);
		addDialogView.findViewById(R.id.addButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addDialog.dismiss();
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

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		getSupportFragmentManager().beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
		return true;
	}

	public ArrayList<String> getSupervisedDevices() {
		return mSupervisedDevices;
	}

	public ArrayList<Integer> getSecurityLevels() {
		return mSecurityLevels;
	}

}
