package com.itzwf.mobilesafe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.itzwf.mobilesafe.domail.ContactInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsUtils {

	public static List<ContactInfo> getAllPhone(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 用户名
				ContactsContract.CommonDataKinds.Phone.NUMBER,// 手机号码
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

		Cursor cursor = resolver.query(uri, projection, null, null, null);
		List<ContactInfo> list = new ArrayList<ContactInfo>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				long contactid = cursor.getLong(2);

				ContactInfo info = new ContactInfo();

				info.name = name;
				info.number = number;
				info.icon = (int) contactid;

				list.add(info);
			}
			cursor.close();
		}
		return list;
	}

	public static Bitmap getContactsIcon(Context context, long contactid) {

		ContentResolver resolver = context.getContentResolver();

		Uri contactUri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_URI, contactid + "");
		// 获取流
		InputStream is = null;
		try {
			is = ContactsContract.Contacts.openContactPhotoInputStream(
					resolver, contactUri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
