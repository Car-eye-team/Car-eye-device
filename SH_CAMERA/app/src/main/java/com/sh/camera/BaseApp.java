package com.sh.camera;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

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
        Bugly.init(getApplicationContext(), "9c4b0e3ce3", false);
        Beta.checkUpgrade(false,false);
    }
}
