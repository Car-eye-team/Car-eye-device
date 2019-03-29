package com.sh.camera.listener;

/**
 * Created by xiePing on 2019/3/27 0027.
 * Description:
 */
public interface OnCallbackListener<T> {
//    void onSuccess(T t);
//    void onFail(T t,String msg);
    void onCallback(T t);
}
