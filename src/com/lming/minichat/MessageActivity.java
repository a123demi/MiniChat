package com.lming.minichat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lming.minichat.MessageAddFragment.AddImageListener;
import com.lming.minichat.MessageEmotionsFragment.EditTextListener;
import com.lming.minichat.adapter.MessageAdapter;
import com.lming.minichat.adapter.MessageAdapter.DeleteMsgContentListener;
import com.lming.minichat.bean.ChatEmoBean;
import com.lming.minichat.bean.MessageBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.customview.CustomEmotionsEditText;
import com.lming.minichat.customview.CustomEmotionsEditText.EmotionsClickListener;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.FaceConversionUtil;

public class MessageActivity extends BaseActivity implements OnClickListener,
		EmotionsClickListener,DeleteMsgContentListener,EditTextListener,AddImageListener {

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
		msgAdapter = new MessageAdapter(this, msgBeanList,this);
		msgLv.setAdapter(msgAdapter);
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

		msgTalkSendBtn.setOnClickListener(this);
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
		if(msgBeanList.size() % 2 == 0){
			msgBean.setIsSend(0);
		}else{
			msgBean.setIsSend(1);
		}
		msgBean.setMsgContent(msgContentEt.getText().toString().trim());
		msgBean.setMsgDate(Calendar.getInstance().getTimeInMillis());
		msgBean.setMsgToLoginName(loginName);
		DBOperator.getInstance().insert(DBBean.TB_MESSAGE_DB, msgBean);
		msgContentEt.setText("");
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
				if(mainApplication.isTalk()){
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
			if(mainApplication.isShowAdd()){
				popMsgBottomLl.setVisibility(View.GONE);
				mainApplication.setShowAdd(false);
			}
			

			break;
		case R.id.message_send_talk_btn:
			break;
		case R.id.message_send_text_btn:
			addMsgToDb();
			msgBeanList = getMsgBeanList(userBean);
			msgAdapter.updateView(msgBeanList);
			if (msgBeanList.size() == 0) {
				msgLv.setSelection(msgBeanList.size());
			} else {
				msgLv.setSelection(msgBeanList.size() - 1);
			}
			break;

		}
	}

	/**
	 * 底部对讲界面与文字界面切换
	 * 
	 * @param isTalk
	 */
	private void toggleRelative(boolean isTalk) {
		if (isTalk) {//对讲，发送变加
			msgTalkRl.setVisibility(View.VISIBLE);
			msgTextRl.setVisibility(View.GONE);
			msgContentEt.setFocusable(false);
			
			//对讲中,发送隐藏
			toggleAddOrSend(false);
		} else {//文字
			msgTalkRl.setVisibility(View.GONE);
			msgTextRl.setVisibility(View.VISIBLE);
			msgContentEt.setFocusable(true);
			
			//文字中个，如有发送，显示发送，否则隐藏
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
	@Override
	public void deleteMsgContent(int position) {
		MessageBean msgBean = msgBeanList.get(position);
		msgBeanList.remove(position);
		HashMap<String,String> params = new HashMap<String,String>();
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
//			if (mListener != null)
//				mListener.onCorpusSelected(emoji);
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.addFace(this, emoji.getId(), emoji.getCharacter());
			msgContentEt.append(spannableString);
		}
	}

	/**
	 * 处理图片添加
	 */
	@Override
	public void processAddImage(String url,int imageType) {
		Log.d(TAG, "processAddImage()->add image url:" + url);
		MessageBean msgBean = new MessageBean();
		msgBean.setMsgType(imageType);
		if(msgBeanList.size() % 2 == 0){
			msgBean.setIsSend(0);
		}else{
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
}
