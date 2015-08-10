package com.itzwf.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.receiver.SafeAdminReseiver;
import com.itzwf.mobilesafe.utils.ToastUtils;

public class SjfdActivity4 extends BaseSjfdActivity {

	protected static final int REQUEST_CODE_ENABLE_ADMIN = 100;
	private RelativeLayout mRl;
	private ImageView mIv;
	private DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd4);
		mIv = (ImageView) findViewById(R.id.sjfd4_admin_iv);
		mRl = (RelativeLayout) findViewById(R.id.sjfd4_admin_rl);
		dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		//初始化子控件
		ComponentName who = new ComponentName(getApplicationContext(),
				SafeAdminReseiver.class);
		if(dpm.isAdminActive(who)){
			mIv.setImageResource(R.drawable.admin_activated);
		}else{
			mIv.setImageResource(R.drawable.admin_inactivated);
		}
		
		// 设备管理员激活点击事件
		mRl.setOnClickListener(new OnClickListener() {

			

			@Override
			public void onClick(View v) {
				ComponentName who = new ComponentName(getApplicationContext(),
						SafeAdminReseiver.class);
				if (dpm.isAdminActive(who)) {
					dpm.removeActiveAdmin(who);
					mIv.setImageResource(R.drawable.admin_inactivated);
				} else {
					// 需要激活
					
					//跳转到激活界面
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							who);
					intent.putExtra(
							DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"峰手机卫士");
					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_ENABLE_ADMIN){
			
			switch (resultCode) {
			case RESULT_OK:
				//激活设备管理员
				mIv.setImageResource(R.drawable.admin_activated);
				break;
			default:
				break;
			}
		}
	}
	@Override
	protected boolean perFormNext() {
		//校验
		ComponentName who = new ComponentName(getApplicationContext(),
				SafeAdminReseiver.class);
		if(!dpm.isAdminActive(who)){
			ToastUtils.MyToast(getApplicationContext(), "请激活设备管理员才能进行下一步");
			return true;
		}
			Intent intent = new Intent(this, SjfdActivity5.class);
			startActivity(intent);
			return false;
		
	}

	@Override
	protected boolean perFormPre() {
		Intent intent = new Intent(this, SjfdActivity3.class);
		startActivity(intent);
		return false;
	}
}
