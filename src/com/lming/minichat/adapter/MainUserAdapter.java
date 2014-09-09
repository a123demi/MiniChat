package com.lming.minichat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lming.minichat.ApplicationListActivity;
import com.lming.minichat.R;
import com.lming.minichat.bean.GroupMemberBean;
import com.lming.minichat.bean.UserBean;
import com.lming.minichat.util.ViewUtil;

public class MainUserAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<GroupMemberBean> mGroupList = new ArrayList<GroupMemberBean>();
	private List<List<GroupMemberBean>> mChildList = new ArrayList<List<GroupMemberBean>>();
	private MainUserListener mMainUserListener;
	
	public MainUserAdapter(Context mContext,List<GroupMemberBean> mGroupList,List<List<GroupMemberBean>> mChildList,MainUserListener mMainUserListener){
		this.mContext = mContext;
		this.mGroupList = mGroupList;
		this.mChildList = mChildList;
		this.mMainUserListener = mMainUserListener;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * 刷新用户信息
	 * @param mGroupList
	 * @param mChildList
	 */
	public void updateView(List<GroupMemberBean> mGroupList,List<List<GroupMemberBean>> mChildList){
		this.mGroupList = mGroupList;
		this.mChildList = mChildList;
		this.notifyDataSetChanged();
	}
	@Override
	public int getGroupCount() {
		return mGroupList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mChildList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mChildList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupView groupView;
		if(convertView == null){
			groupView = new GroupView();
			convertView = mInflater.inflate(R.layout.user_main_group_list_view, null);
			groupView.groupMainGroupNameTv= (TextView) convertView.findViewById(R.id.group_main_group_name);
			groupView.groupMainUserLl= (LinearLayout) convertView.findViewById(R.id.group_man_group_ll);
			convertView.setTag(groupView);
		}else{
			groupView = (GroupView)convertView.getTag();
		}
		
		groupView.groupMainGroupNameTv.setText(mGroupList.get(groupPosition).getDepartName());
		
		groupView.groupMainUserLl.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mMainUserListener.expandedGroup(isExpanded, groupPosition);
			}
		});
		
		groupView.groupMainUserLl.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				mMainUserListener.popGroupManager(v,groupPosition);
				return true;
			}
		});
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildView childView;
		if(convertView == null){
			childView = new ChildView();
			convertView = mInflater.inflate(R.layout.user_main_child_list_view, null);
			childView.childMainUserNameTv= (TextView) convertView.findViewById(R.id.child_main_user_name);
			childView.childMainUserLl= (LinearLayout) convertView.findViewById(R.id.child_main_user_ll);
			convertView.setTag(childView);
		}else{
			childView = (ChildView)convertView.getTag();
		}
		final UserBean userBean = mChildList.get(groupPosition).get(childPosition).getUserBean();
		if(userBean.getNickName() == null || "".equals(userBean.getNickName())){
			childView.childMainUserNameTv.setText(userBean.getLoginName());
		}else{
			childView.childMainUserNameTv.setText(userBean.getNickName());
		}
		
		childView.childMainUserLl.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = ViewUtil.getIntent(mContext, ApplicationListActivity.class);
				intent.putExtra("LoginName", userBean.getLoginName());
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	public class GroupView{
		public TextView groupMainGroupNameTv;
		public LinearLayout groupMainUserLl;
	}
	
	public class ChildView{
		public TextView childMainUserNameTv;
		public LinearLayout childMainUserLl;
	}
	
	public interface MainUserListener{
		public void expandedGroup(boolean isExpanded,int groupPosition);
		public void popGroupManager(View parent,int groupPosition);
	}

}
