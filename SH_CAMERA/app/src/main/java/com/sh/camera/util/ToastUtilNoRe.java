/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtilNoRe {  
  
    private static String oldMsg;  
    protected static Toast toast   = null;  
    private static long oneTime=0;  
    private static long twoTime=0;  
      
    public static void showToast(Context context, String s){      
    	Toast.makeText(context, s,  0).show();
    }  
    public static void showToastN(Context context, String s){      
    	if(toast==null){   
    		toast =Toast.makeText(context, s, Toast.LENGTH_SHORT);  
    		toast.show();  
    		oneTime=System.currentTimeMillis();  
    	}else{  
    		twoTime=System.currentTimeMillis();  
    		if(s.equals(oldMsg)){  
    			if(twoTime-oneTime>Toast.LENGTH_SHORT){  
    				toast.show();  
    			}  
    		}else{  
    			oldMsg = s;  
    			toast.setText(s);  
    			toast.show();  
    		}         
    	}  
    	oneTime=twoTime;  
    }  
      
      
    public static void showToast(Context context, int resId){     
        showToast(context, context.getString(resId));  
    }  
  
}  
