package com.lming.minichat.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lming.minichat.MainApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库管理类
 * 
 * @author 杨雪平
 * 
 */
public class DBOperator extends SQLiteOpenHelper {
	private static final String TAG = " DBOperator ";
	private static SQLiteDatabase sqlDb = null;
	public static String DB_NAME = "MINI_CHAT_DB";
	public static int DB_VERSION = 1;
	private Context context = null;
	
	private static DBOperator instance = null;
	
	static{
    	instance = new DBOperator();
	}
	
	public static DBOperator getInstance(){
		return instance;
	}
	

	private DBOperator() {
		super(MainApplication.getInstance(), DB_NAME, null, DB_VERSION);
		context = MainApplication.getInstance();
		sqlDb = getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 遍历需要初始化的数据表
		Log.i(TAG,"初始化的数据表 ***********************************************************");
		try {
			StringBuffer createSql = null;
			for (String tableName : DBBean.needInitTables.keySet()) {
				//获取表对应的实体类
				Class newoneClass = Class.forName(DBBean.needInitTables.get(tableName));
				Field[] fs = newoneClass.getDeclaredFields(); 
				//根据实体类的成员变量构造创建表语句
				StringBuffer tableField = new StringBuffer("");
				createSql = new StringBuffer("create table ");
				for (Field f : fs) {
					//判断该属性是否定义为主键，如果不是根据属性的类型分配不通的列类型
					if(DBBean.primaryKey.containsKey(tableName)&&DBBean.primaryKey.get(tableName).equals(f.getName())){
						tableField.append(tableField.length() > 0 ? ",": "").append(f.getName()).append(" integer PRIMARY KEY");
					}else if(f.getType().equals(int.class)){
						tableField.append(tableField.length() > 0 ? ",": "").append(f.getName()).append(" integer");
					}else if(f.getType().equals(float.class)){
						tableField.append(tableField.length() > 0 ? ",": "").append(f.getName()).append(" float");
					}else if(f.getType().equals(boolean.class)){
						tableField.append(tableField.length() > 0 ? ",": "").append(f.getName()).append(" boolean");
					}else{
						tableField.append(tableField.length() > 0 ? ",": "").append(f.getName()).append(" text");
					}
				}
				createSql.append(tableName).append("(").append(tableField).append(");");//合并语句
				db.execSQL(createSql.toString());//创建表
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
	}

	/**
	 * 查询
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	private Cursor queryCursor(String tableName, String[] returnColumn,
			Map<String, String> params) {
		try {
			// params转换成可执行查询条件
			String queryParm = null;
			String[] queryParmValue = null;
			String limit = null;
			String groupBy = null;
			String having = null;
			String orderBy = null;
			
			if (params != null && params.size() > 0) {
				StringBuffer queryKey = new StringBuffer("");
				//queryParmValue = new String[params.size()];
				ArrayList<String> queryParmList = new ArrayList<String>();
				int i = 0;
				for (String key : params.keySet()) {
					if ("LIMIT".equals(key)) {
						limit = params.get(key);
					} else if ("GROUPBY".equals(key)) {
						groupBy = params.get(key);
					} else if ("HAVING".equals(key)) {
						having = params.get(key);
					} else if ("ORDERBY".equals(key)) {
						orderBy = params.get(key);
					}else{
						queryKey.append(queryKey.length() > 0 ? " and " : "").append(key).append("?");
						queryParmList.add(params.get(key));
					}
				}
				queryParm = queryKey.toString();
				
				if(queryParmList.size()>0){
					queryParmValue = new String[queryParmList.size()];
					queryParmList.toArray(queryParmValue);
				}
			}
			
			Cursor cursor = sqlDb.query(tableName, returnColumn, queryParm,
					queryParmValue, groupBy, having, orderBy,limit);
			return cursor;
		} catch (Exception ce) {
			ce.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断当前数据在表中是否存在
	 * 
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public int queryRowNum(String tableName, Map<String, String> params){
		Cursor cursor = queryCursor(tableName, new String[] { "*" }, params);
		int rowNum = cursor.getCount();
		cursor.close();
		return rowNum;
	}

	/**
	 * 查询记录，返回数组列表
	 * 
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public List<String[]> queryArrayList(String tableName,
			String[] returnColumn, Map<String, String> params){
		List<String[]> returnList = new ArrayList<String[]>();
		Cursor cursor = queryCursor(tableName, returnColumn, params);
		if(cursor!=null){
			cursor.moveToFirst();
			String[] columValue = null;
			while (!cursor.isAfterLast()) {
				//遍历返回数据到列表中
				columValue = new String[cursor.getColumnCount()];
				for (int i = 0; i < cursor.getColumnCount();) {
					columValue[i] = cursor.getString(i++);
				}
				returnList.add(columValue);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return returnList;
	}

	/**
	 * 查询记录到MAP中
	 * @param tableName 表名
	 * @param returnColumn 需要返回数据的列名称
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public List<Map<String, String>> queryMapList(String tableName,
			String[] returnColumn, Map<String, String> params){
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
		Cursor cursor = queryCursor(tableName, returnColumn, params);
		if(cursor!=null){
			cursor.moveToFirst();
			Map<String, String> columValue = null;
			while (!cursor.isAfterLast()) {
				//遍历返回数据到列表中
				columValue = new HashMap<String, String>();
				for (int i = 0; i < cursor.getColumnCount();) {
					columValue.put(returnColumn[i], cursor.getString(i++));
				}
				returnList.add(columValue);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return returnList;
	}
	
	/**
	 * 查询记录到实体类列表中
	 * @param tableName 表名
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public List<Object> queryBeanList(String tableName,Map<String, String> params){
		List<Object> returnList = new ArrayList<Object>();
		Cursor cursor = queryCursor(tableName, new String[]{"*"}, params);//查询全部数据
		if(cursor!=null){
			try{
				//获取表对应的实体类
				Class tableBean = Class.forName(DBBean.needInitTables.get(tableName));
				//获取表对应的实体类构造函数
				Constructor construtor =  tableBean.getConstructor();
				Field[] fs = tableBean.getDeclaredFields(); 
				cursor.moveToFirst();
				StringBuffer methodName = null;
				Method setMethod = null;
				Object object = null;
				while (!cursor.isAfterLast()) {
					//遍历返回数据，构造实体对象
					object = construtor.newInstance();
					for (int i = 0; i < cursor.getColumnCount();i++) {
						//给实体对象的成员变量赋值
						methodName = new StringBuffer("set");
						String columnName = cursor.getColumnName(i);
						String columnValue = cursor.getString(i);
						
						//logger.error(columnName+"++++++++++++++"+columnValue);
						
//						Field f = tableBean.getField(columnName);
						Field f = tableBean.getDeclaredField(columnName);
						//methodName.append(columnName);
						methodName.append(columnName.substring(0, 1).toUpperCase()).append(columnName.substring(1));
						
						if(f.getType().equals(float.class)){
							setMethod = tableBean.getMethod(methodName.toString(),float.class);
							setMethod.invoke(object, Float.parseFloat(columnValue));
						}else if(f.getType().equals(boolean.class)){
							setMethod = tableBean.getMethod(methodName.toString(),boolean.class);
							if(columnValue.equals("0")){
								setMethod.invoke(object, false);
							}else{
								setMethod.invoke(object, true);
							}
							
						}else if(f.getType().equals(int.class)){
							setMethod = tableBean.getMethod(methodName.toString(),int.class);
							setMethod.invoke(object, Integer.parseInt(columnValue));
						}else if(f.getType().equals(long.class)){
							setMethod = tableBean.getMethod(methodName.toString(),long.class);
							setMethod.invoke(object, Long.parseLong(columnValue));							
						}else{
							setMethod = tableBean.getMethod(methodName.toString(),String.class);
							setMethod.invoke(object, columnValue);
						}
					}
					returnList.add(object);
					cursor.moveToNext();
				}
			}catch(Exception ine){
				ine.printStackTrace();
				
			}finally{
				cursor.close();
			}
		}

		return returnList;
	}
	
	/**
	 * 插入记录到当前表
	 * @param tableName 表名
	 * @param value 表对应实体类对象
	 * @return
	 */
	public synchronized long insert(String tableName,Object value){
		
		long insertRow = 0;
		//SQLiteDatabase insertDB = null;
		try {
			Field[] fs = value.getClass().getDeclaredFields();
			ContentValues contentValues = new ContentValues();
			StringBuffer methodName = null;
			Method getMethod = null;
			Class clazz = value.getClass();
			for(Field f:fs){
				//取实体对象的成员变量值
				if(DBBean.primaryKey.containsKey(tableName)&&DBBean.primaryKey.get(tableName).equals(f.getName()))//如果是主键则不需要插入
					continue;
				
				methodName = new StringBuffer("get");
				//methodName.append(f.getName());
				methodName.append(f.getName().substring(0, 1).toUpperCase()).append(f.getName().substring(1));
				getMethod = clazz.getMethod(methodName.toString());
				if(f.getType().equals(float.class)){
					contentValues.put(f.getName(), ((Float)getMethod.invoke(value)).floatValue());
				}else if(f.getType().equals(boolean.class)){
					contentValues.put(f.getName(), ((Boolean)getMethod.invoke(value)).booleanValue());
				}else if(f.getType().equals(int.class)){
					contentValues.put(f.getName(), ((Integer)getMethod.invoke(value)).intValue());
				}else if(f.getType().equals(long.class)){
					contentValues.put(f.getName(), ((Long)getMethod.invoke(value)).longValue());
				}else{
					contentValues.put(f.getName(), ((String)getMethod.invoke(value)));
				}
			}
			
			insertRow = sqlDb.insert(tableName, null, contentValues);
		} catch (Exception ce) {
			ce.printStackTrace();
		}
		return insertRow;
	}

	/**
	 * 修改记录
	 * @param tableName 表名
	 * @param values 修改后的值
	 * @param params key表示条件，value表示条件的值，如：key="id>",value=1
	 * @return
	 */
	public synchronized long update(String tableName, ContentValues values,Map<String, String> params){
		
		long insertRow = 0;
		try {
			// params转换成可修改限定条件
			String queryParm = null;
			String[] queryParmValue = null;
			if (params != null && params.size() > 0) {
				StringBuffer queryKey = new StringBuffer("");
				queryParmValue = new String[params.size()];
				int i = 0;
				for (String key : params.keySet()) {
					queryKey.append(queryKey.length() > 0 ? " and " : "").append(key).append("?");
					queryParmValue[i++] = params.get(key);
				}
				queryParm = queryKey.toString();
			}
			insertRow = sqlDb.update(tableName, values, queryParm, queryParmValue);
		} catch (Exception ce) {
			ce.printStackTrace();
		}
		return insertRow;
	}
	
	/**
	 * 数据删除
	 * @param tableName
	 * @param params
	 */
	public synchronized void del(String tableName,Map<String, String> params){
		String queryParm = null;
		String[] queryParmValue = null;
		if (params != null && params.size() > 0) {
			StringBuffer queryKey = new StringBuffer("");
			queryParmValue = new String[params.size()];
			int i = 0;
			for (String key : params.keySet()) {
				queryKey.append(queryKey.length() > 0 ? " and " : "").append(key).append("?");
				queryParmValue[i++] = params.get(key);
			}
			queryParm = queryKey.toString();
		}
		sqlDb.delete(tableName, queryParm, queryParmValue);
	}
	/**
	 * 释放资源
	 */
//	public void release(){
//    	if(sqlDb!=null){
//    		sqlDb.close();
//    		sqlDb = null;
//    	}
//    	close();
//	}

}
