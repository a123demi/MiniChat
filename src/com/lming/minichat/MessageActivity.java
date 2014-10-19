package com.lming.minichat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lming.minichat.MessageAddFragment.AddImageListener;
import com.lming.minichat.MessageEmotionsFragment.EditTextListener;
import com.lming.minichat.adapter.MessageAdapter;
import com.lming.minichat.bean.ChatEmoBean;
import com.lming.minichat.bean.MessageBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.customview.CustomEmotionsEditText;
import com.lming.minichat.customview.CustomEmotionsEditText.EmotionsClickListener;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.FaceConversionUtil;
import com.lming.minichat.util.FileUtils;
import com.lming.minichat.util.ToastUtil;

public class MessageActivity extends BaseActivity implements OnClickListener,
		EmotionsClickListener, EditTextListener, AddImageListener,
		OnTouchListener {

	private static final String TAG = MessageActivity.class.getSimpleName();
	private RelativeLayout msgTextRl, msgTalkRl;
	private ImageView msgVoiceIv, msgAddIv, msgKeyBoardIv;
	private Button msgTalkSendBtn, msgTextSendBtn;
	private CustomEmotionsEditText msgContentEt;
	private ListView msgLv;
	private LinearLayout popMsgBottomLl;

	private List<MessageBean> msgBeanList = new ArrayList<MessageBean>();
	private MessageAdapter msgAdapter;

	private MainApplication mainApplication;
	private String loginName;
	private UserBean userBean;

	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;
	private String rootPath;
	private String storagePath;

	private Handler mHandler = new Handler();
	private boolean isPlayer = false;
	private int count = 1;
	private int prePosition = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_activity);
		mainApplication = MainApplication.getInstance();
		loginName = this.getIntent().getStringExtra("loginName");
		userBean = UserInfoManager.getInstance().getUserBeanByLoginName(
				loginName);

		findById();

		msgBeanList = getMsgBeanList(userBean);
		msgAdapter = new MessageAdapter(this, msgBeanList);
		msgLv.setAdapter(msgAdapter);

		msgLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MessageBean msgBean = msgBeanList.get(position);
				if (1 == msgBean.getMsgType() || 2 == msgBean.getMsgType()) {
					Intent intent = new Intent();
					intent.putExtra("imageUrl", msgBean.getMsgImageUrl());
					intent.setClass(MessageActivity.this,
							MessagePreviewActivity.class);
					MessageActivity.this.startActivity(intent);
				} else if (3 == msgBean.getMsgType()) {
					LinearLayout childLl = (LinearLayout) ((RelativeLayout) view)
							.getChildAt(2);
					ImageView msgVoiceIv;
					TextView msgVoiceTv;
					boolean isSend = msgBean.getIsSend() == 0 ? true : false;
					if (!isSend) {
						msgVoiceIv = (ImageView) childLl.getChildAt(0);
						msgVoiceTv = (TextView) childLl.getChildAt(1);
					} else {
						msgVoiceTv = (TextView) childLl.getChildAt(0);
						msgVoiceIv = (ImageView) ((LinearLayout) childLl
								.getChildAt(1)).getChildAt(0);
					}

					if (prePosition == -1 || prePosition != position) {
						isPlayer = false;
						if (mediaPlayer != null) {
							if(mediaPlayer.isPlaying()){
								mediaPlayer.reset();
							}
						}
					}
					prePosition = position;

					if (!isPlayer) {
						String voicePath = msgBean.getMsgVoiceUrl();
						if (voicePath == null || "".equals(voicePath)) {
							ToastUtil.toast(MessageActivity.this,
									"录音不存在，确认后在试听");
							return;
						}
						isPlayer = true;
						startPlayer(voicePath, msgVoiceIv, msgVoiceTv);
					} else {
						msgVoiceTv.setText("");
						msgVoiceIv
								.setImageResource(R.drawable.msg_talk_voice_bg);
						stopPlayer();
						isPlayer = false;
					}
				}
			}

		});

		msgLv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				deleteMsgContent(position);// 长按删除

				return true;
			}

		});
		if (msgBeanList.size() == 0) {
			msgLv.setSelection(msgBeanList.size());
		} else {
			msgLv.setSelection(msgBeanList.size() - 1);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	@Override
	public void findById() {
		msgTextRl = (RelativeLayout) this
				.findViewById(R.id.message_bottom_text_rl);
		msgTalkRl = (RelativeLayout) this
				.findViewById(R.id.message_bottom_talk_rl);

		msgTalkSendBtn = (Button) this.findViewById(R.id.message_send_talk_btn);
		msgTextSendBtn = (Button) this.findViewById(R.id.message_send_text_btn);

		msgContentEt = (CustomEmotionsEditText) this
				.findViewById(R.id.message_content_et);

		msgVoiceIv = (ImageView) this.findViewById(R.id.message_voice_iv);
		msgAddIv = (ImageView) this.findViewById(R.id.message_add_iv);
		msgKeyBoardIv = (ImageView) this.findViewById(R.id.message_keyboard_iv);

		popMsgBottomLl = (LinearLayout) this
				.findViewById(R.id.message_emotions_bottom_ll);

		msgTalkSendBtn.setOnTouchListener(this);
		msgTextSendBtn.setOnClickListener(this);

		msgVoiceIv.setOnClickListener(this);
		msgAddIv.setOnClickListener(this);
		msgKeyBoardIv.setOnClickListener(this);

		msgLv = (ListView) this.findViewById(R.id.message_info_lv);
		msgContentEt.setEmotionsClickListener(this);
		msgContentEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					mainApplication.setSend(false);
					toggleAddOrSend(false);
				} else {
					mainApplication.setSend(true);
					toggleAddOrSend(true);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void initData() {
		toggleRelative(mainApplication.isTalk());
		toggleAddOrSend(mainApplication.isSend());
		if (mainApplication.isShowEmotion()) {
			popMsgBottomLl.setVisibility(View.VISIBLE);
			redirectFragment(mainApplication.getMsgBottomPosition());
		} else {
			popMsgBottomLl.setVisibility(View.GONE);
		}
		msgAdapter.updateView(msgBeanList);

		rootPath = FileUtils.getRootPathByDir(Environment.DIRECTORY_MUSIC,
				"miniTalkVoiceS");
	}

	/**
	 * 数据库查询获取Message消息
	 * 
	 * @param bean
	 * @return
	 */
	private List<MessageBean> getMsgBeanList(UserBean bean) {
		List<MessageBean> msgList = new ArrayList<MessageBean>();

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("msgToLoginName=", bean.getLoginName());

		List<Object> objList = DBOperator.getInstance().queryBeanList(
				DBBean.TB_MESSAGE_DB, params);

		for (Object obj : objList) {
			msgList.add((MessageBean) obj);
		}

		return msgList;
	}

	/**
	 * 发送按钮，增加message to Db
	 */
	private void addMsgToDb() {
		MessageBean msgBean = new MessageBean();
		msgBean.setMsgType(0);
		if (msgBeanList.size() % 2 == 0) {
			msgBean.setIsSend(0);
		} else {
			msgBean.setIsSend(1);
		}
		msgBean.setMsgContent(msgContentEt.getText().toString().trim());
		msgBean.setMsgDate(Calendar.getInstance().getTimeInMillis());
		msgBean.setMsgToLoginName(loginName);
		DBOperator.getInstance().insert(DBBean.TB_MESSAGE_DB, msgBean);
		msgContentEt.setText("");

		msgBeanList = getMsgBeanList(userBean);
		msgAdapter.updateView(msgBeanList);
		if (msgBeanList.size() == 0) {
			msgLv.setSelection(msgBeanList.size());
		} else {
			msgLv.setSelection(msgBeanList.size() - 1);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/**
		 * finish()重新为初始化状态
		 */
		mainApplication.setShowAdd(false);
		mainApplication.setSend(false);
		mainApplication.setTalk(false);
		mainApplication.setShowEmotion(false);

		// 释放mediaRecorder
		stopRecorder();
		stopPlayer();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.message_add_iv:
			mainApplication.setShowAdd(!mainApplication.isShowAdd());
			// msgContentEt.setFocusable(false);
			// msgContentEt.setFocusableInTouchMode(false);
			if (mainApplication.isShowAdd()) {
				popMsgBottomLl.setVisibility(View.VISIBLE);
				mainApplication.setMsgBottomPosition(1);
				redirectFragment(mainApplication.getMsgBottomPosition());
				if (mainApplication.isTalk()) {
					toggleRelative(false);
					mainApplication.setTalk(false);
				}
				msgContentEt.setFocusable(false);

			} else {
				popMsgBottomLl.setVisibility(View.GONE);
				msgContentEt.setFocusable(true);
			}

			break;
		case R.id.message_keyboard_iv:
			mainApplication.setTalk(false);
			this.toggleRelative(false);// 显示文字界面
			break;
		case R.id.message_voice_iv:
			mainApplication.setTalk(true);
			this.toggleRelative(true);// 显示对讲界面
			if (mainApplication.isShowAdd()) {
				popMsgBottomLl.setVisibility(View.GONE);
				mainApplication.setShowAdd(false);
			}

			break;
		case R.id.message_send_text_btn:
			addMsgToDb();

			break;

		}
	}

	/**
	 * 底部对讲界面与文字界面切换
	 * 
	 * @param isTalk
	 */
	private void toggleRelative(boolean isTalk) {
		if (isTalk) {// 对讲，发送变加
			msgTalkRl.setVisibility(View.VISIBLE);
			msgTextRl.setVisibility(View.GONE);
			msgContentEt.setFocusable(false);

			// 对讲中,发送隐藏
			toggleAddOrSend(false);
		} else {// 文字
			msgTalkRl.setVisibility(View.GONE);
			msgTextRl.setVisibility(View.VISIBLE);
			msgContentEt.setFocusable(true);

			// 文字中个，如有发送，显示发送，否则隐藏
			toggleAddOrSend(mainApplication.isSend());
		}

	}

	/**
	 * 底部文字界面切换Add和send
	 * 
	 * @param isSend
	 */
	private void toggleAddOrSend(boolean isSend) {

		if (isSend) {
			msgTextSendBtn.setVisibility(View.VISIBLE);
			msgAddIv.setVisibility(View.GONE);

		} else {
			msgTextSendBtn.setVisibility(View.GONE);
			msgAddIv.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void emotionsClick(View v) {
		mainApplication.setShowEmotion(!mainApplication.isShowEmotion());
		if (mainApplication.isShowEmotion()) {
			popMsgBottomLl.setVisibility(View.VISIBLE);
			mainApplication.setMsgBottomPosition(0);
			redirectFragment(mainApplication.getMsgBottomPosition());
			// msgContentEt.setFocusable(false);
		} else {
			popMsgBottomLl.setVisibility(View.GONE);
			// msgContentEt.setFocusable(true);

		}
		// msgContentEt.setFocusableInTouchMode(false);
	}

	/**
	 * fragment切换跳转0：emotions,1:add
	 * 
	 * @param position
	 */
	private void redirectFragment(int position) {
		Fragment fragment = null;
		switch (position) {
		case 1:
			fragment = new MessageAddFragment();
			mainApplication.setShowEmotion(false);
			break;
		case 0:
			fragment = new MessageEmotionsFragment();
			mainApplication.setShowAdd(false);
			break;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.message_emotions_bottom_ll, fragment);
		// ft.addToBackStack(null);

		// 处理锁屏异常："Can not perform this action after onSaveInstanceState"
		// Use commitAllowingStateLoss() only as a last resort.
		ft.commitAllowingStateLoss();
	}

	/**
	 * 长按删除消息
	 */
	private void deleteMsgContent(int position) {
		MessageBean msgBean = msgBeanList.get(position);
		msgBeanList.remove(position);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("msgId=", String.valueOf(msgBean.getMsgId()));
		DBOperator.getInstance().del(DBBean.TB_MESSAGE_DB, params);

		msgAdapter.updateView(msgBeanList);
	}

	/**
	 * 获取表情选择
	 */
	@Override
	public void processEditText(ChatEmoBean emoji) {
		if (emoji.getId() == R.drawable.face_del_ico_dafeult) {
			int selection = msgContentEt.getSelectionStart();
			String text = msgContentEt.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					msgContentEt.getText().delete(start, end);
					return;
				}
				msgContentEt.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.getCharacter())) {
			// if (mListener != null)
			// mListener.onCorpusSelected(emoji);
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.addFace(this, emoji.getId(), emoji.getCharacter());
			msgContentEt.append(spannableString);
		}
	}

	/**
	 * 处理图片添加
	 */
	@Override
	public void processAddImage(String url, int imageType) {
		Log.d(TAG, "processAddImage()->add image url:" + url);
		MessageBean msgBean = new MessageBean();
		msgBean.setMsgType(imageType);
		if (msgBeanList.size() % 2 == 0) {
			msgBean.setIsSend(0);
		} else {
			msgBean.setIsSend(1);
		}
		msgBean.setMsgImageUrl(url);
		msgBean.setMsgDate(Calendar.getInstance().getTimeInMillis());
		msgBean.setMsgToLoginName(loginName);
		DBOperator.getInstance().insert(DBBean.TB_MESSAGE_DB, msgBean);
		msgBeanList = getMsgBeanList(userBean);
		msgAdapter.updateView(msgBeanList);
		if (msgBeanList.size() == 0) {
			msgLv.setSelection(msgBeanList.size());
		} else {
			msgLv.setSelection(msgBeanList.size() - 1);
		}
	}

	/**
	 * *************************************** 处理录音
	 */

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.message_send_talk_btn:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {// 按下,开始录音
				msgTalkSendBtn.setText("松开 结束");
				startRecorder();

			} else if (event.getAction() == MotionEvent.ACTION_UP) {// 释放,停止录音，保存，并释放

				stopRecorder();
				if (storagePath != null) {
					File voiceFile = new File(storagePath);
					if (voiceFile.exists()) {
						addTalkVoiceToDb(storagePath);
					} else {
						ToastUtil.toast(MessageActivity.this, "录音不存在!");
					}
				}
				msgTalkSendBtn.setText("按下 说话");
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {// 移动，

			}

			return true;
		}
		return true;
	}

	/**
	 * 开始录音
	 */
	@SuppressLint("SimpleDateFormat")
	private void startRecorder() {
		if (mediaRecorder == null) {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		}
		storagePath = rootPath + File.separator + "rec_"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".3pg";
		mediaRecorder.setOutputFile(storagePath);

		try {
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (IllegalStateException e) {
			Log.e(TAG, "startRecorder()->"
					+ "mediaRecorder IllegalStateException:" + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG,
					"startRecorder()->" + "mediaRecorder IOException:"
							+ e.getMessage());
		}
	}

	/**
	 * 停止录音
	 */

	private void stopRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}

	/**
	 * 将talkVoicePath加入到数据库
	 * 
	 * @param talkVoicePath
	 */
	private void addTalkVoiceToDb(String talkVoicePath) {
		MessageBean msgBean = new MessageBean();
		msgBean.setMsgType(3);

		if (msgBeanList.size() % 2 == 0) {
			msgBean.setIsSend(0);
		} else {
			msgBean.setIsSend(1);
		}
		msgBean.setMsgVoiceUrl(talkVoicePath);
		msgBean.setMsgDate(Calendar.getInstance().getTimeInMillis());
		msgBean.setMsgToLoginName(loginName);

		DBOperator.getInstance().insert(DBBean.TB_MESSAGE_DB, msgBean);
		msgBeanList = getMsgBeanList(userBean);
		msgAdapter.updateView(msgBeanList);
		if (msgBeanList.size() == 0) {
			msgLv.setSelection(msgBeanList.size());
		} else {
			msgLv.setSelection(msgBeanList.size() - 1);
		}
	}

	/**
	 * ******************************************* 处理语音播放
	 */

	/**
	 * 开始播放
	 */
	private void startPlayer(String voicePath, ImageView voiceIv,
			TextView voiceTimeTv) {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}

		// 判断是否播放完成
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				isPlayer = false;
				prePosition = -1;
			}
		});
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(voicePath);
			mediaPlayer.prepare();
			mediaPlayer.start();
			voiceIv.setImageResource(R.drawable.msg_talk_voice_playing1);
			voiceTimeTv.setText(FileUtils.getTimeByMillSecond(mediaPlayer
					.getDuration()));
			mHandler.post(new MyRunnable(voiceIv));
		} catch (IllegalArgumentException e) {
			ToastUtil.toast(
					this,
					"startPlayer()-> mPlayer IllegalArgumentException:"
							+ e.getMessage());
		} catch (SecurityException e) {
			ToastUtil.toast(this, "startPlayer()-> mPlayer SecurityException:"
					+ e.getMessage());
		} catch (IllegalStateException e) {
			ToastUtil.toast(
					this,
					"startPlayer()-> mPlayer IllegalStateException:"
							+ e.getMessage());
		} catch (IOException e) {
			ToastUtil.toast(this,
					"startPlayer()-> mPlayer IOException:" + e.getMessage());
		}

	}

	class MyRunnable implements Runnable {
		private ImageView imageView;

		public MyRunnable(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void run() {
			if (mediaPlayer != null && isPlayer) {
				count++;
				setVoiceImage(count, imageView);
				mHandler.postDelayed(new MyRunnable(imageView), 1000);
			} else {
				count = 1;
				imageView.setImageResource(R.drawable.msg_talk_voice_bg);
			}
		}

	}

	/**
	 * 设置语音图片变化
	 * 
	 * @param count
	 * @param voiceIv
	 */
	private void setVoiceImage(int count, ImageView voiceIv) {
		int myCount = count % 3;
		switch (myCount) {
		case 0:
			voiceIv.setImageResource(R.drawable.msg_talk_voice_playing1);
			break;
		case 1:
			voiceIv.setImageResource(R.drawable.msg_talk_voice_playing2);
			break;
		case 2:
			voiceIv.setImageResource(R.drawable.msg_talk_voice_playing3);
			break;
		}
	}

	/**
	 * 停止播放
	 */
	private void stopPlayer() {
		if (mediaPlayer != null) {
			prePosition = -1;
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}
