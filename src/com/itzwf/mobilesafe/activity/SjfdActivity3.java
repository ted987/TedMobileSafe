package com.itzwf.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;
import com.itzwf.mobilesafe.utils.ToastUtils;

public class SjfdActivity3 extends BaseSjfdActivity {

	protected static final int REQUEST_CODE_CONTACT = 0;
	private EditText mNumber;
	private Button mBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd3);
		mNumber = (EditText) findViewById(R.id.sjfd_safenumber_et);
		mBt = (Button) findViewById(R.id.sjfd_safenumber_bt);
		String number = PreferenceUtils.getString(this, Constants.SAFE_NUMBER);
		mNumber.setText(number);
		if (!TextUtils.isEmpty(number)) {
			mNumber.setSelection(number.length());
		}
		// 设置按钮点击事件
		mBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SjfdActivity3.this,
						ContactListView.class);
				startActivityForResult(intent, REQUEST_CODE_CONTACT);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CONTACT) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				
				String number = data.getStringExtra(ContactListView.KEY_NUM);
				mNumber.setText(number);
				PreferenceUtils.putString(SjfdActivity3.this,  Constants.SAFE_NUMBER, number);
				if(!TextUtils.isEmpty(number)){
					mNumber.setSelection(number.length());
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected boolean perFormNext() {
		String number = mNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			ToastUtils.MyToast(getApplicationContext(), "安全号码不能为空");
			return true;
		}
		// 保存安全号码
		PreferenceUtils.putString(this, Constants.SAFE_NUMBER, number);
		Intent intent = new Intent(this, SjfdActivity4.class);
		startActivity(intent);
		return false;

	}

	@Override
	protected boolean perFormPre() {
		Intent intent = new Intent(this, SjfdActivity2.class);
		startActivity(intent);
		return false;
	}
}
