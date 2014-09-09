package com.lming.minichat.util;

import com.lming.minichat.MainApplication;

import android.content.Context;

public class ValidateUtil {
	private static Context mContext;
	static{
		mContext = MainApplication.getInstance().getApplicationContext();
	}
	/**
	 * 判断是否为空，并toast
	 * @param str
	 * @param msg
	 * @return
	 */
	public static boolean isNullOrEmpty(String str,String msg){
		if(str == null || "".equals(str)){
			ToastUtil.toast(mContext, msg);
			return true;
		}
		return false;
	}
	
}
