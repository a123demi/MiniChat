package com.lming.minichat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

public class ViewUtil {
	public static int[] getScreenSize(Activity activity){
		int[] screenSize = new int[2];
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        screenSize[0] = width;
        screenSize[1] = height;
        return screenSize;
	}
	
	public static Intent getIntent(Context mContext,Class<?> clz){
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.setClass(mContext, clz);
		return intent;
	}
}
