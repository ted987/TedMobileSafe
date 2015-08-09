package com.itzwf.mobilesafe.view;

import com.itzwf.mobilesafe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private TextView mTitle;
	private ImageView mToggle;
	private final static int BKG_FIRST = 0;
	private final static int BKG_MIDDLE = 1;
	private final static int BKG_LAST = 2;
	private boolean isToggle;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet set) {
		super(context, set);
		View view = View.inflate(context, R.layout.view_setting_item, this);

		mTitle = (TextView) findViewById(R.id.setting_item_title);
		mToggle = (ImageView) findViewById(R.id.setting_item_btn);

		TypedArray ta = context.obtainStyledAttributes(set,
				R.styleable.SettingItemView);
		
		String title = ta.getString(R.styleable.SettingItemView_title);
		//回收
		ta.recycle();
		
		mTitle.setText(title);
		
		int bkg = ta.getInt(R.styleable.SettingItemView_itbackground, 0);
		
		 switch (bkg) {
		case BKG_FIRST:
			view.setBackgroundResource(R.drawable.setting_first_selector);
			break;
		case BKG_MIDDLE:
			view.setBackgroundResource(R.drawable.setting_middle_selector);
			break;
		case BKG_LAST:
			view.setBackgroundResource(R.drawable.setting_last_selector);
			break;
		default:
			view.setBackgroundResource(R.drawable.setting_first_selector);
			break;
		}
		 settoggle(isToggle);
	}

	public void settoggle(boolean on){
		this.isToggle = on;
		if(on){
			mToggle.setImageResource(R.drawable.on);
		}else{
			mToggle.setImageResource(R.drawable.off);
		}
	}
	
	public void toggle(){
		settoggle(!isToggle);	
	}
	
	public boolean isToggle(){
		return isToggle;
	}
}
