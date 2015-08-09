package com.itzwf.mobilesafe.activity;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PackageUtils;
import com.itzwf.mobilesafe.utils.PreferenceUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	public static final int ERROR = 0;
	private static final int SUCCESS = 1;
	protected static final int REQUEST_CODE_INSTALL = 2;
	public static final int JSONEXCEPTION = 110;
	public static final int CLIENTPROTOCOLEXCEPTION = 120;
	public static final int IOEXCEPTION = 121;

	private static final String TAG = "SplashActivity";
	private TextView mTvVersion;
	private String netName;
	private String netUrl;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ERROR:
				Toast.makeText(SplashActivity.this,
						"code:" + msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				backHome();
				break;
			case SUCCESS:
				showUpdateDialog();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 初始化控件
		mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
		mTvVersion.setText(PackageUtils.getVersionName(this));
		// 检测版本是否要更新
		boolean updateON = PreferenceUtils.getBoolean(getApplicationContext(),
				Constants.AUTO_UPDATE, true);
		if(updateON){
			checkVersionUpdate();
		}else{
			backHome();
		}
	}

	// 弹出提示框提示更新
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(SplashActivity.this);
		// 设置点击取消不可用
		builder.setCancelable(false);
		builder.setTitle("版本更新提示");

		builder.setMessage(netName);
		builder.setPositiveButton("立刻更新", new OnClickListener() {
			// 下载
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 弹出进度条下载
				showProgressDialog();
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				backHome();
			}
		});
		builder.show();

	}

	// 下载进度条窗口
	protected void showProgressDialog() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.show();
		// 下载新版本
		HttpUtils utils = new HttpUtils();
		String url = netUrl;
		final File target = new File(Environment.getExternalStorageDirectory(),
				SystemClock.uptimeMillis() + ".apk");
		utils.download(url, target.getAbsolutePath(),
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// 成功的回调 安装APK
						dialog.dismiss();
						// <intent-filter>
						// <action android:name="android.intent.action.VIEW" />
						// <category
						// android:name="android.intent.category.DEFAULT" />
						// <data android:scheme="content" />
						// <data android:scheme="file" />
						// <data
						// android:mimeType="application/vnd.android.package-archive"
						// />
						// </intent-filter>

						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(target),
								"application/vnd.android.package-archive");
						startActivityForResult(intent, REQUEST_CODE_INSTALL);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
						// total :是下载文件的总大小
						// current 是当前下载到的位置
						dialog.setMax((int) total);
						dialog.setProgress((int) current);
					}

					@Override
					public void onFailure(HttpException e, String arg1) {
						e.printStackTrace();
						// 失败的回调
						backHome();
						dialog.dismiss();
					}
				});
	}

	// 开始安装又会弹出一个安装提示框,这个是系统提供的,根据系统提供的响应码捕捉用户点击安装或取消
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode:自己发送的请求代码
		// resultCode:系统发送的结果代码
		if (requestCode == REQUEST_CODE_INSTALL) {
			switch (resultCode) {
			// RESULT_OK : 点击安装
			// RESULT_CANCELED:取消安装
			case RESULT_OK:

				break;
			case RESULT_CANCELED:
				backHome();
				break;

			default:
				break;
			}
		}
	}

	private void checkVersionUpdate() {

		new Thread(new CheckVersionTask()) {
		}.start();

	}

	private class CheckVersionTask implements Runnable {

		@Override
		public void run() {

			String uri = "http://188.188.2.53/update.txt";
			AndroidHttpClient client = AndroidHttpClient.newInstance("",
					getApplicationContext());
			HttpGet get = new HttpGet(uri);
			HttpParams params = client.getParams();
			// 设置网络超时时间
			HttpConnectionParams.setConnectionTimeout(params, 1000);
			// 设置读取超时时间
			HttpConnectionParams.setSoTimeout(params, 1000);
			try {
				HttpResponse response = client.execute(get);
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					// 访问成功
					String result = EntityUtils.toString(response.getEntity(),
							"utf-8");
					Log.d(TAG, "访问结果:" + result);
					JSONObject jsonobject = new JSONObject(result);
					// 获取服务器上的版本号
					int netCode = jsonobject.getInt("versionCode");
					// 获取本地的版本号
					int localCode = PackageUtils
							.getVersionCode(SplashActivity.this);

					netName = jsonobject.getString("versionName");

					netUrl = jsonobject.getString("url");
					System.out.println(netUrl);
					// 判断版本号
					if (netCode > localCode) {
						// 需要更新,提示框弹出
						Log.d(TAG, "需要更新");
						Message msg = Message.obtain();
						msg.what = SUCCESS;
						mHandler.sendMessage(msg);
					} else {
						// 不需要更新,返回主页面
						backHome();
					}
				} else {
					// 访问失败
					backHome();

				}
			} catch (JSONException e) {
				e.printStackTrace();

				Message msg = Message.obtain();
				msg.what = ERROR;
				msg.obj = JSONEXCEPTION;
				mHandler.sendMessage(msg);

			} catch (ClientProtocolException e) {
				e.printStackTrace();

				Message msg = Message.obtain();
				msg.what = ERROR;
				msg.obj = CLIENTPROTOCOLEXCEPTION;
				mHandler.sendMessage(msg);

			} catch (IOException e) {
				e.printStackTrace();

				Message msg = Message.obtain();
				msg.what = ERROR;
				msg.obj = IOEXCEPTION;
				mHandler.sendMessage(msg);

			} finally {
				if (client != null) {
					client.close();
					client = null;
				}
			}
		}
	}

	// 返回主页
	public void backHome() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2000);
	}
}
