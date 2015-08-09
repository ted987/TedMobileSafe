package com.itzwf.mobilesafe.activity;

import com.itzwf.mobilesafe.R;

import android.app.Activity;
import android.app.PendingIntent.OnFinished;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View;

public abstract class BaseSjfdActivity extends Activity {
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				float x1 = e1.getRawX();
				float x2 = e2.getRawX();
				float y1 = e1.getRawY();
				float y2 = e1.getRawY();
				// 判断是否在做Y轴动作
				if (Math.abs(y1 - y2) > Math.abs(x1 - x2)) {
					return false;
				}
				// 滑动到下一页
				if (x1 > x2 + 50) {
					doNext();
					return true;
				}
				//滑动到上一页
				if (x1 + 50 < x2) {
					doPre();
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	private void doNext() {
		if (perFormNext()) {
			return;
		}
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
	}

	private void doPre() {
		if (perFormPre()) {
			return;
		}
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}

	public void clickNext(View v) {
		doNext();
	}

	public void clickPre(View v) {
		doPre();
	}

	// 不相同的代码交给儿子去做,抽象方法儿子必须要实现
	protected abstract boolean perFormNext();

	protected abstract boolean perFormPre();
}
