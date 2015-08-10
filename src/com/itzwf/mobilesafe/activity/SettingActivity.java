package com.itzwf.mobilesafe.activity;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.service.SrljService;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;
import com.itzwf.mobilesafe.utils.ServicestateUtils;
import com.itzwf.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity implements OnClickListener {

	private SettingItemView mSivAutoUpdate;
	private SettingItemView mSrlj;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
		mSrlj = (SettingItemView) findViewById(R.id.setting_siv_srlj);
		
		mSivAutoUpdate.setOnClickListener(this);
		mSrlj.setOnClickListener(this);
		//回显,初始化
		boolean update = PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE);
		mSivAutoUpdate.settoggle(update);
		
	}
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.setting_siv_autoupdate:
			mSivAutoUpdate.toggle();
			
			boolean toggle = mSivAutoUpdate.isToggle();
			
			PreferenceUtils.putBoolean(getApplicationContext(), Constants.AUTO_UPDATE, toggle);
			break;
		case R.id.setting_siv_srlj:
			//判断服务是否开启
			if(ServicestateUtils.isRunning(getApplicationContext(), SrljService.class)){
				Intent intent = new Intent(getApplicationContext(), SrljService.class);
					stopService(intent);
					mSrlj.settoggle(false);
			}else{
				Intent intent = new Intent(getApplicationContext(), SrljService.class);
				startService(intent);
				mSrlj.settoggle(true);
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	
		
		if(ServicestateUtils.isRunning(getApplicationContext(), SrljService.class)){
			mSrlj.settoggle(true);
		}else{
			mSrlj.settoggle(false);
		}
	}
}
