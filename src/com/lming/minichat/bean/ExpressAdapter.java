package com.lming.minichat.bean;

import java.util.ArrayList;
import java.util.List;

import com.lming.minichat.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExpressAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<ExpressBean> expressList = new ArrayList<ExpressBean>();
	
	public ExpressAdapter(Context mContext,List<ExpressBean> expressList){
		this.mContext = mContext;
		this.expressList = expressList;
		this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return expressList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return expressList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView holderView = null;
		if(convertView == null){
			holderView = new HolderView();
			convertView = mInflater.inflate(R.layout.express_info_list_view, null);
			holderView.timeTv = (TextView)convertView.findViewById(R.id.express_lv_time_tv);
			holderView.contentTv = (TextView)convertView.findViewById(R.id.express_lv_content_tv);
			
			convertView.setTag(holderView);
		}else{
			holderView =(HolderView) convertView.getTag();
		}
		ExpressBean expressBean = expressList.get(position);
		holderView.timeTv.setText(expressBean.getTime());
		holderView.contentTv.setText(expressBean.getContext());
		return convertView;
	}
	
	public class HolderView{
		public TextView timeTv;
		private TextView contentTv;
	}

}
