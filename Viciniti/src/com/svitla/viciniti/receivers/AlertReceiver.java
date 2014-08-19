package com.svitla.viciniti.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.svitla.viciniti.MainActivity;
import com.svitla.viciniti.R;
import com.svitla.viciniti.VicinityConstants;
import com.svitla.viciniti.controllers.MenuController;
import com.svitla.viciniti.controllers.NotificationController;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
	Intent mainActivity = new Intent(context, MainActivity.class);
	mainActivity.setAction(Intent.ACTION_MAIN);
	mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	mainActivity.addCategory(Intent.CATEGORY_LAUNCHER);
	context.startActivity(mainActivity);

	NotificationController.showNotification(context, intent.getExtras().getString(VicinityConstants.ALERT_MESSAGE));
	if (MenuController.DeviceStatus.getAlarmLayout() != null)
	    MenuController.DeviceStatus.getAlarmLayout().setBackgroundColor(Color.RED);
    }
}
