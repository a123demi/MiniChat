package com.lming.minichat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	protected void findById(){
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	/**
	 * activity跳转
	 * @param cls
	 */
	public static void startActivity(Context context,Class<?> cls){
		Intent intent = new Intent();
		intent.setClass(context, cls);
		context.startActivity(intent);
	}
}
