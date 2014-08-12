package com.svitla.viciniti.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.svitla.viciniti.R;
import com.svitla.viciniti.VicinityConstants;

public class LogController {

	private static Context context;
	private static File pathFile;

	public static void init(Context context) {
		LogController.context = context;
		File directory = new File(VicinityConstants.VICINITI_DIR + "logs/");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		try {
			pathFile = new File(directory.getAbsoluteFile() + "/log_" + new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) + ".txt");
			pathFile.createNewFile();
			appendFile("App launched. " + getBuildVersion() + ". LogFile created");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getBuildVersion() {
		return "Build version: " + context.getString(R.string.build_version);
	}

	public static void showBuildVersionToast() {
		Toast.makeText(context, getBuildVersion(), Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("SimpleDateFormat")
	private static String getFormattedCurrentData() {
		return new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
	}

	public static void appendFile(String logEntry) {

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathFile, true);

			fos.write((getFormattedCurrentData() + " - ").getBytes());
			fos.write(logEntry.toString().getBytes());
			fos.write("\n".getBytes());

			fos.close();

			Toast.makeText(context, "Log updated", Toast.LENGTH_SHORT).show();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

			AlertDialog.Builder delmessagebuilder = new AlertDialog.Builder(context);

			delmessagebuilder.setCancelable(false);

			delmessagebuilder.setMessage("Error: Log file not found");

			delmessagebuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			delmessagebuilder.create().show();

		} catch (IOException e) {

			e.printStackTrace();

			AlertDialog.Builder delmessagebuilder = new AlertDialog.Builder(context);

			delmessagebuilder.setCancelable(false);

			delmessagebuilder.setMessage("Error: Log file access denied");

			delmessagebuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			delmessagebuilder.create().show();
		}
	}
}
