package com.itzwf.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itzwf.mobilesafe.domail.BlackInfo;

public class BlackDao {

	private BlackOpenHelper helper;

	public BlackDao(Context context) {
		helper = new BlackOpenHelper(context);
	}

	/**
	 * 增加的方法
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean insert(String number, int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BlackListDB.BlackList.COLUMN_NUMBER, number);
		values.put(BlackListDB.BlackList.COLUMN_TYPE, type);

		long insert = db.insert(BlackListDB.BlackList.TABLE_NAME, null, values);
		db.close();
		return insert != -1;
	}

	/**
	 * 删除的方法
	 * 
	 * @param number
	 * @return
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String whereClause = BlackListDB.BlackList.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };
		int delete = db.delete(BlackListDB.BlackList.TABLE_NAME, whereClause,
				whereArgs);
		db.close();
		return delete != 0;
	}

	/**
	 * 更新的方法
	 * 
	 * @param number
	 * @param type
	 * @return
	 */
	public boolean update(String number, int type) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BlackListDB.BlackList.COLUMN_TYPE, type);
		String whereClause = BlackListDB.BlackList.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };
		int update = db.update(BlackListDB.BlackList.TABLE_NAME, values,
				whereClause, whereArgs);
		db.close();
		return update != 0;
	}

	public int findType(String number) {
		SQLiteDatabase db = helper.getReadableDatabase();
		// select type from 表名 where number=?;
		String sql = "select " + BlackListDB.BlackList.COLUMN_TYPE + " from "
				+ BlackListDB.BlackList.TABLE_NAME + " where "
				+ BlackListDB.BlackList.COLUMN_NUMBER + "=?";
		String[] selectionArgs = new String[] { number };
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		int type = -1;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();
		return type;
	}

	public List<BlackInfo> findAll() {
		List<BlackInfo> list = new ArrayList<BlackInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		// "select number,type from blacklist;"
		String sql = "select " + BlackListDB.BlackList.COLUMN_NUMBER + ","
				+ BlackListDB.BlackList.COLUMN_TYPE + " from "
				+ BlackListDB.BlackList.TABLE_NAME;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;

				list.add(info);
				
			}
			cursor.close();
		}
		db.close();
		return list;
	}
	
	public List<BlackInfo>findPart(int perPageSize,int index){
		List<BlackInfo> list = new ArrayList<BlackInfo>();
		// "select number,type from blacklist limit ? offset ?;"
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql = "select "+BlackListDB.BlackList.COLUMN_NUMBER+","
		+BlackListDB.BlackList.COLUMN_TYPE+" from "+BlackListDB.BlackList.TABLE_NAME+
		" limit "+perPageSize+" offset "+index;
		
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor!=null){
			while(cursor.moveToNext()){
				String number =cursor.getString(0);
				int type = cursor.getInt(1);
				
				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;
				list.add(info);
				
			}
			cursor.close();
		}
		db.close();
		return list;
	}
}