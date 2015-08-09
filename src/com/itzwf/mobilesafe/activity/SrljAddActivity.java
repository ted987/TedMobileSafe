package com.itzwf.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itzwf.mobilesafe.R;
import com.itzwf.mobilesafe.db.BlackDao;
import com.itzwf.mobilesafe.domail.BlackInfo;

public class SrljAddActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_ADD = "add";
	public static final String EXTRA_NUMBER = "number";
	public static final String EXTRA_TYPE = "type";
	public static final String EXTRA_POSITION = "position";
	private static final String TAG = "SrljAddActivity";
	private TextView mTitle;
	private EditText mNumber;
	private RadioGroup mType;
	private Button mSave;
	private Button mCancel;
	private int mCheckedId = -1;
	private boolean isUpdate;
	private int position = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_add);
		//
		mTitle = (TextView) findViewById(R.id.add_title_tv);
		mNumber = (EditText) findViewById(R.id.add_number_et);
		mType = (RadioGroup) findViewById(R.id.add_type_rg);
		mSave = (Button) findViewById(R.id.add_save_bt);
		mCancel = (Button) findViewById(R.id.add_cl_bt);
		// 监听按钮
		mSave.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		mType.setOnCheckedChangeListener(this);
		// 接受传过来的动作
		Intent intent = getIntent();
		String action = intent.getAction();
		// 根据动作,判断是更新页面还是增加页面
		if (action.equals(ACTION_UPDATE)) {

			isUpdate = true;
			// 改变UI
			mTitle.setText("更新黑名单");
			mNumber.setEnabled(false);
			mSave.setText("更新");
			// 显示初始值
			String number = intent.getStringExtra(EXTRA_NUMBER);
			mNumber.setText(number);
			position = intent.getIntExtra(EXTRA_POSITION, -1);
			int type = intent.getIntExtra(EXTRA_TYPE, -1);

			switch (type) {
			case BlackInfo.TYPE_CALL:
				mCheckedId = R.id.add_call_rb;
				break;
			case BlackInfo.TYPE_SMS:
				mCheckedId = R.id.add_sms_rb;
				break;
			case BlackInfo.TYPE_ALL:
				mCheckedId = R.id.add_all_rb;
				break;

			default:
				break;
			}
			// 传入参数
			mType.check(mCheckedId);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 点击保存
		case R.id.add_save_bt:

			showAddButton();
			break;
		// 点击取消
		case R.id.add_cl_bt:
			showCancelButton();
			break;

		default:
			break;
		}
	}

	private void showCancelButton() {
		finish();
	}

	private void showAddButton() {

		String number = mNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(getApplicationContext(), "手机号码不能为空",
					Toast.LENGTH_SHORT).show();
		}
		if (mCheckedId == -1) {
			Toast.makeText(getApplicationContext(), "拦截类型不能为空",
					Toast.LENGTH_SHORT).show();
		}
		int type = -1;
		switch (mCheckedId) {
		case R.id.add_call_rb:
			type = BlackInfo.TYPE_CALL;
			break;
		case R.id.add_sms_rb:
			type = BlackInfo.TYPE_SMS;
			break;
		case R.id.add_all_rb:
			type = BlackInfo.TYPE_ALL;
			break;
		default:
			break;
		}
		BlackDao db = new BlackDao(SrljAddActivity.this);
		// 回显更新数据
		if (isUpdate) {
			db.update(number, type);
			Intent data = new Intent();
			data.putExtra(EXTRA_TYPE, type);
			data.putExtra(EXTRA_POSITION, position);
			setResult(RESULT_OK, data);
		} else {

			boolean insert = db.insert(number, type);
			if (insert) {
				// 回显增加数据
				Intent date = new Intent();
				date.putExtra(EXTRA_NUMBER, number);
				date.putExtra(EXTRA_TYPE, type);
				setResult(RESULT_OK, date);
			} else {
				Toast.makeText(SrljAddActivity.this, "号码已存在,不能重复输入",
						Toast.LENGTH_SHORT).show();
			}
		}

		finish();

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		this.mCheckedId = checkedId;
	}
}
