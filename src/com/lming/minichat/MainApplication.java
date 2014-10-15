package com.lming.minichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.lming.minichat.service.MainService;

public class MainApplication extends Application {
	private static MainApplication instance;
	private boolean isExit = true;
	private boolean isTalk = false;//message发送界面，底部显示判断是对讲界面还是文字界面
	private boolean isSend = false;//message文字发送界面，底部显示判断是增加界面还是发送界面
	private boolean isShowEmotion = false;//message是否显示emotions
	private boolean isShowAdd = false;
	private int msgBottomPosition = 0; //设置message底部显示emotions还是add
	
	public static MainApplication getInstance(){
		return instance;
	}
	
	private List<Activity> activityList = new ArrayList<Activity>();
	
	public void onCreate(){
		super.onCreate();
		instance = this;
		SDKInitializer.initialize(this);
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

	/**
	 * @return the isTalk
	 */
	public boolean isTalk() {
		return isTalk;
	}

	/**
	 * @param isTalk the isTalk to set
	 */
	public void setTalk(boolean isTalk) {
		this.isTalk = isTalk;
	}

	/**
	 * @return the isSend
	 */
	public boolean isSend() {
		return isSend;
	}

	/**
	 * @param isSend the isSend to set
	 */
	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	/**
	 * @return the isShowEmotion
	 */
	public boolean isShowEmotion() {
		return isShowEmotion;
	}
	
	

	/**
	 * @return the isShowAdd
	 */
	public boolean isShowAdd() {
		return isShowAdd;
	}

	/**
	 * @param isShowAdd the isShowAdd to set
	 */
	public void setShowAdd(boolean isShowAdd) {
		this.isShowAdd = isShowAdd;
	}

	/**
	 * @param isShowEmotion the isShowEmotion to set
	 */
	public void setShowEmotion(boolean isShowEmotion) {
		this.isShowEmotion = isShowEmotion;
	}

	/**
	 * @return the msgBottomPosition
	 */
	public int getMsgBottomPosition() {
		return msgBottomPosition;
	}

	/**
	 * @param msgBottomPosition the msgBottomPosition to set
	 */
	public void setMsgBottomPosition(int msgBottomPosition) {
		this.msgBottomPosition = msgBottomPosition;
	}
	
	private double x;
	private double y;
	private float radiu;
	public float getRadiu() {
		return radiu;
	}

	public void setRadiu(float radiu) {
		this.radiu = radiu;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	
	public void addActivity(Activity activity){
		if(!activityList.contains(activity))
			activityList.add(activity);
	}
	
	public void removeActivity(Activity activity){
		if(activityList.contains(activity))
			activityList.remove(activity);
	}
	
	/*
	 * 退出activity
	 */
	public void exitActivity(){
		
		if(MainService.isReady()){
			MainService.getInstance().stopService();
		}
		
		for(Activity activity:activityList){
			activity.finish();
		}
	}
	
}
