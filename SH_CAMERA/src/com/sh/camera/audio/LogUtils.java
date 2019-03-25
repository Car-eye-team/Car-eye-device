package com.sh.camera.audio;

import android.util.Log;

/**
 * Author : szmichaelYe
 * CreateDate : 2019/3/24  14:01
 * Email : haigand@163.com
 * Version : 1.0
 * Desc : 用于格式化输出
 * Modified :
 *
 *
 */


public class LogUtils {
    public final static String TAG = "CarEye";
    public static void v(String tag, String content) {
        Log.v(tag, content);
    }
    public static void d(String tag, String content) {
        Log.d(tag, content);
    }
    public static void e(String tag, String content) {
        Log.e(tag, content);
    }

    public static void w(String tag, String content) {
        Log.w(tag, content);
    }

    public static void v(String content) {
        Log.v(TAG, content);
    }
    public static void d(String content) {
        Log.d(TAG, content);
    }
    public static void e(String content) {
        Log.e(TAG, content);
    }

    public static void w(String content) {
        Log.w(TAG, content);
    }
}
