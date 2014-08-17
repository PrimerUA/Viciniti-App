package com.svitla.viciniti.controllers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.svitla.viciniti.R;
import com.svitla.viciniti.VicinityConstants;
import com.svitla.viciniti.beans.SignalArray;
import com.svitla.viciniti.monitor.LevelsMonitor;
import com.svitla.viciniti.monitor.SignalMonitor;

public class PlotController {

	private static boolean isActive;

	private static GraphView graphView;
	private static GraphViewSeries strengthSeries;
	private static LinearLayout contentLayout;
	private static int columnQuantity;
	private static SignalArray localSignalArray;
	private static BluetoothDevice bluetoothDevice;

	public static void init(Context context, BluetoothDevice bDevice, LinearLayout layout) {
		isActive = true;
		columnQuantity = 0;
		contentLayout = layout;
		bluetoothDevice = bDevice;
		localSignalArray = SignalMonitor.getArrayByDevice(bDevice);
		graphView = new BarGraphView(context, localSignalArray.getBluetoothDevice().getName());
		graphView.getGraphViewStyle().setTextSize(context.getResources().getDimension(R.dimen.d15p));
		GraphViewData data = new GraphViewData(columnQuantity++, localSignalArray.getSignalArray().get(0).getRssi());
		strengthSeries = new GraphViewSeries(new GraphViewData[] { data });
		buildGraph();
	}

	public static void updateData() {
		if (graphView != null)
			contentLayout.removeView(graphView);
		localSignalArray = SignalMonitor.getArrayByDevice(bluetoothDevice);
//		GraphViewData zeroData = new GraphViewData(0,0);
//		GraphViewSeries levelSeries = new GraphViewSeries(new GraphViewData[] { zeroData });
//		for (int i = 1; i < LevelsMonitor.getLevels().size(); i++) {
//			for (int j = 1; j < VicinityConstants.PLOT_COLUMNS_NUMBER; j++) {
//				GraphViewData data = new GraphViewData(j, LevelsMonitor.getLevels().get(i).getLevel());
//				levelSeries.appendData(data, false, VicinityConstants.PLOT_COLUMNS_NUMBER);
//			}
//		}
		buildGraph();
	}

	private static void buildGraph() {
		for (int i = 1; i < localSignalArray.getSignalArray().size(); i++) {
			GraphViewData data = new GraphViewData(columnQuantity++, localSignalArray.getSignalArray().get(i).getRssi());
			strengthSeries.appendData(data, false, VicinityConstants.PLOT_COLUMNS_NUMBER);
		}
		graphView.addSeries(strengthSeries);
		contentLayout.addView(graphView);
	}

	public static GraphView getGraphView() {
		return graphView;
	}

	public static LinearLayout getContentLayout() {
		return contentLayout;
	}

	public static int getColumnQuantity() {
		return columnQuantity;
	}

	public static boolean isActive() {
		return isActive;
	}

	public static void setActive(boolean isActive) {
		PlotController.isActive = isActive;
	}

}
