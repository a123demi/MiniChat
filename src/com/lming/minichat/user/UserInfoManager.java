package com.lming.minichat.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lming.minichat.bean.GroupBean;
import com.lming.minichat.bean.UserBean;

/**
 * 用户管理单例
 * 
 * @author Administrator
 * 
 */
public class UserInfoManager {
	private static UserInfoManager instance;

	private UserBean mSelfUserBean;// 登陆用户信息

	private String mSelfLoginName;
	private String mSelfPassword;

	private GroupBean mSelfGroupBean;
	private String mGroupName;
	private String mGroupId;
	private int mGroupUserId;

	/**
	 * <loginName, UserInfoBean>
	 */
	private Map<Integer, GroupBean> groupMap = new HashMap<Integer, GroupBean>();
	private Map<String, UserBean> userMap = new HashMap<String, UserBean>();
	private Map<String, List<UserBean>> groupUserMap = new HashMap<String, List<UserBean>>();

	public static UserInfoManager getInstance() {
		synchronized (UserInfoManager.class) {
			if (instance == null) {
				instance = new UserInfoManager();
			}
			return instance;
		}
	}

	public UserInfoManager() {

	}

	/**
	 * 增加部门
	 * 
	 * @param groupBean
	 */
	public void addToGroup(GroupBean groupBean) {
		groupMap.put(groupBean.getId(), groupBean);
	}
	
	public void removeGroup(int groupUserId){
		groupMap.remove(groupUserId);
	}
	
	public int getGroupUserId(String groupName){
		int groupUserId = -1;
		List<GroupBean> groupBeanList = this.getGroupBeans();
		for(GroupBean groupBean:groupBeanList){
			if(groupBean.getGroupName().equals(groupName)){
				groupUserId = groupBean.getId();
				break;
			}
		}
		return groupUserId;
	}
	
	/**
	 * 添加用户
	 * 
	 * @param userBean
	 */
	public void addToFriend(UserBean userBean) {
		userMap.put(userBean.getLoginName(), userBean);

		if (userBean.getLoginName().equals(this.getmSelfLoginName())) {
			this.setmSelfUserBean(userBean);
			this.setmGroupUserId(userBean.getGroupUserId());
		}

		int groupUserId = userBean.getGroupUserId();

		GroupBean groupBean = groupMap.get(groupUserId);
		String groupName = groupBean.getGroupName();
		if (this.mSelfUserBean != null) {
			if (groupBean.getId() == mSelfUserBean.getGroupUserId()) {
				this.setmGroupId(groupBean.getGroupId());
				this.setmGroupName(groupName);
				this.setmSelfGroupBean(groupBean);
				
			}
		}
		List<UserBean> userList = groupUserMap.get(groupName);

		if (userList == null) {
			userList = new ArrayList<UserBean>();
		}

		userList.remove(userBean);
		userList.add(userBean);

		groupUserMap.put(groupName, userList);
	}

	/**
	 * 根据loginName 获取用户
	 * 
	 * @param loginName
	 * @return
	 */
	public UserBean getUserBeanByLoginName(String loginName) {
		if (loginName == null)
			return null;
		synchronized (userMap) {
			if (mSelfUserBean != null) {
				if (loginName.equals(mSelfUserBean.getLoginName()))
					return mSelfUserBean;
			}
			return userMap.get(loginName);
		}
	}

	/**
	 * 获取所有组的List
	 * 
	 * @return
	 */
	public List<GroupBean> getGroupBeans() {
		synchronized (groupMap) {
			if (groupMap == null || groupMap.size() == 0)
				return null;
			// 更新部门列表
			Set<Integer> allGroups = new HashSet<Integer>();
			allGroups.addAll(groupMap.keySet());

			List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
			for (int key : allGroups) {
				groupBeanList.add(groupMap.get(key));
			}
			return groupBeanList;
		}
	}

	/**
	 * 根据部门名称获取该部门id的所有用户
	 * 
	 * @param groupName
	 * @return
	 */
	public List<UserBean> getUserBeanByGroupName(String groupName) {
		if (groupName == null) {
			return null;
		}
		List<UserBean> userList = new ArrayList<UserBean>();

		if (groupUserMap.get(groupName) != null) {
			userList.addAll(groupUserMap.get(groupName));
		}
		return userList;
	}
	
	public void updateUser(UserBean userBean){
		userMap.put(userBean.getLoginName(), userBean);
	}
	
	/**
	 * 根据Id获取GroupBean
	 * @param groupUserId
	 * @return
	 */
	public GroupBean getGroupBeanByGroupUserId(int groupUserId){
		return groupMap.get(groupUserId);
	}

	/**
	 * @return the mSelfUserBean
	 */
	public UserBean getmSelfUserBean() {
		return mSelfUserBean;
	}

	/**
	 * @param mSelfUserBean
	 *            the mSelfUserBean to set
	 */
	public void setmSelfUserBean(UserBean mSelfUserBean) {
		this.mSelfUserBean = mSelfUserBean;
	}

	/**
	 * @return the mSelfLoginName
	 */
	public String getmSelfLoginName() {
		return mSelfLoginName;
	}

	/**
	 * @param mSelfLoginName
	 *            the mSelfLoginName to set
	 */
	public void setmSelfLoginName(String mSelfLoginName) {
		this.mSelfLoginName = mSelfLoginName;
	}

	/**
	 * @return the mSelfPassword
	 */
	public String getmSelfPassword() {
		return mSelfPassword;
	}

	/**
	 * @param mSelfPassword
	 *            the mSelfPassword to set
	 */
	public void setmSelfPassword(String mSelfPassword) {
		this.mSelfPassword = mSelfPassword;
	}

	/**
	 * @return the mSelfGroupBean
	 */
	public GroupBean getmSelfGroupBean() {
		return mSelfGroupBean;
	}

	/**
	 * @param mSelfGroupBean
	 *            the mSelfGroupBean to set
	 */
	public void setmSelfGroupBean(GroupBean mSelfGroupBean) {
		this.mSelfGroupBean = mSelfGroupBean;
	}

	/**
	 * @return the mGroupName
	 */
	public String getmGroupName() {
		return mGroupName;
	}

	/**
	 * @param mGroupName
	 *            the mGroupName to set
	 */
	public void setmGroupName(String mGroupName) {
		this.mGroupName = mGroupName;
	}

	/**
	 * @return the mGroupId
	 */
	public String getmGroupId() {
		return mGroupId;
	}

	/**
	 * @param mGroupId
	 *            the mGroupId to set
	 */
	public void setmGroupId(String mGroupId) {
		this.mGroupId = mGroupId;
	}

	/**
	 * @return the mGroupUserId
	 */
	public int getmGroupUserId() {
		return mGroupUserId;
	}

	/**
	 * @param mGroupUserId the mGroupUserId to set
	 */
	public void setmGroupUserId(int mGroupUserId) {
		this.mGroupUserId = mGroupUserId;
	}
	

}
