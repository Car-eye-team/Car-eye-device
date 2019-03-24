package com.sh.camera.socket.db;

import com.sh.camera.socket.utils.CommConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名称：808    
 * 类名称：DBOpenHelper    
 * 类描述：操作数据库    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:18:30    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:18:30    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class DBOpenHelper extends SQLiteOpenHelper {


	private static final String DATABASE_NAME = CommConstants.DATABASE_NAME;  
	public static int DATABASE_VERSION = 2;

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table datamsg (_id integer primary key autoincrement,seq integer,msgid integer,datahex text,createtime text)");
		System.out.println("数据库版本："+db.getVersion());
		
		// 若不是第一个版本安装，直接执行数据库升级
		final int FIRST_DATABASE_VERSION = 1;
		onUpgrade(db, FIRST_DATABASE_VERSION, DATABASE_VERSION);


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if(newVersion>oldVersion){
			upgradeToVersion(db);
		}
	}

	/**
	 * 更新数据库
	 * @param db
	 */
	private void upgradeToVersion(SQLiteDatabase db){
		//db.execSQL("create table location (gpsflag integer,lat integer,lng integer,speed integer,direction integer,miles integer,gpstime text,createtime datetime)");
	}

	

}
