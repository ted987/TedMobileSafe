package com.itzwf.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.domail.AppInfo;
import com.itzwf.mobilesafe.engine.AppInfoProvider;
import com.itzwf.mobilesafe.view.ProgressDesView;

public class RjglActivity extends Activity {
	public static final String TAG = "RjglActivity";
	private ProgressDesView mPdvRom;
	private ProgressDesView mPdvSd;
	private LinearLayout mLoading;
	private ListView mListView;
	private AppAdapter mAdapter;
	private List<AppInfo> mDatas;
	private List<AppInfo> mUserDatas;
	private List<AppInfo> mSystemDatas;
	private TextView mLvTitle;

	private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String dataString = intent.getDataString();
			String packagename = dataString.replace("package:", "");

			ListIterator<AppInfo> iterator = mUserDatas.listIterator();
			while (iterator.hasNext()) {
				AppInfo next = iterator.next();
				if (next.packageName.equals(packagename)) {
					iterator.remove();
					break;
				}
			}
			// UI更新
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rjgj);
		// 初始化控件
		mPdvRom = (ProgressDesView) findViewById(R.id.rjgj_pdv_rom);
		mPdvSd = (ProgressDesView) findViewById(R.id.rjgj_pdv_sd);
		mLoading = (LinearLayout) findViewById(R.id.srlj_loading);
		mListView = (ListView) findViewById(R.id.rjgj_lv);
		mLvTitle = (TextView) findViewById(R.id.rjgj_item_title);

		// 注册广播监听事件监听程序安装和删除
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mPackageReceiver, filter);

		// 设置ROM进度条
		File dataDirectory = Environment.getDataDirectory();
		// 剩余空间
		long romFreeSpace = dataDirectory.getFreeSpace();
		// 总空间
		long romTotalSpace = dataDirectory.getTotalSpace();
		// 使用的空间
		long romUsableSpace = romTotalSpace - romFreeSpace;

		mPdvRom.setDesTitle("内存:");
		// Formatter.formatFileSize 自动格式化:字节变kb mb等
		mPdvRom.setDesLeft(Formatter.formatFileSize(this, romUsableSpace)
				+ "已用");
		mPdvRom.setDesRight(Formatter.formatFileSize(this, romFreeSpace) + "可用");
		int romProgress = (int) (romUsableSpace * 100f / romTotalSpace + 0.5f);
		mPdvRom.setDesProgress(romProgress);

		// 设置SD进度条
		File storageDirectory = Environment.getExternalStorageDirectory();
		// 剩余空间
		long sdFreeSpace = storageDirectory.getFreeSpace();
		// 总空间
		long sdTotalSpace = storageDirectory.getTotalSpace();
		// 已使用空间
		long sdUsableSpace = sdTotalSpace - sdFreeSpace;

		mPdvSd.setDesTitle("SD卡:");
		mPdvSd.setDesLeft(Formatter.formatFileSize(this, sdUsableSpace) + "已用");
		mPdvSd.setDesRight(Formatter.formatFileSize(this, sdFreeSpace) + "可用");
		int sdProgress = (int) (sdUsableSpace * 100f / sdTotalSpace + 0.5f);
		mPdvSd.setDesProgress(sdProgress);

		// 开启新的线程做耗时的操作
		new Thread() {
			public void run() {
				mLoading.setVisibility(View.VISIBLE);
				mLvTitle.setVisibility(View.GONE);
				SystemClock.sleep(1000);
				mDatas = AppInfoProvider.getAllApps(getApplicationContext());
				mUserDatas = new ArrayList<AppInfo>();
				mSystemDatas = new ArrayList<AppInfo>();
				for (AppInfo info : mDatas) {
					if (info.isSystem) {
						mSystemDatas.add(info);
					} else {
						mUserDatas.add(info);
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 加载数据
						mLoading.setVisibility(View.GONE);
						mLvTitle.setVisibility(View.VISIBLE);
						mLvTitle.setText("用户程序" + "(" + mUserDatas.size() + ")"
								+ "个");
						mAdapter = new AppAdapter();
						mListView.setAdapter(mAdapter);

						// 设置滑动监听事件
						mListView.setOnScrollListener(new OnScrollListener() {

							@Override
							public void onScrollStateChanged(AbsListView view,
									int scrollState) {

							}

							@Override
							public void onScroll(AbsListView view,
									int firstVisibleItem, int visibleItemCount,
									int totalItemCount) {
								// 滑动过程中
								if (mUserDatas == null || mSystemDatas == null) {
									return;
								}
								int userSize = mUserDatas.size();

								if (firstVisibleItem >= 0
										&& firstVisibleItem <= userSize) {
									mLvTitle.setText("用户程序" + "("
											+ mUserDatas.size() + ")" + "个");
								} else if (firstVisibleItem >= userSize + 1) {
									mLvTitle.setText("用户程序" + "("
											+ mSystemDatas.size() + ")" + "个");
								}
							}
						});
					}
				});
			}
		}.start();
		// 设置ListView点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {

			private PopupWindow mWindow;
			private View contentView;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				}
				int userSize = mUserDatas.size();
				if (position == userSize + 1) {
					return;
				}
				AppInfo info = null;
				if (position > 0 && position <= userSize) {
					info = mUserDatas.get(position - 1);
				} else {
					info = mSystemDatas.get(position - userSize - 2);
				}
				if (mWindow == null) {
					contentView = View.inflate(RjglActivity.this,
							R.layout.popup_window, null);
					int width = 200;
					int height = LayoutParams.WRAP_CONTENT;
					mWindow = new PopupWindow(contentView, width, height);
					// 焦点
					mWindow.setFocusable(true);
					// 设置其他位置可触摸
					mWindow.setOutsideTouchable(true);
					// 给上面一个设置透明色
					mWindow.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));
					// 设置属性动画
					mWindow.setAnimationStyle(R.style.PopAnimation);
				}

				final AppInfo app = info;
				contentView.findViewById(R.id.popup_uninstall)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 卸载
								// <action
								// android:name="android.intent.action.VIEW" />
								// <action
								// android:name="android.intent.action.DELETE"
								// />
								// <category
								// android:name="android.intent.category.DEFAULT"
								// />
								// <data android:scheme=package />
								Intent intent = new Intent();
								intent.setAction("android.intent.action.DELETE");
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setData(Uri.parse("package:"
										+ app.packageName));
								startActivity(intent);
								mWindow.dismiss();

								// UI更新,通过广播接收器获取卸载的软件的包名,然后通过迭代器循环用户程序的集合,找到相同包名的程序就进行删除
							}
						});
				contentView.findViewById(R.id.popup_open).setOnClickListener(
						new OnClickListener() {
							// 打开
							@Override
							public void onClick(View v) {
								// 获取包管理器
								PackageManager pm = getPackageManager();
								Intent intent = pm
										.getLaunchIntentForPackage(app.packageName);
								if (intent != null) {
									startActivity(intent);
									mWindow.dismiss();
								}
							}
						});
				contentView.findViewById(R.id.popup_share).setOnClickListener(
						new OnClickListener() {
							// 分享
							@Override
							public void onClick(View v) {
								// <action
								// android:name="android.intent.action.MAIN" />
								// <category
								// android:name="android.intent.category.DEFAULT"
								// />
								// <data
								// android:mimeType="vnd.android-dir/mms-sms" />
								Intent intent = new Intent();
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setType("vnd.android-dir/mms-sms");
								intent.putExtra("sms_body", app.name+" niu B ruan jian");
								startActivity(intent);
								mWindow.dismiss();
							}
						});
				contentView.findViewById(R.id.popup_info).setOnClickListener(
						new OnClickListener() {
							// 信息
							@Override
							public void onClick(View v) {
								// <action
								// android:name="android.settings.APPLICATION_DETAILS_SETTINGS"
								// />
								// <category
								// android:name="android.intent.category.DEFAULT"
								// />
								// <data android:scheme="package" />
								Intent intent = new Intent();
								intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
								intent.addCategory("android.intent.category.DEFAULT");
								intent.setData(Uri.parse("package:"
										+ app.packageName));
								startActivity(intent);
								mWindow.dismiss();

							}
						});
				mWindow.showAsDropDown(view, 60, -80);
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mPackageReceiver);
	}

	public class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// if (mDatas != null) {
			// return mDatas.size();
			// }
			int userCount = 0;
			if (mUserDatas != null) {
				userCount = mUserDatas.size();
				userCount += 1;
			}
			int systemCount = 0;
			if (mSystemDatas != null) {
				systemCount = mSystemDatas.size();
				systemCount += 1;
			}
			return userCount + systemCount;
		}

		@Override
		public Object getItem(int position) {
			// if (mDatas != null) {
			// return mDatas.get(position);
			// }
			return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int userSize = mUserDatas.size();
			if (position == 0) {
				TextView itemTitle = new TextView(getApplicationContext());
				itemTitle.setPadding(4, 4, 4, 4);
				itemTitle.setBackgroundColor(Color.parseColor("#ffcccccc"));
				itemTitle.setTextColor(Color.BLACK);
				itemTitle.setText("用户程序" + "(" + userSize + ")" + "个");
				return itemTitle;
			}
			int systemSize = mSystemDatas.size();
			if (position == userSize + 1) {
				TextView itemTitle = new TextView(getApplicationContext());
				itemTitle.setPadding(4, 4, 4, 4);
				itemTitle.setBackgroundColor(Color.parseColor("#ffcccccc"));
				itemTitle.setTextColor(Color.BLACK);
				itemTitle.setText("系统程序" + "(" + systemSize + ")" + "个");
				return itemTitle;
			}

			ViewHolder holder = null;
			if (convertView == null || convertView instanceof TextView) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_app_manage, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				// 控件初始化
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.rjgl_item_icon_iv);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.rjgl_item_name_tv);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.rjgl_item_size_tv);
				holder.tvInstallPath = (TextView) convertView
						.findViewById(R.id.rjgl_item_position_tv);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}
			AppInfo info = null;

			if (position < userSize + 1) {
				info = mUserDatas.get(position - 1);
			} else {
				info = mSystemDatas.get(position - userSize - 2);
				Log.d(TAG, (position - userSize - 2) + "");
			}

			// 添加数据
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.tvSize.setText(Formatter.formatFileSize(
					getApplicationContext(), info.size));
			holder.tvInstallPath.setText(info.isInstallSD ? "SD卡安装" : "手机内存");
			return convertView;
		}

		private class ViewHolder {
			ImageView ivIcon;
			TextView tvName;
			TextView tvSize;
			TextView tvInstallPath;
		}
	}
}
