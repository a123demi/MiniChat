package com.lming.minichat;

import android.os.Bundle;
/**
 * 分发界面(进入登陆界面或主界面)
 * @author Administrator
 *
 */
public class IndexActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(MainApplication.getInstance().isExit()){
			startActivity(this,LoginActivity.class);
		}else{
			startActivity(this,UserMainActivity.class);
		}
		this.finish();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
}
