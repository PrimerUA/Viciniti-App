package com.svitla.viciniti;

import android.os.Environment;

public class VicinityConstants {
    
    public final static int DISCOVERABLE_DURATION = 0; // always discoverable

    public final static int REQUEST_ENABLE_BT = 100;
    public final static int TAKE_PHOTO_CODE = 101;
    public final static int GPS_ENABLED_CODE = 102;
    
    public final static int FRAGMENT_LEVELS = 0;
    public final static int FRAGMENT_MAIN = 1;
    
    public final static int PLOT_COLUMNS_NUMBER = 10;

    public final static String ALERT_MESSAGE = "alert-message";
    
    public final static String VICINITI_DIR = Environment.getExternalStorageDirectory().getPath() + "/viciniti/";
    
    public final static int LOG_UPDATE_SENSOR_INTERVAL = 1000; // 1 second

}
