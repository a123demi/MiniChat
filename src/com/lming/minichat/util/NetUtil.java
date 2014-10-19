package com.lming.minichat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	
	/**
	 * 判断是否wifi连接
	 * @param mContext
	 * @return
	 */
	public static boolean isWifiConnected(Context mContext){
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否在手机网络
	 * @param mContext
	 * @return
	 */
	public static boolean isMobileConnected(Context mContext){
		ConnectivityManager manager =(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if(networkInfo != null && networkInfo.isConnected()){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 判断网络是否连接
	 * @param mContext
	 * @return
	 */
	public static boolean isNetConnected(Context mContext){
		boolean isWifi = isWifiConnected(mContext);
		boolean isMobile = isMobileConnected(mContext);
		
		if(isWifi || isMobile){
			return true;
		}
		return false;
	}
}
