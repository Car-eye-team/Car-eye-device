/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.util.Log;

import com.sh.camera.service.MyIntentServiceSdCar;
import com.sh.camera.util.Constants;


/**
 * sd 文件卡处理
 */


public class SdCardBiz {
	private static SdCardBiz instance;
	String path = Constants.SD_CARD_PATH;
	
	public static SdCardBiz getInstance() {
		if (instance == null) {
			instance = new SdCardBiz();
		}
		return instance;
	}
	
	public String getSDPath(){
		return path;
	}
	public void getDetection(boolean isCleaning){
		if(!isCleaning&&getSDFreeSize()<Constants.SD_FREE2C){
			Constants.isCleaning = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					cleanSD();
					Constants.isCleaning = false;
				}
			}).start();
		}
	}
	public void getDetectionTWO(){
//		if(!isCleaning&&getSDFreeSize()<Constants.SD_FREE2C){
			Constants.isCleaning = true;
			cleanSD();
			Constants.isCleaning = false;
//		}
	}	
	
	public void getDetectionServiceSdCar(boolean isCleaning, Context instance){
		Log.d("CMD", "disk clean process!");  
		if(!isCleaning&&getSDFreeSize()<Constants.SD_FREE2C){
			if (MyIntentServiceSdCar.instance == null) {
				
				Intent intentS= new Intent(instance,MyIntentServiceSdCar.class);
				instance.startService(intentS);
				
			}else {
				Intent intentS= new Intent(instance,MyIntentServiceSdCar.class);
				instance.startService(intentS);
			}
		}
	}
	
	
	//获取剩余容量
    public long getSDFreeSize() {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    //获取路径总容量
    public long getSDAllSize() {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }
    
    private void cleanSD(){
		/**
		 * 在清理之后，清空一个LOST.DIR文件夹内的莫名数据
		 */    	
		File momingqimiao = new File(getSDPath()+"LOST.DIR/");
		if(momingqimiao.exists()){
			File[] ms = momingqimiao.listFiles();
			if(ms!=null&&ms.length>0){
				for (int i = 0; i < ms.length; i++) {
					ms[i].delete();
				}
			}
		}
		
		//获取所有本应用的文件
		ArrayList<HashMap<String, String>> files = new ArrayList<HashMap<String, String>>();
		File f = new File(Constants.CAMERA_FILE_PATH);
		if(f.exists()) addFile(f, files);
		//按时间排序----排序在addFile方法内完成
//		for (int i = 0; i < files.size(); i++) {
//			long l1 = Long.parseLong(files.get(i).get("time"));
//			for (int j = 1; j < files.size(); j++) {
//				long l2 = Long.parseLong(files.get(j).get("time"));
//				if(l1>l2){
//					HashMap<String, String> map = files.get(i);
//					files.set(i, files.get(j));
//					files.set(j, map);
//				}
//			}
//		}
		//循环删除
		
		for (int i = 0; i < files.size(); i++) {
			File file = new File(files.get(i).get("path"));
			if(file.exists()){
				file.delete();
			}
			if(getSDFreeSize()>Constants.SD_FREE2C) break;
		}
	}

	//添加文件夹内上锁文件以外的文件
	private void addFile(File f, ArrayList<HashMap<String, String>> files){
		File[] fs = f.listFiles();
	
		for (int i = 0; i < fs.length; i++) {
			for (int j = i+1; j < fs.length; j++) {
				
				int firstPo = getStringPosL(fs[i].getName());
				if (firstPo>=0) {
					firstPo = firstPo+1;
				}else {
					firstPo = 0;
				}
				long t1 = 0,t2 = 0;
				try {
					if(fs[i].getName().substring(0, 4).equals("lock")){
						t1 = Long.parseLong(fs[i].getName().substring(4, fs[i].getName().length()-4));
					}else{
						t1 = Long.parseLong(fs[i].getName().substring(firstPo, fs[i].getName().length()-4));
					}
					if(fs[j].getName().substring(0, 4).equals("lock")){
						t2 = Long.parseLong(fs[j].getName().substring(4, fs[j].getName().length()-4));
					}else{
						t2 = Long.parseLong(fs[j].getName().substring(firstPo, fs[j].getName().length()-4));
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("88888888888888_____getDetection -e:"+e);
				}
				
				if(t1>t2){
					File file = fs[i];
					fs[i] = fs[j];
					fs[j] = file;
				}
			}
		}
		
		L:for (int i = 0; i < fs.length; i++) {
			try {
				String name = fs[i].getName();
				
				int firstPo = getStringPosL(name);
				if (firstPo>=0) {
					firstPo = firstPo+1;
				}else {
					firstPo = 0;
				}
				if(name.substring(0, 4).equals("lock")) continue L;
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("path", fs[i].getAbsolutePath());
				map.put("time", Long.parseLong(name.substring(firstPo, name.length()-4))+"");
				files.add(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


public int getStringPosL(String path){
	int po= -1;
	try {
		if (!path.isEmpty()) {
			po = path.indexOf("-");
		}
		return po;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return po;
	}	
	
}

}
