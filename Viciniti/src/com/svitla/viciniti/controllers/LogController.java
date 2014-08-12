package com.svitla.viciniti.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.svitla.viciniti.VicinityConstants;

import android.app.AlertDialog;
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
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
	}

	public static void appendFile(String logEntry) {

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathFile.getPath());

			fos.write((getFormattedCurrentData() + " - ").getBytes());
			fos.write(logEntry.toString().getBytes());
			fos.write("\n".getBytes());

			fos.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
