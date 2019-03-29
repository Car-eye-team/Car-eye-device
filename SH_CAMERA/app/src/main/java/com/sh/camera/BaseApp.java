package com.sh.camera;

import android.app.Application;
import android.net.Uri;

import com.blankj.utilcode.util.Utils;
import com.sh.camera.faceRecognition12.FaceDB;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

/**
 * Created by xiePing on 2019/3/24 0024.
 * Description:
 */
public class BaseApp extends Application {
    public FaceDB mFaceDB;
    public Uri mImage;
    @Override
    public void onCreate() {
        super.onCreate();
        //blankj的三方工具类初始化
        Utils.init(this);
        Bugly.init(getApplicationContext(), "9c4b0e3ce3", false);
        Beta.checkUpgrade(false,false);
        mFaceDB = new FaceDB(this.getExternalCacheDir().getPath());
        mImage = null;
    }

    public void setCaptureImage(Uri uri) {
        mImage = uri;
    }

    public Uri getCaptureImage() {
        return mImage;
    }
}
