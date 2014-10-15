package com.lming.minichat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addActivity(this);
	}
	
	protected void findById(){
		
	}
	
	public void addActivity(Activity activity){
		MainApplication.getInstance().addActivity(activity);
	}
	
	public void removeActivity(Activity activity){
		MainApplication.getInstance().removeActivity(activity);
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	/**
	 * activity跳转
	 * @param cls
	 */
	public static void startActivity(Context context,Class<?> cls,int flags){
		Intent intent = new Intent();
		intent.setClass(context, cls);
		if(flags != -1)
			intent.setFlags(flags);
		context.startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		/**
		 * 退出应用
		 * 1.设置为退出的标示
		 * 2.退出所有的activity
		 * 3.退出进程
		 */
		case R.id.exit_menu:
			MainApplication.getInstance().setExit(true);
			MainApplication.getInstance().exitActivity();
			
			System.exit(0);
			break;
		case R.id.about_menu:
			startActivity(BaseActivity.this,AboutActivity.class,Intent.FLAG_ACTIVITY_NO_ANIMATION);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		removeActivity(this);
	}
}
