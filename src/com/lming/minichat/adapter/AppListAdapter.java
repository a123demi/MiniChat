package com.lming.minichat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lming.minichat.R;
import com.lming.minichat.bean.ApplicationListBean;

public class AppListAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<ApplicationListBean> mAppList;
	
	public AppListAdapter(Context mContext,List<ApplicationListBean> mAppList){
		this.mContext = mContext;
		this.mAppList = mAppList;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView;
		if(convertView == null){
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.application_list_grid_view, null);
			holderView.appListGridViewTv = (TextView) convertView.findViewById(R.id.app_list_grid_view_tv);
			
			convertView.setTag(holderView);
		}else{
			holderView = (HolderView)convertView.getTag();
		}
		
		holderView.appListGridViewTv.setText(mAppList.get(position).getAppName());
		
		Drawable drawable= mContext.getResources().getDrawable(mAppList.get(position).getAppImgId());
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		holderView.appListGridViewTv.setCompoundDrawables(null,drawable,null,null);
		
		return convertView;
	}
	
	public class HolderView{
		public TextView appListGridViewTv;
	}

}
