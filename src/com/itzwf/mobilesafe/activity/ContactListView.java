package com.itzwf.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.domail.ContactInfo;
import com.itzwf.mobilesafe.utils.ContactsUtils;

public class ContactListView extends Activity {

	protected static final String KEY_NUM = "number";
	private ListView mListView;
	private List<ContactInfo> mDatas;
	private MyAdapter adapter;
	private static final String TAG = "ContactListView";
	private LinearLayout mLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		mListView = (ListView) findViewById(R.id.contact_lv);
		mLoading = (LinearLayout) findViewById(R.id.srlj_loading);
		new Thread(){
			public void run() {
				mDatas = ContactsUtils.getAllPhone(ContactListView.this);
				SystemClock.sleep(1000);
				runOnUiThread(new Runnable() {
					public void run() {
						mLoading.setVisibility(View.GONE);
						adapter = new MyAdapter();
						mListView.setAdapter(adapter);
					}
				});
				
			};
		}.start();
		

	
		// 设置LISTVIEW点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				ContactInfo info = mDatas.get(position);
				Intent data = new Intent();
				data.putExtra(KEY_NUM, info.number);
				setResult(Activity.RESULT_OK, data);

				finish();
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ContactListView.this,
						R.layout.item_contact, null);
				holder = new ViewHolder();
				// 给VIew做标记
				convertView.setTag(holder);
				// 子控件初始化
				holder.itemName = (TextView) convertView
						.findViewById(R.id.contact_name_item);
				holder.itemNumber = (TextView) convertView
						.findViewById(R.id.contact_number_item);
				holder.itemIcon = (ImageView) convertView
						.findViewById(R.id.contact_iv_item);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactInfo info = mDatas.get(position);

			holder.itemName.setText(info.name);
			holder.itemNumber.setText(info.number);
			Bitmap bitmap = ContactsUtils.getContactsIcon(
					getApplicationContext(), info.icon);
			holder.itemIcon.setImageBitmap(bitmap);

			return convertView;
		}

	}

	private class ViewHolder {
		TextView itemName;
		TextView itemNumber;
		ImageView itemIcon;
	}
}
