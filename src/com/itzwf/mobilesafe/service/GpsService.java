package com.itzwf.mobilesafe.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.itzwf.mobilesafe.utils.Constants;
import com.itzwf.mobilesafe.utils.PreferenceUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.LoaderManager;
import android.telephony.SmsManager;

public class GpsService extends Service {

	private LocationManager mLm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		long minTime = 5 * 1000;
		float minDistance = 10;
		mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 服务关闭后关闭GPS测量
		mLm.removeUpdates(listener);
	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 当GPS芯片连接状态变化时 的回调
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 当GPS芯片可用时的回调
		}

		@Override
		public void onProviderDisabled(String provider) {
			// 当GPS芯片不可用时的回调
		}

		@Override
		public void onLocationChanged(Location location) {
			// 位置改变时的回调
			double latitude = location.getLatitude();// 纬度
			double longitude = location.getLongitude();// 经度
			// 发短信告诉当前的位置
			loadLocation(latitude, longitude);

		}
	};

	private void loadLocation(final double latitude, final double longitude) {
		// 接口地址：http://lbs.juhe.cn/api/getaddressbylngb
		// 支持格式：JSON/XML
		// 请求方式：GET
		// 请求示例：http://lbs.juhe.cn/api/getaddressbylngb?lngx=116.407431&lngy=39.914492
		// 请求参数：
		// 名称 类型 必填 说明
		// lngx String Y google地图经度 (如：119.9772857)
		// lngy String Y google地图纬度 (如：27.327578)
		// dtype String N 返回数据格式：json或xml,默认json

		String url = "http://lbs.juhe.cn/api/getaddressbylngb";

		HttpUtils utils = new HttpUtils();
		// 读取超时
		utils.configSoTimeout(30000);
		// 网络超时
		utils.configTimeout(30000);
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("lngx", longitude + "");
		params.addQueryStringParameter("lngy", latitude + "");
		params.addQueryStringParameter("dtype", "json");

		utils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException e, String msg) {
				// 失败
				e.printStackTrace();
				sendSms("longitude" + longitude + "----" + "latitude"
						+ latitude);
				stopSelf();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// 成功
				String json = responseInfo.result;
				try {
					JSONObject root = new JSONObject(json);
					JSONObject rowObject = root.getJSONObject("row");
					JSONObject resultObject = rowObject.getJSONObject("result");
					String address = resultObject
							.getString("formatted_address");
					sendSms(address + "  longitude:" + longitude
							+ "  latitude:" + latitude);
					stopSelf();

				} catch (JSONException e) {
					e.printStackTrace();
					sendSms("longitude" + longitude + "----" + "latitude"
							+ latitude);
					stopSelf();
				}
			}
		});
	}

	public void sendSms(String text) {
		SmsManager sms = SmsManager.getDefault();
		String address = PreferenceUtils.getString(getApplicationContext(),
				Constants.SAFE_NUMBER);
		sms.sendTextMessage(address, null, text, null, null);
	}
}
