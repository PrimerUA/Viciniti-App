package com.svitla.viciniti.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.svitla.viciniti.MainActivity;
import com.svitla.viciniti.R;
import com.svitla.viciniti.monitor.LevelsMonitor;

public class LevelsListFragment extends ListFragment implements OnItemLongClickListener {

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
	mainActivity.getSupportActionBar().setTitle(R.string.levels_fragment_title);
	mainActivity.getSupportActionBar().setHomeButtonEnabled(true);
	mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	refreshList();

	return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	String item = (String) getListAdapter().getItem(position);
	Toast.makeText(getActivity(), item + " - longpress to remove", Toast.LENGTH_LONG).show();
    }

    public void refreshList() {
	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, LevelsMonitor.getInfo());
	setListAdapter(adapter);
    }

    private void showDeleteDialog(final int position) {
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
	alertDialogBuilder.setTitle("Remove selected zone?");
	alertDialogBuilder.setMessage(LevelsMonitor.getLevels().get(position).getName());
	alertDialogBuilder.setCancelable(false).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int id) {
		if (LevelsMonitor.getLevels().size() > 1) {
		    LevelsMonitor.getLevels().remove(position);
		    refreshList();
		    Toast.makeText(getActivity(), "Zone has been removed", Toast.LENGTH_LONG).show();
		} else {
		    Toast.makeText(getActivity(), "At least 1 zone is required", Toast.LENGTH_LONG).show();
		}
		dialog.dismiss();
	    }
	}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
	    }
	});
	AlertDialog alertDialog = alertDialogBuilder.create();
	alertDialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
	showDeleteDialog(position);
	return false;
    }

}