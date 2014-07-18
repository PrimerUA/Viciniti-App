package com.svitla.viciniti.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.svitla.viciniti.MainActivity;
import com.svitla.viciniti.R;
import com.svitla.viciniti.controllers.BluetoothController;
import com.svitla.viciniti.controllers.DevicesController;
import com.svitla.viciniti.controllers.MenuController;
import com.svitla.viciniti.monitor.DevicesMonitor;

public class DeviceListFragment extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public static DeviceListFragment newInstance(int sectionNumber) {
		DeviceListFragment fragment = new DeviceListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	private MainActivity mainActivity;

	private ListView mBluetoothListView;

	public DeviceListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_devices, container, false);
		mainActivity = (MainActivity) getActivity();
		mainActivity.getSupportActionBar().setTitle(R.string.placeholder_fragment_title);
		mainActivity.getSupportActionBar().setHomeButtonEnabled(false);
		mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		mBluetoothListView = (ListView) rootView.findViewById(R.id.listView);
		mBluetoothListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DevicesController.superviseDevice(getActivity(), position);
			}
		});
		mBluetoothListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				MenuController.plotControll(mainActivity, DevicesMonitor.getDevices().get(position));
				return true;
			}
		});
		
		BluetoothController.setBluetoothListView(mBluetoothListView);
		
		return rootView;
	}

}
