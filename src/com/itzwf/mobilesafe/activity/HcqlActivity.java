package com.itzwf.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.domail.CacheInfo;

public class HcqlActivity extends Activity implements OnClickListener {
	private PackageManager mPm;
	private ImageView mLine;
	private ImageView mIcon;
	private TextView mName;
	private ProgressBar mProgressBar;
	private TextView mCacheSize;
	private ListView mListView;
	private List<CacheInfo> mDatas;
	private scanTask scan;
	private RelativeLayout sRlScan;
	private RelativeLayout sRlFinish;
	private TextView mResultTv;
	private Button mAgainBt;
	private int mCacheAppCount;
	private long mCacheSizeAmount;
	private CacheCleanAdapter mAdapter;
	private Button mCleanAll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hcql);

		// 数据初始化
		mLine = (ImageView) findViewById(R.id.hcql_scan_line);
		mIcon = (ImageView) findViewById(R.id.hcql_scan_icon);
		mName = (TextView) findViewById(R.id.hcql_name_tv);
		mCacheSize = (TextView) findViewById(R.id.hcql_size_tv);
		mListView = (ListView) findViewById(R.id.hcql_item);
		mProgressBar = (ProgressBar) findViewById(R.id.hcql_progressBar);
		sRlScan = (RelativeLayout) findViewById(R.id.hcql_scan_rl);
		sRlFinish = (RelativeLayout) findViewById(R.id.hcql_finish_rl);
		mResultTv = (TextView) findViewById(R.id.hcql_finish_tv);
		mAgainBt = (Button) findViewById(R.id.hcql_finish_bt);
		mCleanAll = (Button) findViewById(R.id.hcql_cleanall_btn);
		// 一键清理点击事件
		mCleanAll.setOnClickListener(this);

		// 重新扫描点击事件
		mAgainBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				starScan();
			}
		});

		mAdapter = new CacheCleanAdapter();

		mPm = getPackageManager();
		// 开始扫描
		starScan();
	}

	private void starScan() {
		if (scan != null) {
			scan.stop();
			scan = null;
		}
		scan = new scanTask();
		scan.execute();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (scan != null) {
			scan.stop();
			scan = null;
		}
	}

	private final class scanTask extends AsyncTask<Void, CacheInfo, Void> {

		private int max;
		private int progress;
		private boolean isFinish;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDatas = new ArrayList<CacheInfo>();
			mListView.setAdapter(mAdapter);
			sRlScan.setVisibility(View.VISIBLE);
			sRlFinish.setVisibility(View.GONE);
			// 给扫描线做属性动画
			TranslateAnimation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 1);
			animation.setDuration(200);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			mLine.startAnimation(animation);

			mCacheAppCount = 0;
			mCacheSizeAmount = 0;

			mCleanAll.setEnabled(false);
			mAgainBt.setEnabled(false);
		}

		// 耗时操作
		@Override
		protected Void doInBackground(Void... params) {
			// 读取所有数据
	
			List<PackageInfo> packages = mPm
					.getInstalledPackages(PackageManager.GET_ACTIVITIES);
			max = packages.size();
			for (PackageInfo pack : packages) {
				progress++;
				if (isFinish) {
					break;
				}
				try {
					Method method = mPm.getClass().getDeclaredMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);
					method.invoke(mPm, pack.packageName, mStatsObserver);
				} catch (Exception e) {
					e.printStackTrace();
				}
			SystemClock.sleep(100);

			}
			return null;

		}

		public void stop() {
			isFinish = true;
		}

		// 更新进度
		public void updataProgress(CacheInfo... progress) {
			publishProgress(progress);

		}

		@Override
		protected void onProgressUpdate(CacheInfo... values) {
			super.onProgressUpdate(values);
			if (isFinish) {
				return;
			}
			// 图标变化
			mIcon.setImageDrawable(values[0].icon);
			// 进度条变化
			mProgressBar.setMax(max);
			mProgressBar.setProgress(progress);
			// 文件名变化
			mName.setText(values[0].name);
			// 缓存大小变化
			mCacheSize.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							values[0].cacheSize));

			mAdapter.notifyDataSetChanged();

			mListView.smoothScrollToPosition(mDatas.size());
		}

		// 完成耗时操作后
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isFinish) {
				return;
			}
			sRlScan.setVisibility(View.GONE);
			sRlFinish.setVisibility(View.VISIBLE);

			mResultTv.setText("总共有"
					+ mCacheAppCount
					+ "个程序有缓存,共"
					+ Formatter.formatFileSize(getApplicationContext(),
							mCacheSizeAmount));
			mListView.smoothScrollToPosition(0);
			mCleanAll.setEnabled(true);
			mAgainBt.setEnabled(true);
		}

	}

	private class CacheCleanAdapter extends BaseAdapter {

		private ViewHolder holder;

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
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_cache_clean, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.hcql_item_name);
				holder.itemSize = (TextView) convertView
						.findViewById(R.id.hcql_item_size);
				holder.itemIcon = (ImageView) convertView
						.findViewById(R.id.hcql_item_icon);
				holder.itemClean = (ImageView) convertView
						.findViewById(R.id.hcql_item_clean);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 每次都忘记读取当前info是用集合来提取
			
			final CacheInfo info = mDatas.get(position);

			holder.itemName.setText(info.name);
			holder.itemSize.setText("缓存大小:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.cacheSize));
			holder.itemIcon.setImageDrawable(info.icon);

			if (info.cacheSize > 0) {
				holder.itemClean.setVisibility(View.VISIBLE);
			} else {
				holder.itemClean.setVisibility(View.GONE);
			}
			// 设置单个清理按钮点击
			holder.itemClean.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// mPm.deleteApplicationCacheFiles(packageName,
					// mClearCacheObserver);
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					intent.setData(Uri.parse("package:" + info.packageName));
					startActivity(intent);
				}
			});
			return convertView;
		}

		private class ViewHolder {
			TextView itemName;
			TextView itemSize;
			ImageView itemIcon;
			ImageView itemClean;
		}
	}

	final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
		public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
			CacheInfo info = new CacheInfo();
			ApplicationInfo applicationInfo;

			try {
				applicationInfo = mPm.getApplicationInfo(stats.packageName, 0);
				info.cacheSize = stats.cacheSize;
				info.icon = applicationInfo.loadIcon(mPm);
				info.name = applicationInfo.loadLabel(mPm).toString();
				info.packageName = stats.packageName;
				// 如果是有缓存的文件,就把他放在第一位,并且计算出缓存总数和缓存文件的个数
				if (info.cacheSize > 0) {
					mDatas.add(0, info);
					mCacheAppCount++;
					mCacheSizeAmount += info.cacheSize;
				} else {

					mDatas.add(info);
				}
				// 推送进度
				scan.updataProgress(info);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	};

	final class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName,
				final boolean succeeded) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(HcqlActivity.this, "清除成功",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		// public abstract void freeStorageAndNotify(long freeStorageSize,
		// IPackageDataObserver observer);

		if (mCacheSizeAmount <= 0) {
			return;
		}
		// 隐藏的.,用反射调用系统方法
		try {
			Method method = mPm.getClass().getDeclaredMethod(
					"freeStorageAndNotify", long.class,
					IPackageDataObserver.class);
			method.invoke(mPm, Long.MAX_VALUE, new ClearCacheObserver());
			// 重新扫描
			starScan();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}