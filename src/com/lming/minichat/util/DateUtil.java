package com.lming.minichat.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class DateUtil {
	/**
	 * 获取格式化日期字符串
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateFormatString(Date date) {
		if (date == null)
			date = new Date();
		String formatStr = new String();
		SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		formatStr = matter.format(date);
		return formatStr;
	}
}
