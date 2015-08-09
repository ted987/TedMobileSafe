package com.itzwf.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	public static void MyToast(Context context, CharSequence text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
}
