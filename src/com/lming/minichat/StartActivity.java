package com.lming.minichat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.lming.minichat.util.FaceConversionUtil;
import com.lming.minichat.util.ViewUtil;

public class StartActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		 // 设置无标题窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start_activity);
        new Thread(new Runnable() {
			@Override
			public void run() {
				FaceConversionUtil.getInstace().getFileText(StartActivity.this);
			}

		}).start();
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
				Intent intent = ViewUtil.getIntent(StartActivity.this, LoginActivity.class);
				StartActivity.this.startActivity(intent);
				StartActivity.this.finish();
			}
			
		}, 3000);
	}
}
