package com.lming.minichat.bean;

public class GroupMemberBean {

	private String sortLetters;  //显示数据拼音的首字母
	private String sortPinYin;	//根据pinyin排序
	private String departName;
	private UserBean userBean;
	
	private boolean isChecked;//电话会议中是否部门是否选择 默认false 
	
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserInfoBean(UserBean userBean) {
		this.userBean = userBean;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public String getSortPinYin() {
		return sortPinYin;
	}
	public void setSortPinYin(String sortPinYin) {
		this.sortPinYin = sortPinYin;
	}
	
}
