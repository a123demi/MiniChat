package com.lming.minichat;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import com.lming.minichat.adapter.AppListAdapter;
import com.lming.minichat.bean.ApplicationListBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.ToastUtil;
import com.lming.minichat.util.ViewUtil;

public class ApplicationListActivity extends BaseActivity {
	private GridView appListGv;
	private String[] appListNameArray;
	private AppListAdapter appListAdapter;
	private EditText appSmsContentEt;

	private String loginName;
	private UserBean userBean;

	private List<ApplicationListBean> appListBeanList = new ArrayList<ApplicationListBean>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.application_list_activity);
		appListGv = (GridView) this.findViewById(R.id.app_list_gv);

		appListGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!toastMsgError()) {
					return;
				}

				switch (position) {
				case 0:
					// go to voice
					goToVoiceMsg();
					break;
				case 1:
					// go to phone
					dailPhone();
					break;
				case 2:
					// go to sms
					sendMsg();
					break;
				case 3:
					// to to user detail
					goToUserDetail();
					break;
				}
				ApplicationListActivity.this.finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = this.getIntent();
		loginName = intent.getStringExtra("LoginName");
		if (loginName != null) {
			userBean = UserInfoManager.getInstance().getUserBeanByLoginName(
					loginName);
		}
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		appListBeanList.clear();
		appListNameArray = this.getResources().getStringArray(
				R.array.app_list_name);
		int[] appImgIds = new int[] { R.drawable.app_list_chat_default,
				R.drawable.app_list_phone_default, R.drawable.app_list_sms_default,
				R.drawable.app_list_user_detail_default };
		for (int i = 0; i < appListNameArray.length; i++) {
			ApplicationListBean appBean = new ApplicationListBean();
			appBean.setAppName(appListNameArray[i]);
			appBean.setAppImgId(appImgIds[i]);
			appListBeanList.add(appBean);
		}

		appListAdapter = new AppListAdapter(this, appListBeanList);
		appListGv.setAdapter(appListAdapter);
	}

	/**
	 * 跳转进入语音消息界面
	 */
	private void goToVoiceMsg() {
		Intent msgIntent = ViewUtil.getIntent(this, MessageActivity.class);
		msgIntent.putExtra("loginName", loginName);
		msgIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		this.startActivity(msgIntent);
	}

	/**
	 * 打电话
	 */
	private void dailPhone() {
		if (userBean.getTelphone() == null || "".equals(userBean.getTelphone())) {
			ToastUtil.toast(this, "电话号码不存在!");
			return;
		}

		// 直接连接打电话
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ userBean.getTelphone()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}

	/**
	 * 发送短信
	 */
	private void sendMsg() {
		if (userBean.getTelphone() == null || "".equals(userBean.getTelphone())) {
			ToastUtil.toast(this, "电话号码不存在!");
			// return;
		}

		String title = "";
		if (userBean.getNickName() == null || "".equals(userBean.getNickName())) {
			title = userBean.getLoginName();
		} else {
			title = userBean.getNickName();
		}

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View editView = inflater.inflate(R.layout.app_sms_activity, null);
		appSmsContentEt = (EditText) editView
				.findViewById(R.id.app_sms_content_et);

		new AlertDialog.Builder(this,R.style.DialogStyle)
				.setTitle(title + ":" + userBean.getTelphone())
				.setIcon(R.drawable.app_list_sms_normal).setView(editView)
				.setPositiveButton("发送", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						sendSMS("10086", appSmsContentEt
								.getText().toString().trim());
						dialog.dismiss();

					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 跳转进入用户详情
	 */
	private void goToUserDetail() {
		Intent detailIntent = ViewUtil.getIntent(this, UserDetailActivity.class);
		detailIntent.putExtra("loginName", loginName);
		this.startActivity(detailIntent);
		this.finish();
	}

	/**
	 * 处理当userBean错误提示
	 * 
	 * @return
	 */
	private boolean toastMsgError() {
		if (userBean == null) {
			ToastUtil.toast(this, "用户信息错误!");
			return false;
		}
		return true;
	}

	/**
	 * 发送短信
	 * 
	 * @param phoneNum
	 * @param message
	 */
	private void sendSMS(String phoneNum, String message) {
		// 初始化发短信SmsManager类
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
				UserMainActivity.class), 0);
		// 如果短信内容长度超过70则分为若干条发
		if (message.length() > 70) {
			ArrayList<String> msgs = smsManager.divideMessage(message);
			for (String msg : msgs) {
				smsManager.sendTextMessage(phoneNum, null, msg, pi, null);
			}
		} else {
			smsManager.sendTextMessage(phoneNum, null, message, pi, null);
		}

	}
	
}
