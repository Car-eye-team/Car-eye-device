/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */
package com.sh.camera.socket.db;

import java.util.ArrayList;
import java.util.List;

import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.model.DSCommData;
import com.sh.camera.socket.utils.CommConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 项目名称：DSS_808    
 * 类名称：DataMsgDao    
 * 类描述：操作消息表     
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:41:07    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:41:07    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class DataMsgDao {

	private DBOpenHelper helper;
	private ContentValues values;

	private static DataMsgDao dataMsgDao = null;

	public DataMsgDao(Context context) {
		helper = new DBOpenHelper(context);
		values = new ContentValues();
	}

	public static DataMsgDao getInstance(Context context){
		if(dataMsgDao == null){
			dataMsgDao =  new DataMsgDao(context);
		}
		return dataMsgDao;
	}

	/**
	 * 插入记录
	 * @param seq 序列号
	 * @param msgid 消息ID
	 * @param datahex 数据
	 * @param createtime 创建时间
	 * @return 记录ID
	 */
	public synchronized long insert(int seq,int msgid,String datahex,String createtime) {
		long rowId = -1;
		if(helper == null){
			helper = new DBOpenHelper(ShCommService.getInstance());
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			values.put(CommConstants.SEQ, seq);
			values.put(CommConstants.MSGID, msgid);
			values.put(CommConstants.DATAHEX,datahex);
			values.put(CommConstants.CREATETIME,createtime);
			rowId = db.insert(CommConstants.TABLE_NAME, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db != null){
				db.close();
			}
		}
		return rowId;
	}

	/**
	 * 删除记录
	 * @param seq 流水号
	 * @param msgid 消息ID
	 * @return 记录ID
	 */
	public synchronized long delete(int seq,int msgid){
		long rowId = -1;
		if(helper == null){
			helper = new DBOpenHelper(ShCommService.getInstance());
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			String[] whereArgs = new String[2];
			whereArgs[0] = String.valueOf(seq);
			whereArgs[1] = String.valueOf(msgid);
			rowId = db.delete(CommConstants.TABLE_NAME, "seq=? and msgid= ?",whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db != null){
				db.close();
			}
		}
		return rowId;
	}
	
	/**
	 * 删除全部记录
	 * @return
	 */
	public synchronized long deleteAll(){
		long rowId = -1;
		if(helper == null){
			helper = new DBOpenHelper(ShCommService.getInstance());
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			rowId = db.delete(CommConstants.TABLE_NAME,null,null);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db != null){
				db.close();
			}
		}
		return rowId;
	}

	/**
	 * 获取全部记录
	 * @return 
	 */
	public synchronized List<DSCommData> findAll(){
		if(helper == null){
			helper = new DBOpenHelper(ShCommService.getInstance());
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		try {
			Cursor cursor = db.query(CommConstants.TABLE_NAME, null, null, null,null, null, null);
			List<DSCommData> list = new ArrayList<DSCommData>();
			DSCommData info = null;
			if(cursor.getCount()!=0){
				while (cursor.moveToNext()) {
					info = new DSCommData();
					info.setSeq(cursor.getInt(cursor.getColumnIndex(CommConstants.SEQ)));
					info.setMsgid(cursor.getInt(cursor.getColumnIndex(CommConstants.MSGID)));
					info.setDatahex(cursor.getString(cursor.getColumnIndex(CommConstants.DATAHEX)));
					info.setCreatetime(cursor.getString(cursor.getColumnIndex(CommConstants.CREATETIME)));
					list.add(info);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db != null){
				db.close();
			}
		}
		return null;
	}

	/**
	 * 获取记录总条数
 	 * @return 记录总数 
	 */
	public synchronized int getCount(){
		try {
			if(helper == null){
				helper = new DBOpenHelper(ShCommService.getInstance());
			}
			SQLiteDatabase db = helper.getWritableDatabase();
			values.clear();
			String sql = "select count(*) from datamsg";
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			int count = c.getInt(0);
			db.close();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return 0;
		}
	}
}
