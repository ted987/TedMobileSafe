package com.itzwf.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {
	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context){
		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			//获取版本名字
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;		
	}
	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context){
		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
