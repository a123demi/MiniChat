package com.lming.minichat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lming.minichat.bean.ExpressAdapter;
import com.lming.minichat.bean.ExpressBean;
import com.lming.minichat.util.ToastUtil;

public class ExpressInfoActivity extends BaseActivity {

	private ListView expressInfoLv;
	private TextView expressErrTv;
	private List<ExpressBean> expressList = new ArrayList<ExpressBean>();
	private ExpressAdapter expressAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.express_info_activity);

		findById();
		initData();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void findById() {
		expressInfoLv = (ListView) this.findViewById(R.id.express_info_lv);
		expressErrTv = (TextView) this.findViewById(R.id.express_err_tv);
		expressInfoLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = this.getIntent();
		String expressData = intent.getStringExtra("expressData");
		if (!processExpressData(expressData)) {
			expressErrTv.setVisibility(View.VISIBLE);
			expressErrTv.setText("返回数据有误");
			return;
		} else {
			expressErrTv.setVisibility(View.GONE);
		}

		expressAdapter = new ExpressAdapter(this, expressList);

		expressInfoLv.setAdapter(expressAdapter);
	}

	/**
	 * 处理快递返回的json数据
	 * 
	 * @param expressData
	 * @return
	 */
	private boolean processExpressData(String expressData) {
		boolean isNormal = true;
		if ("".equals(expressData)) {
			isNormal = false;
		}
		try {
			JSONArray expressInfoArray = new JSONArray(expressData);
			ExpressBean expressBean = null;
			JSONObject expressInfoObj = null;
			for (int i = 0; i < expressInfoArray.length(); i++) {
				expressBean = new ExpressBean();
				expressInfoObj = expressInfoArray.getJSONObject(i);
				expressBean.setTime(expressInfoObj.getString("time"));
				expressBean.setContext(expressInfoObj.getString("context"));
				expressList.add(expressBean);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			isNormal = false;
			ToastUtil.toast(this, e.getMessage());
		}

		return isNormal;
	}

}
