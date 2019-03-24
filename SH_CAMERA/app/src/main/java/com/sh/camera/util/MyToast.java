/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sh.camera.R;



@SuppressLint("ResourceAsColor")
public class MyToast {

	public static Toast toast = null;
	public MyToast(){
	}

	private static String oldMsg;  
	private static long oneTime=0;  
	private static long twoTime=0;  
	/**
	 * 
	 * @param context
	 * @param s
	 * @param b
	 * @param which 0 small
	 */
	public static void showToast(Context context, String s,boolean b,int img){ 

		if(toast==null){   
			toast =Toast.makeText(context, s, Toast.LENGTH_SHORT);  
			toast.setGravity(Gravity.CENTER, 0, 0);
			if (b) {
				toast.setDuration(Toast.LENGTH_SHORT);
			}else {
				toast.setDuration(Toast.LENGTH_LONG);
			}
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.toast_new, null);
			TextView tv = (TextView) view.findViewById(R.id.toast_tv);
			ImageView iv_one = (ImageView) view.findViewById(R.id.iv_toast_one);
			ImageView iv_two = (ImageView) view.findViewById(R.id.iv_toast_two);
			iv_one.setImageResource(img);
			tv.setText(s);
			//    		 if (which==0) {
				//    			 iv_one.setVisibility(View.GONE);
				//    			 iv_two.setVisibility(View.VISIBLE);
			//    		}else {
			//    			iv_two.setVisibility(View.GONE);
			//    			iv_one.setVisibility(View.VISIBLE);
			//    		}
			toast.setView(view);
			toast.show();  
			oneTime=System.currentTimeMillis();  
		}else{  
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.toast_new, null);
			TextView tv = (TextView) view.findViewById(R.id.toast_tv);
			ImageView iv_one = (ImageView) view.findViewById(R.id.iv_toast_one);
			ImageView iv_two = (ImageView) view.findViewById(R.id.iv_toast_two);

			iv_one.setImageResource(img);


			twoTime=System.currentTimeMillis();  
			if(s.equals(oldMsg)){  

				if(twoTime-oneTime>Toast.LENGTH_SHORT){ 

					toast.show();  
				} 

			}else{  

				oldMsg = s;  
				//                toast.setText(s);  
				tv.setText(s);
				toast.setView(view);
				toast.show();  
			}         
		}  
		oneTime=twoTime;  
	} 

	public static void showToast(Context context, int resId,boolean b,int img){ 

		String text = context.getResources().getString(resId);
		showToast(context, text, b, img);
	}
}
