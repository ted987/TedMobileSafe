package com.itzwf.mobilesafe.utils;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusTextView extends TextView {

	public FocusTextView(Context context) {
		super(context,null);
	}
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		  android:singleLine="true"
   //         android:ellipsize="marquee"
//		            android:focusable="true"
//		            android:focusableInTouchMode="true"
//		            android:marqueeRepeatLimit="marquee_forever"
		setEllipsize(TruncateAt.MARQUEE);
		setSingleLine(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setMarqueeRepeatLimit(-1);
	}
	//欺骗程序有焦点
	@Override
	public boolean isFocused() {
		return super.isFocused();
	}
	//因为如果focused是false的时候会停止走马灯,所以让他在对的时候开始走马灯,停止的时候就不做操作了
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		if(focused){
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}
	//原理和上面那个一样,重写方法让判断在false的时候不做操作,就不会停止走马灯
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus){
			super.onWindowFocusChanged(hasWindowFocus);
			
		}
	}
}
