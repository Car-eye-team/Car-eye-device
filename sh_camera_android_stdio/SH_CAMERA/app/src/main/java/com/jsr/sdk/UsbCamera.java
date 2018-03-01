/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.jsr.sdk;

import android.graphics.Bitmap;

public class UsbCamera {

	/**
     * Set the picture size, this method doesn't do any size check.
     * The size needs to be set before openCamera.
     *
     * @param width the width of the picture;
     *        height the height of the picture.
     */
	public native void setPictureSize(int width, int height);
	
	/**
     * Initialize the video device, connect the camera mounted as dev/video*.
     *
     * @param videoid id in dev/video*.
     * 
     * Returns -1 if fail to open usb camera.
     */
    public native int openCamera(int videoid);
    
    /**
     * Read data from device to keep the stream of camera source.
     *
     * Returns 1 OK; -2 means video device is lost.
     */
    public native int refreshFrame();
    
    /**
     * Stop the camera.
     */
    public native void stopCamera();
    
    /**
     * Get the data of a Bitmap from native.
     *
     * @param bitmap An object to inflate the image data.
     * 
     */
    public native void getBitmap(Bitmap bitmap);
    
    /**
     * Get the raw image as a byte array, the image format is NV12.
     *
     * @param yuvArray.
     * 
     * Returns 0 data not refresh; 1 OK.
     */
    public native int getRawFrame(byte[] yuvArray);
    
    static {
        System.loadLibrary("usbcamera");
    }
    
}
