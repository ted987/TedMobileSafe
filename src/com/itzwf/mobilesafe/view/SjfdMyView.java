package com.itzwf.mobilesafe.view;

import com.itzwf.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SjfdMyView extends RelativeLayout {
	private ImageView miv;
	public SjfdMyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.sjfd_relativelayout, this);
		
	}

	public SjfdMyView(Context context) {
		super(context);
		initView(context);
	}


}
