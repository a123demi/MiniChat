package com.lming.minichat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lming.minichat.IndexActivity;
import com.lming.minichat.R;
import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.user.UserInfoManager;

public class MainService extends Service {

//	private static final String TAG = MainService.class.getSimpleName();
	private static final int NOTIF_ID = 1;
	private static MainService instance;
	private Notification mNotif;
	private NotificationManager mNm;
	private PendingIntent mPendingIntent;
	private UserBean mSelfUserBean;
	private GroupBean mSelfGroupBean;
	

	public static MainService getInstance() {
		if (instance == null) {
			instance = new MainService();
		}

		return instance;
	}

	/**
	 * 判断instance是否可用
	 * @return
	 */
	public static boolean isReady() {
		return instance == null ? false : true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		instance = this;
		mNm = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		mSelfUserBean = UserInfoManager.getInstance().getmSelfUserBean();
		mSelfGroupBean = UserInfoManager.getInstance().getmSelfGroupBean();
		buildNotification();
	}
	
	@Override
	public int onStartCommand(Intent intent ,int flags,int startId){
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy(){
		
		super.onDestroy();
	}
	
	/**
	 * 停止服务
	 */
	public void stopService(){
		instance.stopSelf();
		mNm.cancel(NOTIF_ID);
		instance = null;
	}
	
	/**
	 * 生成通知提示
	 */
	private void buildNotification(){
		String name = null;
		if(mSelfUserBean.getNickName() != null && !"".equals(mSelfUserBean.getNickName())){
			name = mSelfUserBean.getNickName();
		}else{
			name = mSelfUserBean.getLoginName();
		}
		String group = mSelfGroupBean.getGroupName();
		//定义需要跳转进入的activity
		Intent notifIntent = new Intent(this,IndexActivity.class);
		mPendingIntent = PendingIntent.getActivity(this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mNotif = new Notification.Builder(this)
		.setContentTitle("迷你聊")
		.setContentText(group + "  " +name+"  登陆")
		.setSmallIcon(R.drawable.mi_ni_chat_logo)
		.setTicker(name+"  登陆")
		.setContentIntent(mPendingIntent)
		.build();
		
		mNm.notify(NOTIF_ID, mNotif);
	}

}
