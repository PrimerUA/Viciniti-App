package com.svitla.viciniti.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.svitla.viciniti.VicinityConstants;

import android.content.Context;
import android.os.Environment;

public class LogController {

	private static Context context;
	private static File pathFile;

	public static void init(Context context, Date datetime) {
		LogController.context = context;
		pathFile = new File(VicinityConstants.VICINITI_DIR + "log/log_" + datetime);

		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		try {
			pathFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getFormattedCurrentData() {
		return new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
	}

	public static void appendFile(String logEntry) {

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathFile.getPath());

			fos.write((getFormattedCurrentData() + " - ").getBytes());
			fos.write(logEntry.toString().getBytes());
			fos.write("\n".getBytes());

			fos.close();

			Toast.makeText(this, "Backup Complete", Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

			AlertDialog.Builder delmessagebuilder = new AlertDialog.Builder(this);

			delmessagebuilder.setCancelable(false);

			delmessagebuilder.setMessage("File Access Error");

			delmessagebuilder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			delmessagebuilder.create().show();

		} catch (IOException e) {

			e.printStackTrace();

			AlertDialog.Builder delmessagebuilder = new AlertDialog.Builder(this);

			delmessagebuilder.setCancelable(false);

			delmessagebuilder.setMessage("File Access Error");

			delmessagebuilder.setNeutralButton("Okay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			delmessagebuilder.create().show();
		}
	}

}
