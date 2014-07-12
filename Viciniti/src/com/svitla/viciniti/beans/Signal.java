package com.svitla.viciniti.beans;

public class Signal {

    private short rssi;
    
    public Signal(short rssi) {
	super();
	this.rssi = rssi;
    }

    public short getRssi() {
	return rssi;
    }

    public void setRssi(short rssi) {
	this.rssi = rssi;
    }

}
