package com.itzwf.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.itzwf.mobilesafe.domail.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppInfoProvider {

	private static final String TAG = "AppInfoProvider";

	public static List<AppInfo> getAllApps(Context context){
		List<AppInfo> list = new ArrayList<AppInfo>();
		//获得包管理器
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		
		for (PackageInfo pack : packages) {
			AppInfo info = new AppInfo();
			info.packageName = pack.packageName;
			ApplicationInfo applicationInfo = pack.applicationInfo;
			info.name = applicationInfo.loadLabel(pm).toString();
			info.icon = applicationInfo.loadIcon(pm);
			//sourceDir是安装包的路径
			//dataDir是文件包下的路径
			info.size = new File(applicationInfo.sourceDir).length();
			
			int flags = applicationInfo.flags;
			
			if((flags & ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				//是系统文件
				info.isSystem = true;
			}else{
				info.isSystem = false;
			}
				
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//外部储存
				info.isInstallSD =true;
			}else{
				info.isInstallSD =false;
			}
			
			Log.d(TAG, "" + info.packageName);
			Log.d(TAG, "" + info.name);
			Log.d(TAG, "" + info.size);
			Log.d(TAG, "" + info.isInstallSD);
			Log.d(TAG, "" + "------------");
			list.add(info);
		}
		return list;
	}
}
