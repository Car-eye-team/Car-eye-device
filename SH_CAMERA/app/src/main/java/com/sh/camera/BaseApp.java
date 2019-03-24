package com.sh.camera;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by xiePing on 2019/3/24 0024.
 * Description:
 */
public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //blankj的三方工具类初始化
        Utils.init(this);
    }
}
