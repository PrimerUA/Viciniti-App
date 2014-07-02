package com.svitla.viciniti.ui.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DevicesController;
import com.svitla.viciniti.receivers.BluetoothReceiver;

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

	private ListView mBluetoothListView;
	private ArrayAdapter<String> mArrayAdapter;
	private SwipeRefreshLayout mRefreshLayout;

	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;

	public static ArrayList<BluetoothDevice> myBTDevices = new ArrayList<BluetoothDevice>();

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mainActivity = (MainActivity) getActivity();
		mainActivity.getSupportActionBar().setTitle(R.string.placeholder_fragment_title);

		mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
		mBluetoothListView = (ListView) rootView.findViewById(R.id.listView);
		mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

		mRefreshLayout.setColorScheme(android.R.color.background_light, android.R.color.black, android.R.color.white, android.R.color.black);
		mRefreshLayout.setOnRefreshListener(this);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothController.init(getActivity(), mBluetoothAdapter, mRefreshLayout);
		
		if (!mBluetoothAdapter.isEnabled()) {
			Log.v("onCreate", "Bluetooth Adapter off");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			Log.v("onCreate", "Bluetooth adapter is on");
			BluetoothController.scanBluetooth();
		}

		mReceiver = new BluetoothReceiver(mBluetoothListView, mArrayAdapter);
		mBluetoothListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DevicesController.superviseDevice(getActivity(), mArrayAdapter, position);
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			BluetoothController.scanBluetooth();
		}
	}

	@Override
	public void onRefresh() {
		if (!BluetoothController.isScanning())
			BluetoothController.scanBluetooth();
		else
			Toast.makeText(getActivity(), "Launch scanner befor refreshing.", Toast.LENGTH_SHORT).show();
	}

}
