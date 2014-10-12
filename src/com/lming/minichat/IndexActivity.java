package com.lming.minichat;

import android.os.Bundle;
import android.view.Window;
/**
 * 分发界面(进入登陆界面或主界面)
 * @author Administrator
 *
 */
public class IndexActivity extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 // 设置无标题窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(MainApplication.getInstance().isExit()){
			startActivity(this,StartActivity.class,-1);
		}else{
			startActivity(this,UserMainActivity.class,-1);
		}
		this.finish();
	}
	
}
