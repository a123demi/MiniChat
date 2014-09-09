package com.lming.minichat.util;

import com.lming.minichat.MainApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
	
	private static Context mContext = MainApplication.getInstance();
	
	public static SharedPreferences getSharedPreference(String sharedName){
		return mContext.getSharedPreferences(sharedName, 0);
	}
}
