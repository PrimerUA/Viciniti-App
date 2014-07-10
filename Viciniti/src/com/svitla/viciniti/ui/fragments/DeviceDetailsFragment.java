package com.svitla.viciniti.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.svitla.viciniti.MainActivity;
import com.svitla.viciniti.R;
import com.svitla.viciniti.controllers.PlotController;
import com.svitla.viciniti.monitor.SignalMonitor;

public class DeviceDetailsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private MainActivity mainActivity;

    public static DeviceDetailsFragment newInstance(int sectionNumber) {
	DeviceDetailsFragment fragment = new DeviceDetailsFragment();
	Bundle args = new Bundle();
	args.putInt(ARG_SECTION_NUMBER, sectionNumber);
	fragment.setArguments(args);
	return fragment;
    }

    public DeviceDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View rootView = inflater.inflate(R.layout.fragment_device_details, container, false);
	mainActivity = (MainActivity) getActivity();
	mainActivity.getSupportActionBar().setTitle(R.string.details_fragment_title);
	mainActivity.getSupportActionBar().setHomeButtonEnabled(true);
	mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	
	PlotController.init(getActivity(), (LinearLayout) rootView.findViewById(R.id.devicesLayout), getArguments().getInt(ARG_SECTION_NUMBER));

	return rootView;
    }
}