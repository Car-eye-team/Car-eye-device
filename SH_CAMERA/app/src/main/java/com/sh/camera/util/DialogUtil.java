/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.sh.camera.service.MainService;

public class DialogUtil {

	private static Dialog currentDlg;

	/**
	 * 查询等待消息提示框
	 * 
	 * @param mcontext
	 * @param msgid
	 * @return
	 */
	public static void popProgress(final Context context, final String msg) {
		MainService.getInstance().handler.post(new Runnable() {

			@Override
			public void run() {
				ProgressDialog pd = new ProgressDialog(context);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				currentDlg = ProgressDialog.show(context, null, msg);
				currentDlg.setCancelable(true);
				currentDlg.setCanceledOnTouchOutside(false);
			}
		});
	}

	/**
	 * 关闭查询等待消息提示框
	 * 
	 * @param mcontext
	 * @param msgid
	 * @return
	 */
	private static boolean isCurrentDlgShowing() {
		return currentDlg != null && currentDlg.isShowing();
	}

	/**
	 * 关闭查询等待消息提示框
	 * 
	 * @param mcontext
	 * @param msgid
	 * @return
	 */
	public static void dismissCurrentDlg() {
		if (isCurrentDlgShowing()) {
			currentDlg.dismiss();
		}
	}
}
