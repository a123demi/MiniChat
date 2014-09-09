package com.lming.minichat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lming.minichat.adapter.GroupManagerAdapter;
import com.lming.minichat.adapter.GroupManagerAdapter.PopWinDelListener;
import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.bean.GroupMemberBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.CharacterParserUtil;
import com.lming.minichat.util.PinyinComparatorUtil;
import com.lming.minichat.util.StaticParamsUtil;
import com.lming.minichat.util.ToastUtil;
import com.lming.minichat.util.ViewUtil;

public class GroupManagerActivity extends BaseActivity implements
		OnClickListener, PopWinDelListener {

	private TextView mAddGroupManagerTv;
	private Button mFinishGroupManagerBtn;
	private ListView mGroupManagerLv;
	private List<GroupMemberBean> mGroupList = new ArrayList<GroupMemberBean>();
	private PinyinComparatorUtil pinyinComparatorUtil;
	private CharacterParserUtil characterParserUtil;
	private GroupManagerAdapter groupManagerAdapter;

	private PopupWindow groupDelPopWin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_manager_activity);

		pinyinComparatorUtil = new PinyinComparatorUtil();
		characterParserUtil = CharacterParserUtil.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
		findById();
		initData();
	}

	@Override
	public void findById() {
		mAddGroupManagerTv = (TextView) this
				.findViewById(R.id.group_manager_add_tv);
		mFinishGroupManagerBtn = (Button) this
				.findViewById(R.id.group_manager_finish_btn);
		mGroupManagerLv = (ListView) this.findViewById(R.id.group_manager_lv);

		mAddGroupManagerTv.setOnClickListener(this);
		mFinishGroupManagerBtn.setOnClickListener(this);

		groupManagerAdapter = new GroupManagerAdapter(this, mGroupList, this);
		mGroupManagerLv.setAdapter(groupManagerAdapter);
		
		mGroupManagerLv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = ViewUtil.getIntent(GroupManagerActivity.this, GroupManagerAddActivity.class);
				intent.putExtra("AddFlag", false);
				intent.putExtra("GroupName", mGroupList.get(position).getDepartName());
				GroupManagerActivity.this.startActivity(intent);
			}
		});
	}

	private void initData() {
		mGroupList.clear();
		List<GroupBean> groupBeanList = UserInfoManager.getInstance().getGroupBeans();
		if (groupBeanList.size() == 0) {
			return;
		}
		/**
		 * 获取组信息
		 */
		mGroupList = filledGroupData(groupBeanList);
		Collections.sort(mGroupList, pinyinComparatorUtil);// 部门排序
		/**
		 * 登陆用户组排在前列
		 */
		sortFirstSelfGroupData();

		groupManagerAdapter.updateView(mGroupList);
	}

	/**
	 * 登陆组排在首位
	 */
	private void sortFirstSelfGroupData() {
		GroupMemberBean selfMemberBean = null;
		for (GroupMemberBean bean : mGroupList) {
			if (bean.getDepartName().equals(
					UserInfoManager.getInstance().getmGroupName())) {
				selfMemberBean = bean;
				break;
			}
		}

		if (selfMemberBean != null) {
			mGroupList.remove(selfMemberBean);
			mGroupList.add(0, selfMemberBean);
		}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.group_manager_add_tv:
			intent = ViewUtil.getIntent(GroupManagerActivity.this,
					GroupManagerAddActivity.class);
			intent.putExtra("AddFlag", true);
			GroupManagerActivity.this.startActivity(intent);
			break;
		case R.id.group_manager_finish_btn:
			intent = ViewUtil.getIntent(GroupManagerActivity.this,
					UserMainActivity.class);
			GroupManagerActivity.this.startActivity(intent);
			GroupManagerActivity.this.finish();
			break;
		}
	}

	@Override
	public void groupDelVisiable(View view, int position) {
		getGroupManagerPopwin(position);
		/**
		 * view的位置，left 和 top
		 */
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		groupDelPopWin
				.showAtLocation(
						view,
						Gravity.NO_GRAVITY,
						(ViewUtil.getScreenSize(this))[0] - 130,
						location[1] - groupDelPopWin.getHeight() / 2
								+ view.getHeight() / 2);
	}

	private void getGroupManagerPopwin(int position) {
		if (groupDelPopWin != null) {
			groupDelPopWin.dismiss();
			return;
		}

		initGroupManagerPopwin(position);
	}

	private void initGroupManagerPopwin(final int position) {
		LayoutInflater mLayoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View myView = mLayoutInflater.inflate(
				R.layout.pop_group_delete_btn_activity, null);
		Button popGroupDelBtn = (Button) myView
				.findViewById(R.id.pop_group_delete_btn);
		// mDeptExchangePopWin = new PopupWindow(viewGroup, 700, 930, true);
		groupDelPopWin = new PopupWindow(myView, 120, 50, true);
		groupDelPopWin.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ico_null));

		popGroupDelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// goto GroupManagerActivity
				GroupMemberBean groupMemberBean = mGroupList.get(position);

				/**
				 * 删除指定行
				 */

				if (groupMemberBean.getDepartName().equals(
						StaticParamsUtil.DEFALUT_GROUP_NAME)) {
					ToastUtil.toast(GroupManagerActivity.this, "我的好友为默认组，无法删除");

				} else {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("groupName=", groupMemberBean.getDepartName());
					
					List<Object> groupObjList = DBOperator.getInstance().queryBeanList(DBBean.TB_GROUP_DB, params);
					int id = ((GroupBean)groupObjList.get(0)).getId();
					DBOperator.getInstance().del(DBBean.TB_GROUP_DB, params);
					
					params.clear();
					params.put("groupUserId=", String.valueOf(id));
					List<Object> userObjList = DBOperator.getInstance()
							.queryBeanList(DBBean.TB_USER_DB, params);

					if (userObjList.size() > 0) {
						for (Object obj : userObjList) {
							UserBean userBean = (UserBean) obj;
							params.clear();
							params.put("id=", String.valueOf(userBean.getId()));

							ContentValues values = new ContentValues();
							values.put("groupUserId",
									UserInfoManager.getInstance().getmGroupUserId());
							DBOperator.getInstance().update(DBBean.TB_USER_DB,
									values, params);

						}

						mGroupList.remove(position);
						groupManagerAdapter.updateView(mGroupList);
						UserInfoManager.getInstance().removeGroup(id);
					}

				}
				groupDelPopWin.dismiss();
			}
		});

		groupDelPopWin.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					groupDelPopWin.dismiss();
					return true;
				}
				return false;
			}
		});
	}

}
