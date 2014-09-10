package com.lming.minichat;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.lming.minichat.bean.UserBean;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.ToastUtil;
import com.lming.minichat.util.ViewUtil;

public class UserDetailActivity extends BaseActivity implements OnClickListener {

	private EditText udNameEt, udNickNameEt, udGroupEt, udOldEt, udEmailEt,
			udTelphoneEt;
	private Spinner udSexSp;
	private Button udCancleBtn, udConfirmBtn;
	private String[] sexArray;
	private ArrayAdapter<String> sexAdapter = null;
	private String loginName = null;
	private UserBean userBean = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_detail_activity);

		sexArray = this.getResources().getStringArray(R.array.sex_array);
		findById();

		sexAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, sexArray);
		sexAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		udSexSp.setAdapter(sexAdapter);

	}

	@Override
	public void findById() {
		udNameEt = (EditText) this.findViewById(R.id.user_detail_name_et);
		udNickNameEt = (EditText) this
				.findViewById(R.id.user_detail_nick_name_et);
		udGroupEt = (EditText) this.findViewById(R.id.user_detail_group_et);
		udOldEt = (EditText) this.findViewById(R.id.user_detail_old_et);
		udEmailEt = (EditText) this.findViewById(R.id.user_detail_email_et);
		udTelphoneEt = (EditText) this
				.findViewById(R.id.user_detail_telphone_et);
		udSexSp = (Spinner) this.findViewById(R.id.user_detail_sex_sp);
		udCancleBtn = (Button) this.findViewById(R.id.user_detail_cancle_btn);
		udConfirmBtn = (Button) this.findViewById(R.id.user_detail_confirm_btn);

		udCancleBtn.setOnClickListener(this);
		udConfirmBtn.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		initData();
	}

	private void initData() {
		Intent intent = this.getIntent();
		loginName = intent.getStringExtra("loginName");
		if (loginName == null || "".equals(loginName)) {
			ToastUtil.toast(this, "用户不存在!");
			return;
		}

		userBean = UserInfoManager.getInstance().getUserBeanByLoginName(
				loginName);

		if (userBean == null) {
			ToastUtil.toast(this, "根据LoginName无法获取用户信息!");
			return;
		}

		udNameEt.setText(loginName);
		udGroupEt.setText(UserInfoManager.getInstance()
				.getGroupBeanByGroupUserId(userBean.getGroupUserId())
				.getGroupName());
		if (userBean.getNickName() != null
				&& !"".equals(userBean.getNickName())) {
			udNickNameEt.setText(userBean.getNickName());
		}
		if (userBean.getEmail() != null && !"".equals(userBean.getEmail())) {
			udEmailEt.setText(userBean.getEmail());
		}
		if (userBean.getOld() == 0) {
			udOldEt.setText("");
		} else {
			udOldEt.setText(String.valueOf(userBean.getOld()));
		}
		if (userBean.getTelphone() != null
				&& !"".equals(userBean.getTelphone())) {
			udTelphoneEt.setText(userBean.getTelphone());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_detail_cancle_btn:
			Intent intent = ViewUtil.getIntent(UserDetailActivity.this,
					UserMainActivity.class);
			UserDetailActivity.this.startActivity(intent);
			UserDetailActivity.this.finish();
			break;
		case R.id.user_detail_confirm_btn:
			updateUser();
			break;
		}

	}

	/**
	 * 更改数据
	 */
	private void updateUser() {

		boolean isUpdate = false;
		ContentValues values = new ContentValues();

		String nickName = udNickNameEt.getText().toString().trim();
		if (!nickName.equals(userBean.getNickName())) {
			isUpdate = true;
			values.put("nickName", nickName);
		}

		int old = "".equals(udOldEt.getText().toString().trim()) ? 0 : Integer
				.valueOf(udOldEt.getText().toString().trim());
		if (old != userBean.getOld()) {
			isUpdate = true;
			values.put("old", old);
		}

		String telphone = udTelphoneEt.getText().toString().trim();
		if (!telphone.equals(userBean.getTelphone())) {
			isUpdate = true;
			values.put("telphone", telphone);
		}

		String email = udEmailEt.getText().toString().trim();
		if (!email.equals(userBean.getEmail())) {
			isUpdate = true;
			values.put("email", email);
		}

		int sex = udSexSp.getSelectedItemPosition();
		if (sex != userBean.getSex()) {
			isUpdate = true;
			values.put("sex", sex);
		}

		if (!isUpdate) {
			ToastUtil.toast(this, "无任何修改！");
			return;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id=", String.valueOf(userBean.getId()));

		DBOperator.getInstance().update(DBBean.TB_USER_DB, values, params);

		userBean.setNickName(nickName);
		userBean.setOld(old);
		userBean.setEmail(email);
		userBean.setTelphone(telphone);
		userBean.setSex(sex);

		UserInfoManager.getInstance().updateUser(userBean);

		Intent intent = ViewUtil.getIntent(UserDetailActivity.this,
				UserMainActivity.class);
		UserDetailActivity.this.startActivity(intent);
		UserDetailActivity.this.finish();
	}
}
