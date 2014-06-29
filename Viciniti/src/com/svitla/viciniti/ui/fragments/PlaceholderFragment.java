package com.svitla.viciniti.ui.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.svitla.viciniti.MainActivity;
import com.svitla.viciniti.R;
import com.svitla.viciniti.beans.Device;
import com.svitla.viciniti.beans.Level;
import com.svitla.viciniti.monitor.DevicesMonitor;
import com.svitla.viciniti.monitor.LevelsMonitor;
import com.svitla.viciniti.utils.Preferences;

public class PlaceholderFragment extends Fragment implements OnRefreshListener {

	private static final String ARG_SECTION_NUMBER = "section_number";
	protected int REQUEST_ENABLE_BT = 1;

	public static PlaceholderFragment newInstance(int sectionNumber) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private MainActivity mainActivity;

	private ListView bluetoothListView;
	private ArrayAdapter<String> mArrayAdapter;
	private SwipeRefreshLayout refreshLayout;

	public BluetoothAdapter mBluetoothAdapter;
	public BroadcastReceiver mReceiver;

	public static ArrayList<BluetoothDevice> myBTDevices = new ArrayList<BluetoothDevice>();

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mainActivity = (MainActivity) getActivity();

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		bluetoothListView = (ListView) rootView.findViewById(R.id.listView);
		mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

		refreshLayout.setColorScheme(android.R.color.background_light, android.R.color.black, android.R.color.white, android.R.color.black);
		refreshLayout.setOnRefreshListener(this);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Log.v("onCreate", "Bluetooth Adapter off");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			Log.v("onCreate", "Bluetooth adapter is on");
			scanBluetooth();
			// startDiscovery();
		}

		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (refreshLayout.isRefreshing())
					refreshLayout.setRefreshing(false);
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
					BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (DevicesMonitor.isSupervisedDevice(bDevice.getName())) {
						Device device = new Device(bDevice, rssi, true);
						DevicesMonitor.getDevices().add(device);
						Preferences.save();
					} else {
						Device device = new Device(bDevice, rssi, false);
						DevicesMonitor.getDevices().add(device);
						Preferences.save();
					}
					mArrayAdapter.add(bDevice.getName() + " - " + " RSSI: " + rssi + "dBm");
					bluetoothListView.setAdapter(mArrayAdapter);
					if (DevicesMonitor.getSupervisedDevices().size() > 0)
						checkSupervisedDevices(rssi);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					mArrayAdapter.clear();
					DevicesMonitor.getDevices().clear();
					Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();
				}
				// else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
				// BluetoothDevice device =
				// intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// for (int i = 0; i < mArrayAdapter.getCount(); i++) {
				// String item = mArrayAdapter.getItem(i);
				// if (item.contains(device.getName())) {
				// mArrayAdapter.remove(item);
				// item = device.getName() + " - " +
				// intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
				// Short.MIN_VALUE) + " dBm";
				// mArrayAdapter.insert(item, i);
				// }
				// }
				// }
				scanBluetooth();
			}
		};

		bluetoothListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				superviseDevice(position);
			}
		});

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getActivity().registerReceiver(mReceiver, filter);
		IntentFilter discoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		getActivity().registerReceiver(mReceiver, discoveryFinished);
		IntentFilter rssiChanged = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
		getActivity().registerReceiver(mReceiver, rssiChanged);

		return rootView;
	}

	protected void checkSupervisedDevices(short rssi) {
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++)
			if (rssi < LevelsMonitor.getLevels().get(i).getLevel()) {
				securityAlert(LevelsMonitor.getLevels().get(i).getLevel());
			}
	}

	private void securityAlert(Integer securityLevel) {
		Level level = null;
		for (int i = 0; i < LevelsMonitor.getLevels().size(); i++)
			if (LevelsMonitor.getLevels().get(i).getLevel().equals(securityLevel))
				level = LevelsMonitor.getLevels().get(i);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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

	protected void superviseDevice(final int number) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Supervise this device?");
		alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				for (int i = 0; i < DevicesMonitor.getSupervisedDevices().size(); i++)
					if (DevicesMonitor.getSupervisedDevices().get(i).getName().equals(mArrayAdapter.getItem(number))) {
						DevicesMonitor.getSupervisedDevices().get(i).setMonitored(true);
						Preferences.save();
						Toast.makeText(getActivity(), "Now supervising " + DevicesMonitor.getSupervisedDevices().get(i).getName(), Toast.LENGTH_SHORT).show();
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

	private void scanBluetooth() {
		if (!refreshLayout.isRefreshing())
			refreshLayout.setRefreshing(true);
		if (mBluetoothAdapter.isDiscovering())
			return;
		Toast.makeText(getActivity(), "Scanning", Toast.LENGTH_SHORT).show();
		mBluetoothAdapter.startDiscovery();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			scanBluetooth();
		}
	}

	@Override
	public void onRefresh() {
		scanBluetooth();
	}

}
