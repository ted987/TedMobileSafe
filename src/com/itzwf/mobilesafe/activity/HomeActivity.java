package com.itzwf.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.domail.HomeItem;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;

public class HomeActivity extends Activity {
	public static final String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
			"软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	public static final String[] DESCRIBES = new String[] { "远程定位手机", "全民拦截骚扰",
			"管理你的软件", "管理正在运行", "流量一目了然", "病毒无法藏身", "系统快如火箭", "常用工具大全" };
	public static final int[] ICONS = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };
	
	private ImageView mLogo;
	private GridView mGridView;

	List<HomeItem> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// 初始化控件
		mLogo = (ImageView) findViewById(R.id.home_iv_logo);
		mGridView = (GridView) findViewById(R.id.home_gv);

		// list集合初始化才能显示数据
		list = new ArrayList<HomeItem>();
		for (int i = 0; i < ICONS.length; i++) {
			HomeItem item = new HomeItem();
			item.describe = DESCRIBES[i];
			item.icon = ICONS[i];
			item.title = TITLES[i];

			list.add(item);
		}
		// 设置适配器
		mGridView.setAdapter(new GvAdapter());

		// setDuration :转的时间----就是转得多快
		// setRepeatCount:永久的旋转
		// mLogo.setRotationY(rotationY)
		ObjectAnimator animator = ObjectAnimator.ofFloat(mLogo, "rotationY", 0,
				90, 180, 270, 360);
		animator.setDuration(2000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);
		animator.setRepeatMode(ObjectAnimator.REVERSE);
		animator.start();
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case Constants.SJFD:
						
					performsjfd();
					break;
				case Constants.SRLJ:
					
					performsrlj();
					break;
				case Constants.RJGL:
					
					performrjgl();
					break;
				case Constants.JCGL:
					
					performjcgl();
					break;
				case Constants.HCQL:
					
					performhcql();
					break;
				default:
					break;
				}
			}
		});
	}  
	//跳转到缓存清理
	protected void performhcql() {
		Intent intent = new Intent(getApplicationContext(),HcqlActivity.class);
		startActivity(intent);
	}
	//跳转到软件管理
	protected void performrjgl() {
		Intent intent = new Intent(getApplicationContext(),RjglActivity.class);
		startActivity(intent);
		
	}
	//跳转到进程管理
	protected void performjcgl() {
		Intent intent = new Intent(getApplicationContext(),JcglActivity.class);
		startActivity(intent);
	}

	protected void performsrlj() {
		Intent intent = new Intent(getApplicationContext(),SrljActivity.class);
		startActivity(intent);
	}

	protected void performsjfd() {
		String psw = PreferenceUtils.getString(this, Constants.SJFD_PSW);
		if (TextUtils.isEmpty(psw)) {
			// 第一次进来,弹出设置密码对话框
			showSetPswDialog();
		} else {
			// 否则,就弹出输入密码对话框
			showInputPswDialog();
		}

	}

	protected void showSetPswDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_psw_set, null);
		final EditText edsz = (EditText) view.findViewById(R.id.dialog_sz_et);
		final EditText edqr = (EditText) view.findViewById(R.id.dialog_qr_et);
		Button okbt = (Button) view.findViewById(R.id.dialog_bt_ok);
		Button cancelbt = (Button) view.findViewById(R.id.dialog_bt_cancel);

		builder.setView(view);
		final AlertDialog dialog = builder.create();

		// 确认按键设置点击事件
		okbt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String psw = edsz.getText().toString().trim();
				if (TextUtils.isEmpty(psw)) {
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					edsz.requestFocus();// 获取焦点
					return;
				}

				String cfpsw = edqr.getText().toString().trim();
				if (TextUtils.isEmpty(cfpsw)) {
					Toast.makeText(HomeActivity.this, "确认密码不能为空",
							Toast.LENGTH_SHORT).show();
					edqr.requestFocus();
					return;
				}
				// 判断两次密码是否相同,不同就吐司,相同就储存
				if (!psw.equals(cfpsw)) {
					Toast.makeText(HomeActivity.this, "密码不一致,请重新输入",
							Toast.LENGTH_SHORT).show();
					edsz.setText("");
					edqr.setText("");
					edsz.requestFocus();
					return;
				}
				
				PreferenceUtils.putString(HomeActivity.this,Constants.SJFD_PSW , psw);
				//进入手机界面
				showsjfd();
				//关闭对话框
				dialog.dismiss();
				
			}
		});
		cancelbt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		 dialog.show();
	}

	protected void showsjfd() {
		boolean flag = PreferenceUtils.getBoolean(HomeActivity.this, Constants.SJFD_SETTING);
		if(flag){
			//设置完成,进入 防盗功能最终显示页面
			Intent intent = new Intent(getApplicationContext(),OnSjfdActivity.class );
			startActivity(intent);
		}else{
			//进入手机防盗设置向导页面
			Intent intent = new Intent(getApplicationContext(),SjfdActivity1.class );
			startActivity(intent);
		}
	}

	// 弹出输入密码对话框
	protected void showInputPswDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_psw_input, null);
		final EditText input = (EditText) view.findViewById(R.id.dialog_input_et);
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);
		builder.setView(view);
		
		final AlertDialog dialog = builder.create();
		
		//确认
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String inpsw = input.getText().toString().trim();
				if(TextUtils.isEmpty(inpsw)){
					Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					input.requestFocus();
					return;
				}
				String psw = PreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PSW);
				if(!inpsw.equals(psw)){
					Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
					input.requestFocus();
					input.setText("");
					return;
				}else{
					showsjfd();
					dialog.dismiss();
				}
			}
		});
		//取消
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private class GvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.item_gv, null);

			TextView title = (TextView) view
					.findViewById(R.id.item_home_tv_title);
			TextView describe = (TextView) view
					.findViewById(R.id.item_home_tv_describe);
			ImageView icon = (ImageView) view
					.findViewById(R.id.item_home_iv_icon);

			HomeItem item = list.get(position);
			title.setText(item.title);
			describe.setText(item.describe);
			icon.setImageResource(item.icon);
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	// 进入设置中心
	public void setting(View v) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}
}
