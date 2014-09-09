package com.lming.minichat.bean;

public class GroupBean {
	public int id;
	public String groupName;
	public String groupId;
	
	public long groupDate;
	
	public GroupBean(){
		
	}
	
	public GroupBean(String groupName,String groupId){
		this.setGroupId(groupId);
		this.setGroupName(groupName);
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName
	 *            the groupName to set
	 */
	public void setGroupName(String groupName) {
			this.groupName = groupName;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
			this.groupId = groupId;
	}
	/**
	 * @return the groupDate
	 */
	public long getGroupDate() {
		return groupDate;
	}
	/**
	 * @param groupDate the groupDate to set
	 */
	public void setGroupDate(long groupDate) {
		this.groupDate = groupDate;
	}
	
	
	
}
