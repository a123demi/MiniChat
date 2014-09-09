package com.lming.minichat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.StaticParamsUtil;
import com.lming.minichat.util.ToastUtil;
import com.lming.minichat.util.ValidateUtil;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = " RegisterActivity ";
	private EditText rgtUserNameEt, rgtPasswordEt, rgtRepasswordEt, rgtEmailEt,
			rgtSecurityCodeEt;
	private Button rgtRegisterBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		findById();
	}

	@Override
	protected void findById() {
		rgtUserNameEt = (EditText) this
				.findViewById(R.id.register_user_name_et);
		rgtPasswordEt = (EditText) this.findViewById(R.id.register_password_et);
		rgtRepasswordEt = (EditText) this
				.findViewById(R.id.register_confirm_password_et);
		rgtEmailEt = (EditText) this.findViewById(R.id.register_email_et);
		rgtSecurityCodeEt = (EditText) this
				.findViewById(R.id.register_security_code_et);
		rgtRegisterBtn = (Button) this.findViewById(R.id.register_btn);

		rgtRegisterBtn.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register_btn:
			register();
			break;
		}
	}

	private void register() {
		List<String> rgtInfoList = new ArrayList<String>();

		String loginName = rgtUserNameEt.getText().toString().trim();
		String password = rgtPasswordEt.getText().toString().trim();
		String repassword = rgtRepasswordEt.getText().toString().trim();
		String email = rgtEmailEt.getText().toString().trim();
		String code = rgtSecurityCodeEt.getText().toString().trim();

		if (ValidateUtil.isNullOrEmpty(loginName, "用户名不能为空")
				|| ValidateUtil.isNullOrEmpty(password, "密码不能为空")
				|| ValidateUtil.isNullOrEmpty(repassword, "密码不能为空")
				|| ValidateUtil.isNullOrEmpty(email, "邮箱不能为空")
				|| ValidateUtil.isNullOrEmpty(code, "验证码不能为空")) {
			return;
		}

		rgtInfoList.add(loginName);
		rgtInfoList.add(password);
		rgtInfoList.add(repassword);
		rgtInfoList.add(email);
		rgtInfoList.add(code);

		if (!validateView(rgtInfoList)) {
			return;
		}
		
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("ORDERBY", "registerDate desc");
		List<Object>  userObjList= DBOperator.getInstance().queryBeanList(DBBean.TB_USER_DB, params);
		for(Object obj:userObjList){
			UserBean bean = (UserBean)obj;
			if(bean.getLoginName().equals(loginName)){
				ToastUtil.toast(this, R.string.login_name_exist);
				return;
			}
		}
		
		UserInfoManager.getInstance().setmSelfLoginName(loginName);
		UserInfoManager.getInstance().setmSelfPassword(password);

		params.clear();
		params.put("groupId=", StaticParamsUtil.DEFALUT_GROUP_ID);
		List<Object> groupObjList = DBOperator.getInstance().queryBeanList(DBBean.TB_GROUP_DB, params);
		if(groupObjList.size() == 0){
			GroupBean groupBean = new GroupBean(StaticParamsUtil.DEFALUT_GROUP_NAME,StaticParamsUtil.DEFALUT_GROUP_ID);
			groupBean.setGroupDate(Calendar.getInstance().getTimeInMillis());
			DBOperator.getInstance().insert(DBBean.TB_GROUP_DB, groupBean);
		}
		groupObjList = DBOperator.getInstance().queryBeanList(DBBean.TB_GROUP_DB, params);
		Log.i(TAG, "register():group size->" + groupObjList.size());
		
		UserBean userBean = new UserBean();
		userBean.setLoginName(loginName);
		userBean.setPassword(password);
		userBean.setEmail(email);
		userBean.setRegisterDate(Calendar.getInstance().getTimeInMillis());
		userBean.setGroupUserId(((GroupBean)groupObjList.get(0)).getId());

		DBOperator.getInstance().insert(DBBean.TB_USER_DB, userBean);

		// 保存用户名和密码到到sharedpreferences
		SharedPreferences shared = this.getSharedPreferences("user_info", 0);
		shared.edit().putString("user_name", loginName).commit();
		shared.edit().putString("user_password", password).commit();
		
		params.clear();
		params.put("ORDERBY", "id desc");
		groupObjList = DBOperator.getInstance().queryBeanList(DBBean.TB_GROUP_DB, params);
		for(Object groupObj:groupObjList){
			GroupBean groupBean = (GroupBean) groupObj;
			UserInfoManager.getInstance().addToGroup(groupBean);
		}
		
		params.clear();
		params.put("ORDERBY", "registerDate desc");
		userObjList= DBOperator.getInstance().queryBeanList(DBBean.TB_USER_DB, params);
		for(Object obj:userObjList){
			UserBean bean = (UserBean) obj;
			UserInfoManager.getInstance().addToFriend(bean);
		}

		MainApplication.getInstance().setExit(false);
		startActivity(this, UserMainActivity.class);
		this.finish();
	}

	private boolean validateView(List<String> rgtInfoList) {
		if (!rgtInfoList.get(1).equals(rgtInfoList.get(2))) {
			ToastUtil.toast(this, "密码不一致");
			return false;
		}
		// 用户名校验
		return true;

	}
}
