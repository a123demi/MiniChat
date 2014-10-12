package com.lming.minichat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lming.minichat.adapter.FaceAdapter;
import com.lming.minichat.bean.ChatEmoBean;
import com.lming.minichat.util.FaceConversionUtil;

public class MessageEmotionsFragment extends Fragment implements
		OnPageChangeListener,OnItemClickListener {
	private LayoutInflater mInflater;
	/**
	 * msg_emotion_pup
	 */
	private ViewPager emoViewPager;
	private LinearLayout emoTabLl;
	private List<View> emotionViewList = new ArrayList<View>();
	private ImageView[] imageViews;
	private ImageView imageView;
	private List<FaceAdapter> faceAdapters;
	
	private List<List<ChatEmoBean>> emojis;
	
	private int current = 0;

	private EditTextListener editTextListener;
	public interface EditTextListener{
		public void processEditText(ChatEmoBean emoji);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			editTextListener = (EditTextListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View emotionsView = inflater.inflate(
				R.layout.message_emotions_fragment, container, false);
		emoViewPager = (ViewPager)emotionsView.findViewById(R.id.pop_msg_emotions_vp);
		emoTabLl = (LinearLayout) emotionsView.findViewById(R.id.pop_msg_tab_ll);
		
		return emotionsView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		emojis = FaceConversionUtil.getInstace().emojiLists;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		initEmotionRelative();
	}

	private void initEmotionRelative() {
		current = 0;
		faceAdapters = new ArrayList<FaceAdapter>();
		for(int i=0; i<emojis.size(); i++){
//			View oneView = mInflater.inflate(R.layout.message_emotions_item_one,
//					null);
//			LinearLayout emotionLl = (LinearLayout) oneView.findViewById(R.id.emotions_ll);
			GridView view = new GridView(MessageEmotionsFragment.this.getActivity());
			FaceAdapter adapter = new FaceAdapter(MessageEmotionsFragment.this.getActivity(), emojis.get(i));
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			emotionViewList.add(view);
		}


		imageViews = new ImageView[emotionViewList.size()];

		for (int i = 0; i < emotionViewList.size(); i++) {
			imageView = new ImageView(this.getActivity());
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			imageViews[i] = imageView;

			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}

			emoTabLl.addView(imageViews[i]);
		}
		emoViewPager.setAdapter(new EmotionPopAdapter());
		emoViewPager.setOnPageChangeListener(this);

	}

	class EmotionPopAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return emotionViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(emotionViewList.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(emotionViewList.get(arg1));
			return emotionViewList.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		current = arg0;
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[arg0]
					.setBackgroundResource(R.drawable.page_indicator_focused);

			if (arg0 != i) {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ChatEmoBean emoji = (ChatEmoBean) faceAdapters.get(current).getItem(position);
		editTextListener.processEditText(emoji);
	}
}
