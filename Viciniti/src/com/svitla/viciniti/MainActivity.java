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
import com.svitla.viciniti.monitor.LevelsMonitor;
import com.svitla.viciniti.ui.fragments.PlaceholderFragment;
import com.svitla.viciniti.utils.Preferences;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
		final EditText name = (EditText) addDialogView.findViewById(R.id.nameSecurityEdit);
		final EditText strength = (EditText) addDialogView.findViewById(R.id.levelSecurityEdit);
		final AlertDialog addDialog = new AlertDialog.Builder(this).create();
		addDialog.setView(addDialogView);
		addDialogView.findViewById(R.id.addButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (("").equals(name.getText().toString()) || ("").equals(strength.getText().toString()))
					Toast.makeText(MainActivity.this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
				else {
					Level level = new Level(name.getText().toString(), Integer.valueOf(strength.getText().toString()));
					LevelsMonitor.getLevels().add(level);
					Preferences.save();
					addDialog.dismiss();
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

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		getSupportFragmentManager().beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
		return true;
	}

}
