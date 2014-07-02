package com.svitla.viciniti.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.svitla.viciniti.MainActivity;
import com.svitla.viciniti.R;
import com.svitla.viciniti.monitor.LevelsMonitor;

public class LevelsListFragment extends ListFragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private MainActivity mainActivity;

	public static LevelsListFragment newInstance(int sectionNumber) {
		LevelsListFragment fragment = new LevelsListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_levels, container, false);
		mainActivity = (MainActivity) getActivity();
		mainActivity.getSupportActionBar().setTitle(R.string.add_text);
		mainActivity.getSupportActionBar().setHomeButtonEnabled(true);
		mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		refreshList();
		
		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_LONG).show();
	}
	
	public void refreshList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, LevelsMonitor.getInfo());
		setListAdapter(adapter);
	}

}