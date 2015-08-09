package com.itzwf.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itzwf.mobilesafe.R;

public class ProgressDesView extends LinearLayout {
	
	private TextView mTvTitle;
	private TextView mTvLeft;
	private TextView mTvRight;
	private ProgressBar mProgressBar;
	
	public ProgressDesView(Context context) {
		this(context, null);
	}
	public ProgressDesView(Context context, AttributeSet attrs) {
		super(context, attrs);
	

		// 类和xml绑定
		View.inflate(context, R.layout.view_progress_des,this);
		// 初始化控件
		mTvTitle = (TextView) findViewById(R.id.view_vpd_type_tv);
		mTvLeft = (TextView) findViewById(R.id.view_vpd_left_tv);
		mTvRight = (TextView) findViewById(R.id.view_vpd_right_tv);
		mProgressBar = (ProgressBar) findViewById(R.id.view_vpd_pb);

	}

	public void setDesTitle(String text) {
		mTvTitle.setText(text);
	}

	public void setDesLeft(String text) {
		mTvLeft.setText(text);
	}

	public void setDesRight(String text) {
		mTvRight.setText(text);
	}

	public void setDesProgress(int progress) {
		mProgressBar.setProgress(progress);
	}
}
