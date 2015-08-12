package com.itzwf.mobilesafe.receiver;

import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		//开启系统服务
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		//获取现在的SIM卡
		String currentSim = tm.getSimSerialNumber();
		//获取绑定的SIM卡
		String localSim = PreferenceUtils.getString(context, Constants.SIM);
		//判断2个SIM卡是否相同,如果不同就发短信告诉手机有可能给盗了
		if(!currentSim.equals(localSim)){
			SmsManager manager = SmsManager.getDefault();
			String number = PreferenceUtils.getString(context, Constants.SAFE_NUMBER);
			manager.sendTextMessage(number, null, "shou ji gei dao le", null, null);
		}
		
	}

}
