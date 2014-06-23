package com.svitla.viciniti.ui.fragments;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.svitla.viciniti.R;
import com.svitla.viciniti.utils.ConnectThread;

public class PlaceholderFragment extends Fragment implements OnRefreshListener {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public static PlaceholderFragment newInstance(int sectionNumber) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private ListView bluetoothListView;
	private ArrayAdapter<String> mArrayAdapter;
	private SwipeRefreshLayout refreshLayout;

	public BluetoothAdapter myBluetoothAdapter = null;
	protected int REQUEST_ENABLE_BT = 1;
	public static ArrayList<BluetoothDevice> myBTDevices = new ArrayList<BluetoothDevice>();

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);

		/*
		 * Display elements initialization
		 */
		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		bluetoothListView = (ListView) rootView.findViewById(R.id.listView);
		mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

		refreshLayout.setColorScheme(android.R.color.background_light, android.R.color.black, android.R.color.white, android.R.color.black);
		refreshLayout.setOnRefreshListener(this);

		/*
		 * Initialize Bluetooth adapter and deal with user not willing to enable
		 * it
		 */
		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!myBluetoothAdapter.isEnabled()) {
			Log.v("onCreate", "Bluetooth Adapter off");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			if (!myBluetoothAdapter.isEnabled()) {
				Log.v("onCreate", "Bluetooth Adapter off despite asking for it to the user");
			} else {
				// dealBluetooth();
				startDiscovery();
			}
		} else {
			Log.v("onCreate", "Bluetooth adapter is on");
			startDiscovery();
		}

		/*
		 * Dealing with a click on an item
		 */
		bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
				Log.v("setOnItemClickListener", "Item clicked: " + i);
				if (myBTDevices.size() > 0) {
					myBTDevices.get(i).fetchUuidsWithSdp();
					myBluetoothAdapter.cancelDiscovery();
					new Thread(new Runnable() {
						@Override
						public void run() {
							Log.v("setOnItemClickListener", "Running pairing");
							final ConnectThread temp = new ConnectThread(myBTDevices.get(i), myBluetoothAdapter);
							temp.run();
						}
					}).start();
				}
			}
		});

		return rootView;
	}

	/*
	 * Create a BroadcastReceiver for ACTIONS
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (refreshLayout.isRefreshing())
				refreshLayout.setRefreshing(false);
			Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();

			/*
			 * When a device is found
			 */
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				/*
				 * Get the BluetoothDevice object from the Intent
				 */
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				try {
					int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
					boolean isDeviceExist = false;
					for (int i = 0; i < mArrayAdapter.getCount(); i++) {
						String item = mArrayAdapter.getItem(i);
						if (item.contains(device.getName())) {
							isDeviceExist = true;
							mArrayAdapter.remove(item);
							item = device.getName() + "\n" + device.getAddress() + "\n" + "RSSI: " + rssi + "dBm";
							mArrayAdapter.insert(item, i);
							break;
						}
					}

					if (!isDeviceExist) {
						mArrayAdapter.add(device.getName() + "\n" + device.getAddress() + "\n" + "RSSI: " + rssi + "dBm");
					}

					Log.v("BroadcastReceiver", "BT device found: " + device.getName() + " AND " + device.getAddress());
					try {
						if (!myBTDevices.contains(device))
							myBTDevices.add(device);
					} catch (Exception e) {
						Log.v("BroadcastReceiver", "Error when adding Bluetooth device: " + e.toString());
					}
				} catch (NullPointerException e) {
					Log.v("BroadcastReceiver", "Value: " + device.toString());
				}
				if (mArrayAdapter.getCount() != 0) {
					bluetoothListView.setAdapter(mArrayAdapter);
				} else {
					Log.v("BroadcastReceiver", "No devices discovered");
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.v("BroadcastReceiver", "Discovery started");
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.v("BroadcastReceiver", "Discovery finished");
			}
			// else if (WifiManager.RSSI_CHANGED_ACTION .equals(action)) {
			// BluetoothDevice device =
			// intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// for (int i=0; i<mArrayAdapter.getCount(); i++) {
			// String item = mArrayAdapter.getItem(i);
			// if (item.contains(device.getName())) {
			// mArrayAdapter.remove(item);
			// int rssi =
			// intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
			// item = device.getName() + "\n" + device.getAddress() + "\n" +
			// " RSSI: " + rssi + "dBm";
			// mArrayAdapter.insert(item, i);
			// break;
			// }
			// }
			// }
		}
	};

	/*
	 * New devices discovery
	 */
	public void startDiscovery() {
		if (!refreshLayout.isRefreshing())
			refreshLayout.setRefreshing(true);
		/*
		 * Initialization
		 */
		Log.v("startDiscovery", "Discovery starting");
		mArrayAdapter.clear();
		myBTDevices = new ArrayList<BluetoothDevice>();
		if (myBluetoothAdapter.isDiscovering()) {
			myBluetoothAdapter.cancelDiscovery();
			if (refreshLayout.isRefreshing())
				refreshLayout.setRefreshing(false);
			return;
		}

		// myBluetoothAdapter.startDiscovery();
		/* Register the BroadcastReceiver */
		IntentFilter actionFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getActivity().registerReceiver(mReceiver, actionFound);
		IntentFilter discoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		getActivity().registerReceiver(mReceiver, discoveryStarted);
		IntentFilter discoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		getActivity().registerReceiver(mReceiver, discoveryFinished);
		// IntentFilter rssiChanged = new
		// IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
		// registerReceiver(mReceiver, rssiChanged);

		if (myBluetoothAdapter.isDiscovering())
			return;
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (myBluetoothAdapter.isDiscovering())
					return;
				myBluetoothAdapter.startDiscovery();
			}
		}, 0, 5000);
		Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();
	}

	/*
	 * Display paired devices NEED TO ADD PAIRED DEVICES TO A LIST OBJECT
	 */
	private void dealBluetooth() {
		Log.v("dealBluetooth", "Bluetooth ON = Good to go!");
		Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			/* Loop through paired devices */
			for (BluetoothDevice device : pairedDevices) {
				System.out.println(device.getAddress());
				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
			if (mArrayAdapter.getCount() != 0) {
				bluetoothListView.setAdapter(mArrayAdapter);
			} else {
				Log.v("dealBluetooth", "No devices discovered");
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				Log.v("onActivityResult", "User enabled Bluetooth");
				dealBluetooth();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.v("onActivityResult", "User disabled Bluetooth");
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		myBluetoothAdapter.cancelDiscovery();
		try {
			getActivity().unregisterReceiver(mReceiver);
		} catch (IllegalArgumentException e) {
			Log.v("onDestroy", "No registered");
		}
	}

	@Override
	public void onRefresh() {
		startDiscovery();
	}
}
