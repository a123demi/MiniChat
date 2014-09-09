package com.lming.minichat;

import android.app.Application;

public class MainApplication extends Application {
	private static MainApplication instance;
	private boolean isExit = true;
	
	public static MainApplication getInstance(){
		return instance;
	}
	
	public void onCreate(){
		super.onCreate();
		instance = this;
	}
	
	public void onTerminate(){
		super.onTerminate();
		setExit(true);
	}

	/**
	 * @return the isExit
	 */
	public boolean isExit() {
		return isExit;
	}

	/**
	 * @param isExit the isExit to set
	 */
	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}
	
	
}
