package com.itzwf.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;

public class SjfdActivity2 extends BaseSjfdActivity {
	private RelativeLayout mBindingSim;
	private ImageView mLock;
	private TelephonyManager mTm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd2);

		mBindingSim = (RelativeLayout) findViewById(R.id.sjfd2_bind_rl);
		mLock = (ImageView) findViewById(R.id.sjfd2_lock_iv);
		// 开启系统服务,调取手机服务
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		String sim = PreferenceUtils.getString(getApplicationContext(),
				Constants.SIM);
		if (TextUtils.isEmpty(sim)) {
			mLock.setImageResource(R.drawable.unlock);
		} else {
			mLock.setImageResource(R.drawable.lock);
		}

		mBindingSim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String sim = PreferenceUtils.getString(getApplicationContext(),
						Constants.SIM);
				if (TextUtils.isEmpty(sim)) {
					// 未绑定,打开绑定
					sim = mTm.getSimSerialNumber();
					PreferenceUtils.putString(SjfdActivity2.this,
							Constants.SIM, sim);
					mLock.setImageResource(R.drawable.lock);
				} else {
					// 已绑定, 解除绑定
					PreferenceUtils.putString(SjfdActivity2.this,
							Constants.SIM, null);
					mLock.setImageResource(R.drawable.unlock);
				}
			}
		});
	}

	// 下一步
	@Override
	protected boolean perFormNext() {
		// 检测是否绑定SIM卡

		String sim = PreferenceUtils.getString(getApplicationContext(),
				Constants.SIM);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(SjfdActivity2.this, "要使用手机防盗功能必须绑定SIM卡",
					Toast.LENGTH_SHORT).show();
			return true;
		}
		Intent intent = new Intent(this, SjfdActivity3.class);
		startActivity(intent);
		return false;

	}

	// 上一步
	@Override
	protected boolean perFormPre() {
		Intent intent = new Intent(this, SjfdActivity1.class);
		startActivity(intent);
		return false;
	}
}
