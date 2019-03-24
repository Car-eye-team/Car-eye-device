/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.DiskManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import com.sh.camera.service.MyIntentServiceSdCar;
import com.sh.camera.util.Constants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;



@SuppressLint("NewApi") public class DiskManager<StorageVolume> {
	  private  static final String tag = "DiskManager";
	  public String [] path = null;	  
	  Context mContext;
	  int m_DiskCnt;	
	  public static  boolean isCleaning = false;	  
	  public DiskManager(Context context)
	  {
		  mContext = context;
		  path = initDisk(context); 
		  m_DiskCnt = path.length;
		  isCleaning = false;
		  Log.d(tag, "Disk free : " + GetDiskFreeTotal()+"count"+m_DiskCnt);	
		 
	  }	  
	  public void RemountDisks()
	  {	 
		  path = initDisk(mContext);		 
		  m_DiskCnt = path.length;
		  Log.d(tag, "Disk free : " + GetDiskFreeTotal()+"count"+m_DiskCnt); 
		  	  	  
	  }
	  
	  public int getDiskCnt()
	  {
		  return m_DiskCnt;		  
	  }
	  
	  public long getSDFreeSize(int index) {
	        StatFs sf = new StatFs(path[index]);
	        long blockSize = sf.getBlockSize();
	        long freeBlocks = sf.getAvailableBlocks();
	        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	    }

	    //获取路径总容量
	    public long getSDAllSize(int index) {
	        StatFs sf = new StatFs(path[index]);
	        long blockSize = sf.getBlockSize();
	        long allBlocks = sf.getBlockCount();
	        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
	    }
	    
	  public int SelectDisk()
	  {
		  int DiskIndex = 0;
		  long Freesize = 0;
		  for(int i = 0; i < m_DiskCnt; i++ )
		  {
			  long count = getSDFreeSize(i);			  
			  if(count > Freesize)
			  {
				  Freesize = count;
				  DiskIndex = i;
			  }			  
		  }		  
		  return DiskIndex;
	  }
	  public String getDiskDirectory(int index)
	  {
		  if(index > m_DiskCnt)
			  return null;
		  return path[index];
			  
	  }	
	  
	  
	  public  String[] initDisk(Context context) {  
	        String sd = null; 
	        Class<?> storageVolumeClazz = null;  
	        ArrayList<String> lineByteList = new ArrayList<String>();	
	        try { 
	        StorageManager storageManager = (StorageManager) context  
	                .getSystemService(Context.STORAGE_SERVICE);  
	        storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");  
	        Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList"); 	       	        
	        Method isRemovable = storageVolumeClazz.getMethod("isRemovable");  
	        Method getPath = storageVolumeClazz.getMethod("getPath");  
	        Object  volumes = getVolumeList.invoke(storageManager);
	        Method getVolumeState = StorageManager.class.getDeclaredMethod("getVolumeState", String.class); 
	        final int length = Array.getLength(volumes);        
	        for (int i = 0; i < length; i++) 
	        {	        		           
	        	Object storageVolumeElement  = Array.get(volumes, i);  	            
	        	String path = (String) getPath.invoke(storageVolumeElement);  
            	String state = (String) getVolumeState.invoke(storageManager, path); 
            	boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);  
                if(Environment.MEDIA_MOUNTED.equals(state) && removable)
                {
                	lineByteList.add(path);
                	Log.d(tag, "Disk path  : " + path);
                }              
	           
	          }  
	        } catch (Exception e)
	        {  
	        	            Log.e(tag, e.getMessage());  
	        }  
	        path = new String [lineByteList.size()];
	        for(int j = 0;j < lineByteList.size(); j++ ){
	        	path[j] = lineByteList.get(j);	        
	        }	        
	        return path;  
	    }  
	  

	  
	   public  String getStorageState(String path,Context context) {
	        try {
	            StorageManager sm = (StorageManager) context.getSystemService(context.STORAGE_SERVICE);
	            Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[] {String.class});
	            String state = (String) getVolumeStateMethod.invoke(sm, path);
	            return state;
	        } catch (Exception e) {

	        }
	        return null;
	    }
	   
	   public void CreateDirctionaryOnDisk(String SubDir)	    
	   {
		    String FileName;
		    if(m_DiskCnt<=0)
		    	return;
		    for(int i =0; i<m_DiskCnt;i ++ )
		    {
			    FileName = getDiskDirectory(i)+SubDir;			    		
				File f = new File(FileName);
				if(!f.exists()){
					f.mkdirs();
				}
		    }  	   
	   }      
	   
	   public int GetDiskFreeTotal()
	   {
		   int total = 0;
		   for(int i = 0; i < m_DiskCnt; i++)
		   {
			   total += getSDFreeSize(i); 
		   }
		   return total;
		   
	   } 
	   
		public void DiskCleanprocess(){
			if(m_DiskCnt < 0)
			{
				return;
			}
			isCleaning = true;
			cleanSD();
			isCleaning = false;			
		}	
		
		public boolean GetCleanStatus()
		{
			return isCleaning;
		}	   
	   
		public void getDetectionServiceSdCar(Context instance){			
			Log.d(tag, "Disk free : " + GetDiskFreeTotal());
			if(!isCleaning&&GetDiskFreeTotal()<Constants.SD_FREEJX){
				if (MyIntentServiceSdCar.instance == null) {
					Intent intentS= new Intent(instance,MyIntentServiceSdCar.class);
					instance.startService(intentS);
					
				}else {
					Intent intentS= new Intent(instance,MyIntentServiceSdCar.class);
					instance.startService(intentS);
				}
			}
		}
		
	    private void cleanSD(){
			/**
			 * 在清理之后，清空一个LOST.DIR文件夹内的莫名数据
			 */    	
	    	for(int j= 0; j < m_DiskCnt; j++ )
	    	{
				File momingqimiao = new File(getDiskDirectory(j)+"LOST.DIR/");
				if(momingqimiao.exists()){
					File[] ms = momingqimiao.listFiles();
					if(ms!=null&&ms.length>0){
						for (int i = 0; i < ms.length; i++) {
							ms[i].delete();
						}
					}
				}
	    	}			
			//获取所有本应用的文件
			ArrayList<HashMap<String, String>> files = new ArrayList<HashMap<String, String>>();
			/*Filename = getDiskDirectory(index)+Constants.CAMERA_FILE_DIR; 					
			File f = new File(Filename);
			if(f.exists()) addFile(f, files);*/
			addFile(files);			
			for (int i = 0; i < files.size(); i++) {
				File file = new File(files.get(i).get("path"));
				Log.d(tag, "delete file : " + files.get(i).get("path"));
				if(file.exists()){
					file.delete();
				}
				if(GetDiskFreeTotal()>Constants.SD_FREE2C) break;
			}
		}

		//添加文件夹内上锁文件以外的文件
		private void addFile(ArrayList<HashMap<String, String>> files){
			
			File[] fs;
			ArrayList <File> file = new ArrayList<File>();			
			for(int i =0; i < m_DiskCnt; i++)
			{
				String Filename = getDiskDirectory(i)+Constants.CAMERA_FILE_DIR; 
				
				File f = new File(Filename);
				File[] fs1 = f.listFiles();	
				
				for(int j =0; j <fs1.length; j++)
					file.add(fs1[j]);		
			}	
			
			fs =new File[file.size()];			
			for(int i =0; i < file.size(); i++)
			{
					fs[i] = file.get(i);
			}		
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
						if (fs[i].getName().substring(0, 4).equals("lock")) {
							String sfilename = fs[i].getName();
							if (sfilename.endsWith(".mp4.tmp"))
								t1 = Long.parseLong(sfilename.substring(4,
										sfilename.length() - 8));
							else if (sfilename.endsWith(".mp4"))
								t1 = Long.parseLong(sfilename.substring(4,
										sfilename.length() - 4));
						} else {
							// t1 =
							// Long.parseLong(fs[i].getName().substring(firstPo,
							// fs[i].getName().length()-4));
							String sfilename = fs[i].getName();
							if (sfilename.endsWith(".mp4.tmp"))
								t1 = Long.parseLong(sfilename.substring(firstPo,
										sfilename.length() - 8));
							else if (sfilename.endsWith(".mp4"))
								t1 = Long.parseLong(sfilename.substring(firstPo,
										sfilename.length() - 4));
						}
						if (fs[j].getName().substring(0, 4).equals("lock")) {
							// t2 = Long.parseLong(fs[j].getName().substring(4,
							// fs[j].getName().length()-4));
							String sfilename = fs[j].getName();
							if (sfilename.endsWith(".mp4.tmp"))
								t2 = Long.parseLong(sfilename.substring(4,
										sfilename.length() - 8));
							else if (sfilename.endsWith(".mp4"))
								t2 = Long.parseLong(sfilename.substring(4,
										sfilename.length() - 4));
						} else {
							// t2 =
							// Long.parseLong(fs[j].getName().substring(firstPo,
							// fs[j].getName().length()-4));
							String sfilename = fs[j].getName();
							if (sfilename.endsWith(".mp4.tmp"))
								t2 = Long.parseLong(sfilename.substring(firstPo,
										sfilename.length() - 8));
							else if (sfilename.endsWith(".mp4"))
								t2 = Long.parseLong(sfilename.substring(firstPo,
										sfilename.length() - 4));
						}

					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("88888888888888_____getDetection -e:"+e);
					}					
					if(t1>t2){
						File file2 = fs[i];
						fs[i] = fs[j];
						fs[j] = file2;
					}
				}
			}
			
			L:for (int i = 0; i < fs.length; i++) {
				try {
					String name = fs[i].getName();					
					/*if(!name.endsWith("mp4") && !name.endsWith("jpg") )
					{
						File filedel = new File(fs[i].getAbsolutePath());
						Log.d(tag, "delete file : " + fs[i].getAbsolutePath());
						if(filedel.exists()){
							filedel.delete();
						}
						continue;
					}*/					
					int firstPo = getStringPosL(name);
					if (firstPo>=0) {
						firstPo = firstPo+1;
					}else {
						firstPo = 0;
					}
					if(name.substring(0, 4).equals("lock")) continue L;					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("path", fs[i].getAbsolutePath());
					if (name.endsWith(".mp4.tmp"))
						map.put("time",
								Long.parseLong(name.substring(firstPo,
										name.length() - 8))
										+ "");
					else if (name.endsWith(".mp4"))
						map.put("time",
								Long.parseLong(name.substring(firstPo,
										name.length() - 4))
										+ "");

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
