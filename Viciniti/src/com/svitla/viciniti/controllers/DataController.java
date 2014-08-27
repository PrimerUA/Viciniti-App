package com.svitla.viciniti.controllers;

import java.util.Date;

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

	private static long lastUpdateDate = new Date().getTime();

	public static void requestGPSData(ActionBarActivity activity, final TextView name, final ProgressBar progressBar) {
		if (progressBar != null)
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
		double longitude = (double) Math.round(location.getLongitude() * 10000) / 10000;
		double latitude = (double) Math.round(location.getLatitude() * 10000) / 10000;

		if (name != null && progressBar != null) {
			name.setText("[Lat: " + longitude + ", Long: " + latitude + "]");
			progressBar.setVisibility(View.INVISIBLE);
		}
		LogController.appendFile("My device GPS update - [Lat: " + longitude + ", Long: " + latitude + "]", true);
	}

	public static void requestAccelerometerData(Context context, final TextView strength) {
		SensorManager senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		senSensorManager.registerListener(new SensorEventListener() {

			float prevX = 0;
			float prevY = 0;
			float prevZ = 0;

			@Override
			public void onSensorChanged(SensorEvent sensorEvent) {
				float x = Math.round(sensorEvent.values[0] * 100) / 100;
				float y = Math.round(sensorEvent.values[1] * 100) / 100;
				float z = Math.round(sensorEvent.values[2] * 100) / 100;
				if (prevX != x && prevY != y && prevZ != z) {
					prevX = x;
					prevY = y;
					prevZ = z;
					Sensor mySensor = sensorEvent.sensor;
					if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
						if (strength != null)
							strength.setText("X=" + String.valueOf(x) + " Y=" + String.valueOf(y) + " Z=" + String.valueOf(z));
						if (new Date().getTime() - lastUpdateDate > VicinityConstants.LOG_UPDATE_SENSOR_INTERVAL) {
							LogController.appendFile(
									"My device Accelerometer update - X=" + String.valueOf(x) + " Y=" + String.valueOf(y) + " Z=" + String.valueOf(z), false);
							lastUpdateDate = new Date().getTime();
						}
					}
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		}, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
}
