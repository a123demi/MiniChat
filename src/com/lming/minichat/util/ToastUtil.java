package com.lming.minichat.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void toast(Context mContext, String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context mContext, int msgId) {
		Toast.makeText(mContext, mContext.getResources().getString(msgId),
				Toast.LENGTH_SHORT).show();
	}
}
