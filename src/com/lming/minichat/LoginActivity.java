package com.lming.minichat;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.ToastUtil;

public class LoginActivity extends Activity implements OnClickListener,OnCheckedChangeListener {
	private Button loginBtn,registerBtn;
	private EditText userNameEt,passwordEt;
	private CheckBox savePasswordCb,autoLoginCb;
	private ImageView historyUserIv;
	private UserInfoManager userInfoManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		MainApplication.getInstance().addActivity(this);
		userInfoManager = UserInfoManager.getInstance();
		findById();
	}
	
	private void findById(){
		loginBtn = (Button)this.findViewById(R.id.login_btn);
		registerBtn = (Button)this.findViewById(R.id.register_btn);
		userNameEt = (EditText)this.findViewById(R.id.login_user_name_et);
		passwordEt = (EditText)this.findViewById(R.id.login_user_password_et);
		savePasswordCb = (CheckBox)this.findViewById(R.id.password_save_cb);
		autoLoginCb = (CheckBox)this.findViewById(R.id.auto_login_cb);
		historyUserIv = (ImageView)this.findViewById(R.id.login_select_user_iv);
		
		loginBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		historyUserIv.setOnClickListener(this);
		
		savePasswordCb.setOnCheckedChangeListener(this);
		autoLoginCb.setOnCheckedChangeListener(this);
		
	}
	
	private void initData(){
		SharedPreferences shared = this.getSharedPreferences("user_info", 0);
		boolean isPasswordSave = shared.getBoolean("password_save_sp", false);
		boolean isAutoLogin = shared.getBoolean("auto_login_sp", false);
		String loginName= shared.getString("loginName", "");
		String password= shared.getString("password", "");
		if(isAutoLogin){
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("loginName=", loginName);
			params.put("password=",password);
			//数据库校验
			if(!validateUser(params)){
				ToastUtil.toast(LoginActivity.this, "用户名或密码错误!");
				return;
			}
			BaseActivity.startActivity(LoginActivity.this,UserMainActivity.class,-1);
		}
		
		if(isPasswordSave){
			userNameEt.setText(loginName);
			passwordEt.setText(password);
			savePasswordCb.setChecked(true);
		}
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initData();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		MainApplication.getInstance().removeActivity(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.login_btn:
			login();
			break;
		case R.id.register_btn:
			BaseActivity.startActivity(LoginActivity.this,RegisterActivity.class,-1);
			break;
		case R.id.login_select_user_iv:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences shared = this.getSharedPreferences("user_info", 0);
		switch(buttonView.getId()){
			case R.id.password_save_cb:
				shared.edit().putBoolean("password_save_sp",isChecked).commit();
				break;
			case R.id.auto_login_cb:
				shared.edit().putBoolean("auto_login_sp",isChecked).commit();
				break;
		}
	}
	

	
	/**
	 * 数据库校验用户名和密码
	 * @param loginName
	 * @param password
	 * @return
	 */
	private boolean validateUser(HashMap<String,String> params){
		
		List<Object> userList = DBOperator.getInstance().queryBeanList(DBBean.TB_USER_DB, params);
		
		if(userList.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 登陆
	 */
	private void login(){
		String userName = userNameEt.getText().toString().trim();
		String password = passwordEt.getText().toString().trim();
		if("".equals(userName)){
			ToastUtil.toast(LoginActivity.this, "用户名不能为空!");
			return;
		}
		
		if("".equals(password)){
			ToastUtil.toast(LoginActivity.this, "密码不能为空!");
			return;
		}
		
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("loginName=", userName);
		params.put("password=",password);
		
		
		//数据库校验
		if(!validateUser(params)){
			ToastUtil.toast(LoginActivity.this, "用户名或密码错误!");
			return;
		}
		
		UserInfoManager.getInstance().setmSelfLoginName(userName);
		UserInfoManager.getInstance().setmSelfPassword(password);
		//保存用户名和密码到到sharedpreferences
		SharedPreferences shared = this.getSharedPreferences("user_info", 0);
		shared.edit().putString("user_name", userNameEt.getText().toString().trim());
		shared.edit().putString("user_password", passwordEt.getText().toString().trim());
		
		params.clear();
		params.put("ORDERBY", "groupDate desc");
		
		List<Object>  groupObjList= DBOperator.getInstance().queryBeanList(DBBean.TB_GROUP_DB, params);
		
		for(Object groupObj:groupObjList){
			GroupBean groupBean = (GroupBean) groupObj;
			userInfoManager.addToGroup(groupBean);
		}
		
		params.clear();
		params.put("ORDERBY", "registerDate desc");
		List<Object>  objList= DBOperator.getInstance().queryBeanList(DBBean.TB_USER_DB, params);
		for(Object obj:objList){
			UserBean userBean = (UserBean) obj;
			userInfoManager.addToFriend(userBean);
		}
		
		MainApplication.getInstance().setExit(false);
		BaseActivity.startActivity(LoginActivity.this,UserMainActivity.class,-1);
		this.finish();
	}
}
