package com.itzwf.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ServicestateUtils {

	public static boolean isRunning(Context context,
			Class<? extends Service> clazz) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo info : list) {
			ComponentName service = info.service;
			String className = service.getClassName();
			if(clazz.getName().equals(className)){
				return true;
			}
		}
		return false;
	}
}
