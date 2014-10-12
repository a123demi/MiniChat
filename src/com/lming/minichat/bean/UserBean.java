package com.lming.minichat.bean;

public class UserBean {
	private int id;
	private String loginName;
	private String password;
	private String nickName;

	private String email;
	private String telphone;
	private int old;
	private int sex;
	private long birthDay;
	private long registerDate;
	
	private int groupUserId;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getOld() {
		return old;
	}

	public void setOld(int old) {
		this.old = old;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public long getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(long birthDay) {
		this.birthDay = birthDay;
	}

	/**
	 * @return the registerDate
	 */
	public long getRegisterDate() {
		return registerDate;
	}

	/**
	 * @param registerDate
	 *            the registerDate to set
	 */
	public void setRegisterDate(long registerDate) {
		this.registerDate = registerDate;
	}

	/**
	 * @return the telphone
	 */
	public String getTelphone() {
		return telphone;
	}

	/**
	 * @param telphone
	 *            the telphone to set
	 */
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	/**
	 * @return the groupUserId
	 */
	public int getGroupUserId() {
		return groupUserId;
	}

	/**
	 * @param groupUserId the groupUserId to set
	 */
	public void setGroupUserId(int groupUserId) {
		this.groupUserId = groupUserId;
	}
}
