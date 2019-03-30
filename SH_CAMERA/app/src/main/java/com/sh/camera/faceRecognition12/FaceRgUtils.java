package com.sh.camera.faceRecognition12;

import android.app.Activity;
import android.app.ProgressDialog;

import com.blankj.utilcode.util.Utils;
import com.sh.camera.BaseApp;

/**
 * Created by xiePing on 2019/3/30 0030.
 * Description:
 */
public class FaceRgUtils {
    /**
     * 载入人脸识别数据
     *
     */
    public static void loadFaceData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                BaseApp app = (BaseApp) Utils.getApp();
                app.mFaceDB.loadFaces();
            }
        }).start();
    }
}
