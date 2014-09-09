package com.lming.minichat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.db.DBBean;
import com.lming.minichat.db.DBOperator;
import com.lming.minichat.user.UserInfoManager;
import com.lming.minichat.util.CharacterParserUtil;
import com.lming.minichat.util.ToastUtil;

public class GroupManagerAddActivity extends BaseActivity implements
		OnClickListener {

	private EditText groupAddNameEt;
	private Button groupAddCancleBtn, groupAddConfirmBtn;

	private CharacterParserUtil characterParserUtil;

	private boolean isAddFlag = true;
	private String groupName = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_manager_add_activity);
		characterParserUtil = CharacterParserUtil.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();

		findById();
		isAddFlag = this.getIntent().getBooleanExtra("AddFlag", true);
		if (isAddFlag) {
			groupAddNameEt.setText("");
		} else {
			groupName = this.getIntent().getStringExtra("GroupName");
			groupAddNameEt.setText(groupName);
		}
	}

	@Override
	public void findById() {
		groupAddNameEt = (EditText) this.findViewById(R.id.group_add_input_et);
		groupAddCancleBtn = (Button) this
				.findViewById(R.id.group_add_cancle_btn);
		groupAddConfirmBtn = (Button) this
				.findViewById(R.id.group_add_confirm_btn);

		groupAddCancleBtn.setOnClickListener(this);
		groupAddConfirmBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.group_add_cancle_btn:
			GroupManagerAddActivity.this.finish();
			break;
		case R.id.group_add_confirm_btn:
			addGroup();

			GroupManagerAddActivity.this.finish();
			break;
		}
	}

	private void addGroup() {
		String groupNameEt = groupAddNameEt.getText().toString().toString();
		if (groupNameEt == null || "".equals(groupNameEt)) {
			ToastUtil.toast(this, "请输入分组名称");
			return;
		}
		char[] groupNameCharArray = groupNameEt.toCharArray();
		StringBuffer groupFirstSb = new StringBuffer();
		for (int i = 0; i < groupNameCharArray.length; i++) {
			String firstStr = (characterParserUtil.getSelling(String
					.valueOf(groupNameCharArray[i]))).substring(0, 1);
			groupFirstSb.append(firstStr.toLowerCase());
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("groupName=", groupNameEt);
		List<Object> groupObjList = DBOperator.getInstance().queryBeanList(
				DBBean.TB_GROUP_DB, params);
		if (groupObjList.size() > 0) {
			ToastUtil.toast(this, "该分组名称已存在，请换一个！");
			return;
		}
		if (isAddFlag) {
			GroupBean groupBean = new GroupBean();
			groupBean.setGroupId(groupFirstSb.toString());
			groupBean.setGroupName(groupNameEt);
			groupBean.setGroupDate(Calendar.getInstance().getTimeInMillis());
			DBOperator.getInstance().insert(DBBean.TB_GROUP_DB, groupBean);
		} else {// 修改
			int groupUserId = UserInfoManager.getInstance().getGroupUserId(groupName);
			if(groupUserId == -1){
				ToastUtil.toast(this, "该部门不存在");
				return;
			}
			
			params.clear();
			params.put("id", String.valueOf(groupUserId));
			ContentValues values = new ContentValues();
			values.put("groupName", groupNameEt);
			values.put("groupId", groupFirstSb.toString());
			DBOperator.getInstance().update(DBBean.TB_GROUP_DB, values, params);
			
			
		}

		params.clear();
		params.put("groupName=", groupNameEt);
		groupObjList = DBOperator.getInstance().queryBeanList(
				DBBean.TB_GROUP_DB, params);
		UserInfoManager.getInstance().addToGroup(
				(GroupBean) groupObjList.get(0));
	}

}
