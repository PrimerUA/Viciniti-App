package com.svitla.viciniti.controllers;

import com.svitla.viciniti.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

public class NotificationController {

	public static void showNotification(Context context, String message) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(android.R.drawable.ic_dialog_alert)
				.setContentTitle("Alert! Device has left the zone").setContentText(message);
		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		Notification notification = mBuilder.build();
		notification.flags = NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.FLAG_AUTO_CANCEL;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, notification);
	}
}
