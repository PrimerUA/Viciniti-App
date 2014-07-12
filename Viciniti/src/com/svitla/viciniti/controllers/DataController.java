package com.svitla.viciniti.controllers;

import com.svitla.viciniti.VicinityConstants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DataController {

    public static void requestGPSData(ActionBarActivity activity, final TextView name, final ProgressBar progressBar) {
	progressBar.setVisibility(View.VISIBLE);
	LocationManager locationManager = (LocationManager) activity.getSystemService(Service.LOCATION_SERVICE);
	if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    activity.startActivityForResult(callGPSSettingIntent, VicinityConstants.GPS_ENABLED_CODE);
	}
	final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	if (location == null && locationManager != null) {
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
		    updateGPSViews(location, name, progressBar);
		}
	    });
	} else {
	    updateGPSViews(location, name, progressBar);
	}

    }

    private static void updateGPSViews(Location location, TextView name, ProgressBar progressBar) {
	name.setText("[Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + "]");
	progressBar.setVisibility(View.INVISIBLE);
    }

    public static void requestAccelerometerData(Context context, final TextView strength) {
	SensorManager senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	Sensor senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	senSensorManager.registerListener(new SensorEventListener() {

	    @Override
	    public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		    strength.setText("X=" + String.valueOf(sensorEvent.values[0]) + "Y=" + String.valueOf(sensorEvent.values[1]) + "Z=" + String.valueOf(sensorEvent.values[2]));
		}
	    }

	    @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
	}, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
