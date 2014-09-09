package com.lming.minichat.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lming.minichat.R;
import com.lming.minichat.bean.GroupMemberBean;

public class GroupManagerAdapter extends BaseAdapter {
	private List<GroupMemberBean> mGroupList;
	private Context mContext;
	private LayoutInflater mInflater;
	private PopWinDelListener mPopWinDelListener;

	public GroupManagerAdapter(Context mContext,
			List<GroupMemberBean> mGroupList,PopWinDelListener mPopWinDelListener) {
		this.mContext = mContext;
		this.mGroupList = mGroupList;
		this.mPopWinDelListener = mPopWinDelListener;
		this.mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void updateView(List<GroupMemberBean> mGroupList) {
		this.mGroupList = mGroupList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mGroupList.size();
	}

	@Override
	public Object getItem(int position) {
		return mGroupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final HolderView holderView;
		if (convertView == null) {
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.group_manager_list_view,
					null);
			holderView.deleteGroupIv = (ImageView) convertView
					.findViewById(R.id.group_manager_list_view_delete_iv);
			holderView.groupNameTv = (TextView) convertView
					.findViewById(R.id.group_manager_list_view_name_tv);
			convertView.setTag(holderView);
		} else {
			holderView = (HolderView) convertView.getTag();
		}
		GroupMemberBean memberBean = mGroupList.get(position);
		holderView.groupNameTv.setText(memberBean.getDepartName());
		holderView.deleteGroupIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopWinDelListener.groupDelVisiable(v, position);
			}
		});

		return convertView;
	}

	public class HolderView {
		public ImageView deleteGroupIv;
		public TextView groupNameTv;
	}
	
	public interface PopWinDelListener{
		public void groupDelVisiable(View view,int position);
	}

}
