/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.StatFs;

/**
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：FileOper    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月18日 上午10:01:00    
 * 修改人：Administrator    
 * 修改时间：2016年10月18日 上午10:01:00    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class FileOper {
	private static final String TAG = "FileOper";
	List<File> list = new ArrayList<File>();
	/** 
	 * 复制单个文件 
	 * @param oldPath String 原文件路径 
	 * @param newPath String 复制后路径 
	 * @return boolean 
	 */ 
	public static Boolean copyFile(String oldPath, String newPath) {   
		try {   
			int bytesum = 0;   
			int byteread = 0;   
			File oldfile = new File(oldPath);   
			//检查文件路径是否存在
			File filePath = new File(newPath);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			if (oldfile.exists()) { //文件存在时   
				InputStream inStream = new FileInputStream(oldPath); //读入原文件   
				FileOutputStream fs = new FileOutputStream(newPath+oldfile.getName().toString());   
				byte[] buffer = new byte[1444];   
				int length;   
				while ( (byteread = inStream.read(buffer)) != -1) {   
					bytesum += byteread; //字节数 文件大小   
					System.out.println(bytesum);   
					fs.write(buffer, 0, byteread);   
				}   
				fs.flush();   
				fs.close(); 
				inStream.close();
				return true;
			}
			return false;
		}   
		catch (Exception e) { 
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace();  
			return false;

		}   

	} 

	public static int totalLen = 0;

	/** 
	 * 复制整个文件夹内容 
	 * @param oldPath String 原文件路径 
	 * @param newPath String 复制后路径 
	 * @return boolean 
	 */ 
	public static boolean copyFolder(String oldPath, String newPath) { 

		try { 
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
			File a=new File(oldPath); 
			String[] file=a.list(); 
			File temp=null; 
			for (int i = 0; i < file.length; i++) { 
				if(oldPath.endsWith(File.separator)){ 
					temp=new File(oldPath+file[i]); 
				} 
				else{ 
					temp=new File(oldPath+File.separator+file[i]); 
				} 

				if(temp.isFile()){ 
					FileInputStream input = new FileInputStream(temp); 
					FileOutputStream output = new FileOutputStream(newPath + "/" + 
							(temp.getName()).toString()); 
					byte[] b = new byte[1024 * 5]; 
					int len; 
					while ( (len = input.read(b)) != -1) { 
						output.write(b, 0, len);
						totalLen = totalLen + len;
					} 
					output.flush(); 
					output.close(); 
					input.close(); 
				} 
				if(temp.isDirectory()){//如果是子文件夹 
					copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
				} 
			} 
			return true;
		} catch (Exception e) { 
			AppLog.e(ExceptionUtil.getInfo(e), e);
			e.printStackTrace(); 
			return false;
		} 

	}

	/**
	 * 获取指定路径剩余空间
	 * @param path
	 * @return 返回剩余空间
	 */
	public static long getSDFreeSize(String path){  
		//取得SD卡文件路径  
		// File path = Environment.getExternalStorageDirectory();   
		StatFs sf = new StatFs(path);   
		//获取单个数据块的大小(Byte)  
		long blockSize = sf.getBlockSize();   
		//空闲的数据块的数量  
		long freeBlocks = sf.getAvailableBlocks();  
		//返回SD卡空闲大小  
		//return freeBlocks * blockSize;  //单位Byte  
		//return (freeBlocks * blockSize)/1024;   //单位KB  
		return (freeBlocks * blockSize)/1024 /1024; //单位MB  
	} 

	/**
	 * 获取指定路径总容量
	 * @param path
	 * @return 返回总容量
	 */
	public static long getSDAllSize(String path){  
		//取得SD卡文件路径  
		//File path = Environment.getExternalStorageDirectory();   
		StatFs sf = new StatFs(path);   
		//获取单个数据块的大小(Byte)  
		long blockSize = sf.getBlockSize();   
		//获取所有数据块数  
		long allBlocks = sf.getBlockCount();  
		//返回SD卡大小  
		//return allBlocks * blockSize; //单位Byte  
		//return (allBlocks * blockSize)/1024; //单位KB  
		return (allBlocks * blockSize)/1024/1024; //单位MB  
	}    
	/**
	 * 获取文件夹内容大小
	 * @param f
	 * @return 返回内容大小
	 */
	public static long getFileListSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileListSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	/**
	 * 删除目录
	 * @param f 文件
	 * @return true 成功 false 失败
	 */
	public static Boolean del(File f){
		try {
			if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
				if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
					f.delete();
				} else {// 若有则把文件放进数组，并判断是否有下级目录
					File delFile[] = f.listFiles();
					int i = f.listFiles().length;
					for (int j = 0; j < i; j++) {
						if (delFile[j].isDirectory()) {
							File f1 = new File(delFile[j].getAbsolutePath());
							del(f1);// 递归调用del方法并取得子目录路径
						}
						delFile[j].delete();// 删除文件
					}
				}
			}
			f.delete();
			return true;
		} catch (Exception e) {
			AppLog.e(ExceptionUtil.getInfo(e), e);
			return false;
		}
	}

	//读取一个文件夹下所有文件及子文件夹下的所有文件  
	public  List<File> ReadAllFile(String filePath) {
		File f = null;
		f = new File(filePath);
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。
		for (File file : files) {
			if (file.isDirectory()) {
				// 如何当前路劲是文件夹，则循环读取这个文件夹下的所有文件
				ReadAllFile(file.getAbsolutePath());
			} else {
				list.add(file);
			}
		}
		return list;
	}

}
