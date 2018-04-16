/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera;

import java.lang.reflect.Method;

import com.sh.camera.service.MainService;
import com.sh.camera.service.ShCommService;
import com.sh.camera.util.Constants;
import com.sh.camera.util.ToastUtilNoRe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;



public class MainActivity extends Activity {
	
	public static MainActivity mainactivity;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static final String TAG = "MainActivity";
	private static String[] PERMISSIONS_STORAGE = {
	        Manifest.permission.READ_EXTERNAL_STORAGE,
	Manifest.permission.WRITE_EXTERNAL_STORAGE };
	


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Constants.setParam(this);	
		int version = android.os.Build.VERSION.SDK_INT;
		Log.d("CMD", "version : " + version);	
		if(false){
			finish();
			return;
			
		}
		onAddALERT_WINDOW(null);
		if(!MainService.isrun){
			startService(new Intent(MainActivity.this, MainService.class));			
		}else{
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.FULLSCREEN);
			sendBroadcast(intent);
		}		
		mainactivity = this;
		//启动通讯service
		startService(new Intent(MainActivity.this, ShCommService.class));		
		finish();
	}
	// 添加悬浮窗权限代码
	@SuppressLint("NewApi")
	public void onAddALERT_WINDOW(View view) {
        
            //开启悬浮窗前先请求权限
            if ("Xiaomi".equals(Build.MANUFACTURER)) {//小米手机
                Log.e(TAG,"小米手机");
                requestPermission();
            } else if ("Meizu".equals(Build.MANUFACTURER)) {//魅族手机
                Log.e(TAG,"魅族手机");
                requestPermission();
            } else {//其他手机
                Log.e(TAG,"其他手机");
                if (Build.VERSION.SDK_INT >= 23) {
//                    if (!Settings.canDrawOverlays(this)) {
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                        startActivityForResult(intent, 12);
//                    } else {
//                        switchActivity();
//                    }
                } else {
                    switchActivity();
                }
            }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (isFloatWindowOpAllowed(this)) {//已经开启
                switchActivity();
            } else {
            	ToastUtilNoRe.showToast(this, "开启悬浮窗失败");
            }
        } else if (requestCode == 12) {
            if (Build.VERSION.SDK_INT >= 23) {
            }
        }

    }

    /**
     * 跳转Activity
     */
    private void switchActivity() {
       
    }

    /**
     * 判断悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e("399", " property: " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("399", "Below API 19 cannot invoke!");
        }
        return false;
    }


    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestPermission() {
        if (isFloatWindowOpAllowed(this)) {//已经开启
            switchActivity();
        } else {
            openSetting();
        }
    }


    /**
     * 打开权限设置界面
     */
    public void openSetting() {
        try {
            Intent localIntent = new Intent(
                    "miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", getPackageName());
            startActivityForResult(localIntent, 11);
            Log.e(TAG,"启动小米悬浮窗设置界面");
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent1.setData(uri);
            startActivityForResult(intent1, 11);
            Log.e(TAG,"启动悬浮窗界面");
        }

    }
}
