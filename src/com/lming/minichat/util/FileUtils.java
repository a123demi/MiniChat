package com.lming.minichat.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * 
 ******************************************
 * @author 廖乃波
 * @文件名称	:  FileUtils.java
 * @创建时间	: 2013-1-27 下午02:35:09
 * @文件描述	: 文件工具类
 ******************************************
 */
public class FileUtils {
	/**
	 * 读取表情配置文件
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("emoji");// 读取assert文件
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据文件路径返回bitmap
	 * @param url
	 * @return
	 */
	public static Bitmap getBitmap(String url){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(url);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return BitmapFactory.decodeStream(fis);
	}
	
	/**
	 * bitmap旋转90度
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createRotateBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			try {
				m.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);// 90就是我们需要选择的90度
				Bitmap bmp2 = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), m, true);
				bitmap.recycle();
				bitmap = bmp2;
			} catch (Exception ex) {
				System.out.print("创建图片失败！" + ex);
			}
		}
		return bitmap;
	}
}
