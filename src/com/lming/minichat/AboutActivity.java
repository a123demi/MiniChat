package com.lming.minichat;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about_activity);
		
		TextView aboutVersion = (TextView) this.findViewById(R.id.about_version_tv);
		aboutVersion.setText(aboutVersion.getText().toString() + getVersion());
		MainApplication.getInstance().addActivity(this);
	}
	
	/**
	 * 获取版本号
	 * @return
	 */
	private String getVersion(){
		PackageManager pm = this.getPackageManager();
		PackageInfo pi;
		String version = "";
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
			version = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			version = e.getMessage().toString();
		}
		return version;
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		MainApplication.getInstance().removeActivity(this);
	}
}
