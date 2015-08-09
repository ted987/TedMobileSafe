package com.itzwf.mobilesafe.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.service.GpsService;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean flag = PreferenceUtils.getBoolean(context,
				Constants.PROTECTING);
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
				Intent service =new Intent(context, GpsService.class);
				context.startService(service);
			} else if ("#*wipedata*#".equals(messageBody)) {
				// 远程销毁数据
			} else if ("#*lockscreen*#".equals(messageBody)) {
				// 远程锁屏
			} else if ("#*alarm*#".equals(messageBody)) {
				// 播放报警音乐
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				player.setLooping(true);
				player.setVolume(1.0f, 1.0f);
				player.start();
			}

		}
	}

}
