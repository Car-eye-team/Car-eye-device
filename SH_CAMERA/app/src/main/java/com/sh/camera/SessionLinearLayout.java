/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class SessionLinearLayout extends LinearLayout{
	
	private DispatchKeyEventListener mDispatchKeyEventListener;

	public SessionLinearLayout(Context context) {
		super(context);
	}

	public SessionLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SessionLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mDispatchKeyEventListener != null) {
			return mDispatchKeyEventListener.dispatchKeyEvent(event);
		}
		return super.dispatchKeyEvent(event);
	}

	public DispatchKeyEventListener getDispatchKeyEventListener() {
		return mDispatchKeyEventListener;
	}

	public void setDispatchKeyEventListener(DispatchKeyEventListener mDispatchKeyEventListener) {
		this.mDispatchKeyEventListener = mDispatchKeyEventListener;
	}

	//监听接口
	public static interface DispatchKeyEventListener {
		boolean dispatchKeyEvent(KeyEvent event);
	}
}
