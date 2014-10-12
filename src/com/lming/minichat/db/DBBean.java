package com.lming.minichat.db;

import java.util.HashMap;
import java.util.Map;
/**
 * 数据表对象
 * @author szluyl
 *
 */
public final class DBBean {
	public static final String TB_USER_DB = "tb_user_db";
	public static final String TB_GROUP_DB = "tb_group_db";
	public static final String TB_MESSAGE_DB = "tb_message_db";
	
	//key为表名，value为表对应的实体类
	public static Map<String,String> needInitTables = new HashMap<String,String>();
	
	public static Map<String,String> primaryKey = new HashMap<String,String>();
	static{
		//定义表与实体类对应关系
		needInitTables.put(TB_USER_DB,"com.lming.minichat.bean.UserBean");
		//设置主键
		primaryKey.put(TB_USER_DB,"id");
		
		//定义表与实体类对应关系
		needInitTables.put(TB_GROUP_DB,"com.lming.minichat.bean.GroupBean");
		//设置主键
		primaryKey.put(TB_GROUP_DB,"id");
		
		//定义表与实体类对应关系
		needInitTables.put(TB_MESSAGE_DB,"com.lming.minichat.bean.MessageBean");
		//设置主键
		primaryKey.put(TB_MESSAGE_DB,"msgId");
	}
	

}
