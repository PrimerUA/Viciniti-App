package com.svitla.viciniti.controllers;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.svitla.viciniti.VicinityConstants;
import com.svitla.viciniti.beans.SignalArray;
import com.svitla.viciniti.monitor.SignalMonitor;

public class PlotController {

    private static boolean isActive;

    private static GraphView graphView;
    private static GraphViewSeries strengthSeries;
    private static LinearLayout contentLayout;
    private static int columnQuantity = 0;
    private static SignalArray localSignalArray;
    private static int number;

    public static void init(Context context, LinearLayout layout, int arrayListNumber) {
	if (SignalMonitor.getMonitorArray().size() > arrayListNumber) {
	    localSignalArray = SignalMonitor.getMonitorArray().get(arrayListNumber);
	    contentLayout = layout;
	    number = arrayListNumber;
	    graphView = new BarGraphView(context, "Bluetooth data plot for device: " + localSignalArray.getBluetoothDevice().getName());
	    strengthSeries = new GraphViewSeries(new GraphViewData[] {});
	    buildGraph();
	} else {
	    Toast.makeText(context, "No data yet", Toast.LENGTH_SHORT).show();
	}
    }

    public static void updateData() {
	if (graphView != null)
	    contentLayout.removeView(graphView);
	localSignalArray = SignalMonitor.getMonitorArray().get(number);
	buildGraph();
    }

    private static void buildGraph() {
	for (int i = 0; i < localSignalArray.getSignalArray().size(); i++)
	    strengthSeries.appendData(new GraphViewData(columnQuantity++, localSignalArray.getSignalArray().get(i).getRssi()), false, VicinityConstants.PLOT_COLUMNS_NUMBER);
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
