package com.itzwf.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;
import com.itzwf.mobilesafe.utils.ToastUtils;

public class SjfdActivity5 extends BaseSjfdActivity {
	private boolean isCheck;
	private CheckBox mCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd5);
		mCheckBox = (CheckBox) findViewById(R.id.sjfd5_checkbox);
		boolean flag = PreferenceUtils.getBoolean(this, Constants.PROTECTING);
		mCheckBox.setChecked(flag);
		//有没有勾选监听事件
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//标记是否有勾选防盗功能
				PreferenceUtils.putBoolean(SjfdActivity5.this, Constants.ISCHECKED, isChecked);
			}
		});
	}

	@Override
	protected boolean perFormNext() {
		// 检查防盗功能是否勾选上

		if (!mCheckBox.isChecked()) {
			ToastUtils.MyToast(SjfdActivity5.this, "要开启防盗功能,必须勾选");
			return true;
		}
		
		Intent intent = new Intent(SjfdActivity5.this, OnSjfdActivity.class);
		startActivity(intent);
		PreferenceUtils.putBoolean(SjfdActivity5.this, Constants.PROTECTING, true);
		PreferenceUtils.putBoolean(SjfdActivity5.this, Constants.SJFD_SETTING, true);
		return false;
	}

	@Override
	protected boolean perFormPre() {
		Intent intent = new Intent(this, SjfdActivity4.class);
		startActivity(intent);
		return false;
	}
}
