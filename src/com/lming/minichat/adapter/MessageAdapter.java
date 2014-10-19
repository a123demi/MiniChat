package com.lming.minichat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lming.minichat.R;
import com.lming.minichat.bean.MessageBean;
import com.lming.minichat.util.FaceConversionUtil;
import com.lming.minichat.util.FileUtils;

public class MessageAdapter extends BaseAdapter {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private List<MessageBean> msgBeanList;
	private LayoutInflater mInflater;
	private Context mContext;

	public MessageAdapter(Context mContext, List<MessageBean> msgBeanList) {
		this.msgBeanList = msgBeanList;
		this.mContext = mContext;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void updateView(List<MessageBean> msgBeanList) {
		this.msgBeanList = msgBeanList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return msgBeanList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgBeanList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		MessageBean entity = msgBeanList.get(position);
		boolean isSend = entity.getIsSend() == 0 ? true : false;
		if (isSend) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		HolderView holderView;
		MessageBean msgBean = msgBeanList.get(position);
		boolean isSend = msgBean.getIsSend() == 0 ? true : false;
		int msgType = msgBean.getMsgType();
		if (convertView == null) {
			holderView = new HolderView();
			if (isSend) {
				convertView = mInflater.inflate(
						R.layout.message_list_view_right, parent, false);
			} else {
				convertView = mInflater.inflate(
						R.layout.message_list_view_left, parent, false);
			}
			holderView.msgContentTv = (TextView) convertView
					.findViewById(R.id.msg_lv_content_tv);
			holderView.msgImageIv = (ImageView) convertView
					.findViewById(R.id.msg_lv_image_iv);
			holderView.msgContentLl = (LinearLayout) convertView
					.findViewById(R.id.msg_lv_content_ll);

			holderView.msgVoiceTv = (TextView) convertView
					.findViewById(R.id.msg_lv_voice_tv);
			holderView.msgVoiceIv = (ImageView) convertView
					.findViewById(R.id.msg_lv_voice_iv);
			holderView.msgVoiceLl = (LinearLayout) convertView
					.findViewById(R.id.msg_lv_voice_ll);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}

		/**
		 * 将[]字符转化为表情
		 */
		if (0 == msgType) {// 文字
			holderView.msgVoiceLl.setVisibility(View.GONE);
			holderView.msgContentLl.setVisibility(View.VISIBLE);
			holderView.msgContentTv.setVisibility(View.VISIBLE);
			holderView.msgImageIv.setVisibility(View.GONE);
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.getExpressionString(mContext, msgBean.getMsgContent());
			holderView.msgContentTv.setText(spannableString);
		} else if (1 == msgType) {// 本地图片
			holderView.msgVoiceLl.setVisibility(View.GONE);
			holderView.msgContentLl.setVisibility(View.VISIBLE);
			holderView.msgContentTv.setVisibility(View.GONE);
			holderView.msgImageIv.setVisibility(View.VISIBLE);
			Bitmap bitmap = FileUtils.getBitmap(msgBean.getMsgImageUrl());

			holderView.msgImageIv.setImageBitmap(bitmap);
		} else if (2 == msgType) {// 拍照图片
			holderView.msgVoiceLl.setVisibility(View.GONE);
			holderView.msgContentLl.setVisibility(View.VISIBLE);
			holderView.msgContentTv.setVisibility(View.GONE);
			holderView.msgImageIv.setVisibility(View.VISIBLE);
			Bitmap bitmap = FileUtils.getBitmap(msgBean.getMsgImageUrl());
			/**
			 * 由于拍照是横屏拍照，需要顺时针旋转90正常显示
			 */
			holderView.msgImageIv.setImageBitmap(FileUtils
					.createRotateBitmap(bitmap));
		} else {// 语音
			holderView.msgVoiceLl.setVisibility(View.VISIBLE);
			holderView.msgContentLl.setVisibility(View.GONE);

			holderView.msgVoiceIv
					.setImageResource(R.drawable.msg_talk_voice_bg);
			holderView.msgVoiceTv.setText("");

		}
		return convertView;
	}

	public class HolderView {
		public TextView msgContentTv;
		public ImageView msgImageIv;
		public LinearLayout msgContentLl;
		public LinearLayout msgVoiceLl;
		public TextView msgVoiceTv;
		public ImageView msgVoiceIv;

	}

}
