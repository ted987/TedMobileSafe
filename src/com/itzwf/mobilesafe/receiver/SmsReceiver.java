package com.itzwf.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.service.GpsService;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;

public class SmsReceiver extends BroadcastReceiver {

	private String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean flag = PreferenceUtils
				.getBoolean(context, Constants.PROTECTING);
		if (!flag) {
			return;
		}
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		// 遍历获得SMS
		for (Object obj : objs) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
			String messageBody = sms.getMessageBody();
			String sendAddress = sms.getOriginatingAddress();

			if ("#*location*#".equals(messageBody)) {
				// GPS追踪
				Intent service = new Intent(context, GpsService.class);
				context.startService(service);
				abortBroadcast();
			} else if ("#*wipedata*#".equals(messageBody)) {
				// 远程销毁数据
				DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context, SafeAdminReseiver.class);
				if(dpm.isAdminActive(who)){
					Log.d(TAG , "远程销毁数据");
					dpm.wipeData(0);
				}
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(messageBody)) {
				// 远程锁屏
				// 获取设备管理员管理
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context,
						SafeAdminReseiver.class);
				if (dpm.isAdminActive(who)) {
					dpm.resetPassword("123", 0);
					dpm.lockNow();
				}
				abortBroadcast();
			} else if ("#*alarm*#".equals(messageBody)) {
				// 播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				player.setLooping(true);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}

		}
	}

}
