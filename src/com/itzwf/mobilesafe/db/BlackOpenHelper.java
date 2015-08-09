package com.itzwf.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackOpenHelper extends SQLiteOpenHelper {

	public BlackOpenHelper(Context context) {
		super(context, BlackListDB.DB_NAME, null, BlackListDB.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql =BlackListDB.BlackList.SQL_CREATE_TABLE;
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
