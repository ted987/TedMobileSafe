package com.itzwf.mobilesafe.db;

public interface BlackListDB {

	String DB_NAME = "black.db";
	int DB_VERSION = 1;

	public interface BlackList {
		String TABLE_NAME = "blacklist";
		String COLUMN_ID = "_id";
		String COLUMN_NUMBER = "number";
		String COLUMN_TYPE = "_type";

		// "create table blacklist(_id integer primary key autoincrement,number text unique,type integer);"
		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + "("
				+ COLUMN_ID + " integer primary key autoincrement,"
				+ COLUMN_NUMBER + " text unique," + COLUMN_TYPE + " integer)";

	}
}
