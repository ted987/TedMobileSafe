package com.itzwf.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;

public class OnSjfdActivity extends Activity {
	private TextView mNumber;
	private ImageView mLock;
	private boolean flag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onsjfd);
		mNumber=(TextView) findViewById(R.id.onsjfd_number_tv);
		mLock = (ImageView) findViewById(R.id.onsjfd_lock_iv);
		
		String number = PreferenceUtils.getString(this, Constants.SAFE_NUMBER);
		mNumber.setText(number);
		flag = PreferenceUtils.getBoolean(this, Constants.PROTECTING);
		
		if(flag){
			mLock.setImageResource(R.drawable.lock);
		}else{
			mLock.setImageResource(R.drawable.unlock);
		}
	}
	
	//防盗保护点击事件
	public void protect(View view){
		if(flag){
			mLock.setImageResource(R.drawable.unlock);
			PreferenceUtils.putBoolean(OnSjfdActivity.this, Constants.PROTECTING, false);
		}else{
			mLock.setImageResource(R.drawable.lock);
			PreferenceUtils.putBoolean(OnSjfdActivity.this, Constants.PROTECTING, true);
		}
	}
	//重新进入设置向导 点击事件
	public void reinstall (View view){
		Intent intent = new Intent(OnSjfdActivity.this,SjfdActivity1.class);
		startActivity(intent);
		PreferenceUtils.putBoolean(OnSjfdActivity.this, Constants.SJFD_SETTING, false);
		finish();
	}
}
