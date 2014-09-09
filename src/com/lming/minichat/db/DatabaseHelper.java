package com.lming.minichat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	DatabaseHelper(Context context, String name, CursorFactory cursorFactory,
			int version) {
		super(context, name, cursorFactory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE mytable (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作
	}
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		// TODO 每次成功打开数据库后首先被执行
	}
	
	public void testOperatot(){
		SQLiteDatabase sqlDb = getReadableDatabase();
		sqlDb.execSQL("INSERT INTO mytable (_id, title) VALUES (5,'test')"); 
		ContentValues cv=new ContentValues(); 
		cv.put("_id", 6); 
		cv.put("title", "test"); 
		sqlDb.insert("mytable",null, cv);
	}
}
