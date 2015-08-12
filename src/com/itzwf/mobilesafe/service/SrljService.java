package com.itzwf.mobilesafe.service;

import java.lang.reflect.Method;



import com.android.internal.telephony.ITelephony;
import com.itzwf.mobilesafe.db.BlackDao;
import com.itzwf.mobilesafe.domail.BlackInfo;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SrljService extends Service {

	private static final String TAG = "SrljService";
	private TelephonyManager mTm;
	private BlackDao mDao;
	//电话状态监听
	private PhoneStateListener listener = new PhoneStateListener(){
		public void onCallStateChanged(int state, final String incomingNumber) {
			//state表示接收电话的状态
			//incomingNumber 打进来的电话号码
//		    * @see TelephonyManager#CALL_STATE_IDLE  搁浅状态
//		     * @see TelephonyManager#CALL_STATE_RINGING 响铃状态
//		     * @see TelephonyManager#CALL_STATE_OFFHOOK  接听状态
			
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//响铃状态,判断是否在黑名单内,如果在就手动挂掉
				int type = mDao.findType(incomingNumber);
				if(type==BlackInfo.TYPE_CALL || type==BlackInfo.TYPE_ALL){
					//挂掉电话 因为系统把服务隐藏了.所以要用反射的方法去实现
					// Context.TELEPHONY_SERVICE
					// ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
					try {
						Class<?> clazz = Class
								.forName("android.os.ServiceManager");
						Method method = clazz.getDeclaredMethod("getService",
								String.class);
						IBinder binder = (IBinder) method.invoke(null,
								Context.TELEPHONY_SERVICE);
						ITelephony telephony = ITelephony.Stub
								.asInterface(binder);
						telephony.endCall();
						
						//删除通话记录
						//创建内容管理者
						final ContentResolver cr = getContentResolver();
						final Uri uri = Uri.parse("Content://call_log/calls");
						//注册内容管理者
						cr.registerContentObserver(uri, true, new ContentObserver(new Handler()) {
							public void onChange(boolean selfChange) {
								String where = "number = ?";
								String[] selectionArgs = new String[]{incomingNumber};
								cr.delete(uri, where, selectionArgs);
							};
						});
						//
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			default:
				break;
			}
		};
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//开启服务
		Log.d(TAG, "开启拦截服务");

		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mDao = new BlackDao(this);
	  //开启电话拦截
		mTm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//关闭服务
		Log.d(TAG, "关闭拦截服务");
		mTm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}
}
