package com.itzwf.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.itzwf.mobilesafe.R;

public class SjfdActivity4 extends BaseSjfdActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sjfd4);
	}

	@Override
	protected boolean perFormNext() {
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
