package com.itzwf.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.db.BlackDao;
import com.itzwf.mobilesafe.domail.BlackInfo;

public class SrljActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private static final int REQUEST_CODE_ADD = 100;
	private static final int REQUEST_CODE_UPDATE = 110;
	protected static final int PERPAGESIZE = 25;
	private ImageView mAdd;
	private ListView mManage;
	private ManageAdapter mAdapter;
	private List<BlackInfo> mData;
	private BlackDao bdao;
	private LinearLayout mLoading;
	private ImageView mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_manage);

		bdao = new BlackDao(getApplicationContext());

		mAdd = (ImageView) findViewById(R.id.srlj_add_iv);
		mManage = (ListView) findViewById(R.id.srlj_manage_lv);
		mLoading = (LinearLayout) findViewById(R.id.srlj_loading);
		mEmpty = (ImageView) findViewById(R.id.srlj_empty_iv);
		// 显示进度条
		mLoading.setVisibility(View.VISIBLE);

		mAdd.setOnClickListener(this);
		mManage.setOnItemClickListener(this);

		new Thread() {
			public void run() {
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mData = bdao.findPart(PERPAGESIZE, 0);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mLoading.setVisibility(View.GONE);
						mAdapter = new ManageAdapter();
						mManage.setAdapter(mAdapter);
						// 给ListView设置空的页面
						mManage.setEmptyView(mEmpty);
					}
				});
			};
		}.start();
		// 设置ListView 监听事件
		mManage.setOnScrollListener(new OnScrollListener() {
			// 滑动改变时的回调
			// scrollState是ListView的状态
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ListView最后一个位置
				int lastVisiblePosition = mManage.getLastVisiblePosition();
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lastVisiblePosition == mData.size() - 1) {

					mLoading.setVisibility(View.VISIBLE);
					new Thread() {
						public void run() {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							int index = mData.size();
							final List<BlackInfo> list = bdao.findPart(
									PERPAGESIZE, index);

							mData.addAll(list);
							runOnUiThread(new Runnable() {
								public void run() {
									mLoading.setVisibility(View.GONE);
									mAdapter.notifyDataSetChanged();
									if (list.size() == 0) {
										Toast.makeText(SrljActivity.this,
												"没有更多数据了", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
						};
					}.start();
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

	}

	// 添加黑名单
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getApplicationContext(),
				SrljAddActivity.class);
		intent.setAction(SrljAddActivity.ACTION_ADD);
		startActivityForResult(intent, REQUEST_CODE_ADD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_ADD) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				String number = data
						.getStringExtra(SrljAddActivity.EXTRA_NUMBER);
				int type = data.getIntExtra(SrljAddActivity.EXTRA_TYPE, -1);
				BlackInfo info = new BlackInfo();
				info.number = number;
				info.type = type;
				mData.add(info);
				mAdapter.notifyDataSetChanged();
			default:
				break;
			}
		} else if (requestCode == REQUEST_CODE_UPDATE) {
			if (resultCode == Activity.RESULT_OK) {
				int type = data.getIntExtra(SrljAddActivity.EXTRA_TYPE, -1);
				int position = data.getIntExtra(SrljAddActivity.EXTRA_POSITION,
						-1);

				mData.get(position).type = type;

				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private void startQuery(final int pageSize, final int offset) {
		mLoading.setVisibility(View.VISIBLE);
		// 开线程去查询数据
		new Thread() {
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 接口访问--》net
				// 初始化List数据
				// mDatas = mDao.findAll();
				final List<BlackInfo> part = bdao.findPart(pageSize, offset);

				// 主线程中设置adapter

				runOnUiThread(new Runnable() {
					public void run() {
						mLoading.setVisibility(View.GONE);

						if (part != null) {

							mData.addAll(part);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		}.start();
	}
	public class ManageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mData != null) {
				return mData.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mData != null) {
				return mData.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				// 加载布局
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_manage_item, null);
				// 初始化
				holder = new ViewHolder();
				// 添加标记
				convertView.setTag(holder);
				// 初始化控件
				holder.mNumber = (TextView) convertView
						.findViewById(R.id.item_number_tv);
				holder.mType = (TextView) convertView
						.findViewById(R.id.item_type_tv);
				holder.mDelete = (ImageView) convertView
						.findViewById(R.id.item_delete_iv);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 拿到值
			final BlackInfo info = mData.get(position);
			holder.mNumber.setText(info.number);

			switch (info.type) {
			case BlackInfo.TYPE_CALL:
				holder.mType.setText("电话拦截");
				break;
			case BlackInfo.TYPE_ALL:
				holder.mType.setText("全部拦截");
				break;
			case BlackInfo.TYPE_SMS:
				holder.mType.setText("短信拦截");
				break;
			default:
				break;
			}
			// 删除点击事件
			holder.mDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean delete = bdao.delete(info.number);
					if (delete) {
						// LIST立刻删除数据,刷新LIST
						mData.remove(info);
						
						mAdapter.notifyDataSetChanged();
						
						
						Toast.makeText(SrljActivity.this, "删除成功",
								Toast.LENGTH_SHORT).show();
						
						startQuery(1, mData.size());
					} else {
						Toast.makeText(SrljActivity.this, "删除成功",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView mNumber;
			TextView mType;
			ImageView mDelete;
		}
	}

	// 跳转到更新的页面
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(SrljActivity.this, SrljAddActivity.class);
		// 设置动作
		intent.setAction(SrljAddActivity.ACTION_UPDATE);
		BlackInfo info = mData.get(position);
		// 传当前的参数进去
		intent.putExtra(SrljAddActivity.EXTRA_NUMBER, info.number);
		intent.putExtra(SrljAddActivity.EXTRA_TYPE, info.type);
		intent.putExtra(SrljAddActivity.EXTRA_POSITION, position);
		startActivityForResult(intent, REQUEST_CODE_UPDATE);
	}
}
