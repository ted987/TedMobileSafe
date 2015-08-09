package com.itzwf.mobilesafe.activity;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;
import com.itzwf.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity implements OnClickListener {

	private SettingItemView mSivAutoUpdate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
		
		mSivAutoUpdate.setOnClickListener(this);
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

		default:
			break;
		}
		
	}
}
