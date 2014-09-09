package com.lming.minichat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lming.minichat.adapter.MainUserAdapter;
import com.lming.minichat.adapter.MainUserAdapter.MainUserListener;
import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.bean.GroupMemberBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.customview.CustomClearEditText;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.CharacterParserUtil;
import com.lming.minichat.util.PinyinComparatorUtil;
import com.lming.minichat.util.ViewUtil;

public class UserMainActivity extends BaseActivity implements MainUserListener{
	private CustomClearEditText mMainUserSearchCcet;
	private ExpandableListView mMainUsersElv;
	private TextView mMainUserNoneTipTv;
	private PinyinComparatorUtil pinyinComparatorUtil;
	private CharacterParserUtil characterParserUtil;
	
	private PopupWindow mGroupPopWin;

	private MainUserAdapter mainUserAdapter;
	private List<GroupMemberBean> groupMemberList = new ArrayList<GroupMemberBean>();
	private List<List<GroupMemberBean>> childMemberList = new ArrayList<List<GroupMemberBean>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_main_activity);
		pinyinComparatorUtil = new PinyinComparatorUtil();
		characterParserUtil = CharacterParserUtil.getInstance();
		findById();

		mainUserAdapter = new MainUserAdapter(this, groupMemberList,
				childMemberList,this);
		mMainUsersElv.setAdapter(mainUserAdapter);
	}

	@Override
	protected void findById() {
		mMainUserSearchCcet = (CustomClearEditText) this
				.findViewById(R.id.main_user_search_custom_et);
		mMainUsersElv = (ExpandableListView) this
				.findViewById(R.id.main_user_elv);
		mMainUserNoneTipTv = (TextView) this
				.findViewById(R.id.main_user_none_tip_tv);

		/**
		 * 搜索框文字变化
		 */
		mMainUserSearchCcet.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filledSearchData(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void initData() {
		groupMemberList.clear();
		childMemberList.clear();
		List<GroupBean> groupBeanList = UserInfoManager.getInstance().getGroupBeans();
		if (groupBeanList.size() == 0) {
			mMainUserNoneTipTv.setVisibility(View.VISIBLE);
			return;
		} else {
			mMainUserNoneTipTv.setVisibility(View.GONE);
		}
		/**
		 * 获取组信息
		 */
		groupMemberList = filledGroupData(groupBeanList);
		Collections.sort(groupMemberList, pinyinComparatorUtil);// 部门排序
		/**
		 * 登陆用户组排在前列
		 */
		sortFirstSelfGroupData();
		/**
		 * 获取组成员信息
		 */
		childMemberList = filledChildData();

		/**
		 * 更新界面
		 */
		mainUserAdapter.updateView(groupMemberList, childMemberList);
		/**
		 * 登陆组展开
		 */
		mMainUsersElv.expandGroup(0);

	}

	/**
	 * 
	 * @param mGroupList
	 * @return
	 */
	private List<GroupMemberBean> filledGroupData(List<GroupBean> mGroupList) {
		List<GroupMemberBean> sortGroup = new ArrayList<GroupMemberBean>();
		for (GroupBean group : mGroupList) {
			GroupMemberBean groupMemberBean = new GroupMemberBean();
			groupMemberBean.setDepartName(group.getGroupName());

			// 汉字转换成拼音
			String pinyin = characterParserUtil.getSelling(group.getGroupName());
			groupMemberBean = pinyinComparatorUtil.getGroupMemberBeanBySort(
					groupMemberBean, pinyin);
			sortGroup.add(groupMemberBean);
		}
		return sortGroup;
	}

	/**
	 * 登陆组排在首位
	 */
	private void sortFirstSelfGroupData() {
		GroupMemberBean selfMemberBean = null;
		for (GroupMemberBean bean : groupMemberList) {
			if (bean.getDepartName().equals(
					UserInfoManager.getInstance().getmGroupName())) {
				selfMemberBean = bean;
				break;
			}
		}

		if (selfMemberBean != null) {
			groupMemberList.remove(selfMemberBean);
			groupMemberList.add(0, selfMemberBean);
		}

	}

	private List<List<GroupMemberBean>> filledChildData() {
		List<List<GroupMemberBean>> tempChildList = new ArrayList<List<GroupMemberBean>>();
		for (GroupMemberBean memberBean : groupMemberList) {
			List<GroupMemberBean> tempList = new ArrayList<GroupMemberBean>();
			List<UserBean> userBeanList = UserInfoManager.getInstance()
					.getUserBeanByGroupName(memberBean.getDepartName());
			for (UserBean bean : userBeanList) {
				GroupMemberBean groupBean = new GroupMemberBean();
				groupBean.setUserInfoBean(bean);
				String pinyin;
				if (bean.getNickName() == null || "".equals(bean.getNickName())) {
					pinyin = characterParserUtil
							.getSelling(bean.getLoginName());
				} else {
					pinyin = characterParserUtil.getSelling(bean.getNickName());
				}
				groupBean = pinyinComparatorUtil.getGroupMemberBeanBySort(
						groupBean, pinyin);
				tempList.add(groupBean);

			}

			Collections.sort(tempList, pinyinComparatorUtil);
			tempChildList.add(tempList);
		}
		return tempChildList;
	}

	/**
	 * 根据查询条件查询
	 * 
	 * @param searchStr
	 */
	private void filledSearchData(String searchStr) {
		List<GroupMemberBean> groupFilterList = new ArrayList<GroupMemberBean>();
		List<List<GroupMemberBean>> childFilterList = new ArrayList<List<GroupMemberBean>>();

		if (TextUtils.isEmpty(searchStr)) {
			groupFilterList = groupMemberList;
			childFilterList = childMemberList;

		} else {
			for (int i = 0; i < groupMemberList.size(); i++) {
				String groupName = groupMemberList.get(i).getDepartName();
				String groupPinyin = groupMemberList.get(i).getSortPinYin();
				/**
				 * 判断groupName是否已经加入到groupFilterList中
				 */
				boolean isAddGroup = false;
				if (groupName.indexOf(searchStr.toLowerCase()) != -1
						|| groupName.indexOf(searchStr.toUpperCase()) != -1
						|| groupPinyin.startsWith(searchStr.toUpperCase())) {
					groupFilterList.add(groupMemberList.get(i));
					isAddGroup = true;
				}

				List<GroupMemberBean> tempList = new ArrayList<GroupMemberBean>();
				for (int j = 0; j < childMemberList.get(i).size(); j++) {
					GroupMemberBean groupMemberBean = childMemberList.get(i)
							.get(j);
					String userNickName = groupMemberBean.getUserBean()
							.getNickName();
					String userLoginName = groupMemberBean.getUserBean()
							.getLoginName();
					String userName;
					if (userNickName == null || "".equals(userNickName)) {
						userName = userLoginName;
					} else {
						userName = userNickName;
					}

					String userNamePinyin = groupMemberBean.getSortPinYin();

					if (userName.indexOf(searchStr.toLowerCase()) != -1
							|| userName.indexOf(searchStr.toUpperCase()) != -1
							|| userNamePinyin.startsWith(searchStr
									.toUpperCase())) {
						tempList.add(groupMemberBean);
						if (!isAddGroup) {
							groupFilterList.add(groupMemberList.get(i));
							isAddGroup = true;
						}
					}
				}
				if(tempList.size() > 0){
					childFilterList.add(tempList);
				}
			}
		}

		mainUserAdapter.updateView(groupFilterList, childFilterList);
		for (int i = 0; i < groupFilterList.size(); i++) {
			mMainUsersElv.expandGroup(i);
		}

		if (groupFilterList.size() > 0) {
			mMainUserNoneTipTv.setVisibility(View.GONE);
		} else {
			mMainUserNoneTipTv.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	/**
	 * 展开和关闭groupList
	 */
	@Override
	public void expandedGroup(boolean isExpanded, int groupPosition) {
		if(isExpanded){
			mMainUsersElv.collapseGroup(groupPosition);
		}else{
			mMainUsersElv.expandGroup(groupPosition);
		}
	}

	@Override
	public void popGroupManager(View parent,int groupPosition) {
		getGroupExchangePopWin(groupPosition);
		/**
		 * view的位置，left 和 top
		 */
		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		mGroupPopWin.showAtLocation(parent, Gravity.NO_GRAVITY, (ViewUtil.getScreenSize(this)[0]-mGroupPopWin.getWidth())/2, location[1]-mGroupPopWin.getHeight());
	}
	
	/***
	 * 获取PopupWindow实例
	 */
	private void getGroupExchangePopWin(int groupPosition) {
		if (null != mGroupPopWin) {
			mGroupPopWin.dismiss();
			return;
		}
		initGroupExchangePopWin(groupPosition);
	}

	/**
	 * 创建PopupWindow
	 */
	protected void initGroupExchangePopWin(int groupPosition) {
		LayoutInflater mLayoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View myView = mLayoutInflater.inflate(R.layout.pop_group_activity, null);
		TextView popGroupTv = (TextView) myView.findViewById(R.id.pop_group_tv);
		// mDeptExchangePopWin = new PopupWindow(viewGroup, 700, 930, true);
		mGroupPopWin = new PopupWindow(myView, 300, 200, true);
		mGroupPopWin.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.pop_group_bg));
		
		popGroupTv.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//goto GroupManagerActivity
				UserMainActivity.this.startActivity(new Intent().setClass(UserMainActivity.this, GroupManagerActivity.class));
				mGroupPopWin.dismiss();
			}
		});
		
		mGroupPopWin.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mGroupPopWin.dismiss();
					return true;
				}
				return false;
			}
		});
	}

}
