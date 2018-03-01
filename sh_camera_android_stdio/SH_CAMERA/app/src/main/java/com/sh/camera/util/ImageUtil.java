/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**    
 *     
 * 项目名称：DSS_CAMERA    
 * 类名称：ImageUtil    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年10月20日 上午9:47:09    
 * 修改人：Administrator    
 * 修改时间：2016年10月20日 上午9:47:09    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class ImageUtil {
	
	/** 
     * 图片旋转 
     *  
     * @param bmp 
     *            要旋转的图片 
     * @param degree 
     *            图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转 
     * @return 
     */  
    public static Bitmap rotateBitmap(Bitmap bmp, float degree) {  
        Matrix matrix = new Matrix();  
        matrix.postRotate(degree);  
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);  
    }  
  
    /** 
     * 图片缩放 
     *  
     * @param bm 
     * @param scale 
     *            值小于则为缩小，否则为放大 
     * @return 
     */  
    public static Bitmap resizeBitmap(Bitmap bm, float scale) {  
        Matrix matrix = new Matrix();  
        matrix.postScale(scale, scale);  
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);  
    }  
  
    /** 
     * 图片缩放 
     *  
     * @param bm 
     * @param w 
     *            缩小或放大成的宽 
     * @param h 
     *            缩小或放大成的高 
     * @return 
     */  
    public static Bitmap resizeBitmap(Bitmap bm, int w, int h) {  
        Bitmap BitmapOrg = bm;  
  
        int width = BitmapOrg.getWidth();  
        int height = BitmapOrg.getHeight();  
  
        float scaleWidth = ((float) w) / width;  
        float scaleHeight = ((float) h) / height;  
  
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
        return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);  
    }  
  
    /** 
     * 图片反转 
     *  
     * @param bm 
     * @param flag 
     *            0为水平反转，1为垂直反转 
     * @return 
     */  
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {  
        float[] floats = null;  
        switch (flag) {  
        case 0: // 水平反转  
            floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };  
            break;  
        case 1: // 垂直反转  
            floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };  
            break;  
        }  
  
        if (floats != null) {  
            Matrix matrix = new Matrix();  
            matrix.setValues(floats);  
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);  
        }  
  
        return null;  
    }  
  
	
}
