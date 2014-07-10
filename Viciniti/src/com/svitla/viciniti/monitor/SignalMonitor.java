package com.svitla.viciniti.monitor;

import java.util.ArrayList;

import com.svitla.viciniti.beans.SignalArray;

public class SignalMonitor {
    private static ArrayList<SignalArray> monitorArray = new ArrayList<SignalArray>();

    public static ArrayList<SignalArray> getMonitorArray() {
	return monitorArray;
    }

}
