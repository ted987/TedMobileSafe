package com.itzwf.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.itzwf.mobilesafe.R;

public class SjfdActivity1 extends BaseSjfdActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd1);
	}

	// 下一步
	@Override
	protected boolean perFormNext() {
		Intent intent = new Intent(this, SjfdActivity2.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean perFormPre() {
		return true;
	}

}
