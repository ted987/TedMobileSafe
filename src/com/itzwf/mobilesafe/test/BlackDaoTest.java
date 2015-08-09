package com.itzwf.mobilesafe.test;

import com.itzwf.mobilesafe.db.BlackDao;

import android.test.AndroidTestCase;

public class BlackDaoTest extends AndroidTestCase {

	public void addTest(){
		BlackDao bdao = new BlackDao(getContext());
		for (int i = 0; i < 200; i++) {
			bdao.insert("135123456"+i, 2);
		}
		
	}
}
