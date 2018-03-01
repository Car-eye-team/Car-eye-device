/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.version;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sh.camera.R;
import com.sh.camera.util.Constants;
import com.sh.camera.util.MyToast;
import com.sh.camera.util.ToastUtil;
public class OldVersionBiz {
	private static final String TAG = "VersionBiz";
	private Context context;
	private boolean versionflag = false;

	private VersionInfo versionInfo;
	private ProgressDialog pBar;
	public static final String UPDATE_APK_PATH = Environment.getExternalStorageDirectory().getPath()+"/apk/";

	public OldVersionBiz(Context context) {
		super();
		this.context = context;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			int what = msg.what;
			if(what == 101){
				doNewVersionUpdate();
			}else if(what == 102){
				doCurrentVersion();
			}else if(what == 1023){
				int nStep = msg.getData().getInt("step", -1);
				Log.e("", "step->" + nStep);
				if (nStep == -1){
					if (dlg!=null) {
						dlg.cancel();
					}
				}else{
					int ddsa =0;
					double nStepDou =nStep;
					double getPremaxIntDou =getPremaxInt;
					if (getPremaxInt != 0) {
						ddsa = (int) (( nStepDou /getPremaxIntDou) * 100);
					}

					pb_progressbar.setVisibility(View.VISIBLE);
					pb_progressbar.setProgress(nStep);
					tv_progressbar_r.setText(StringUtil.getDecimal(StringUtil.div(nStep, 1024*1024, 2))+"MB/"+StringUtil.getDecimal(StringUtil.div(getPremaxIntDou, 1024*1024, 2))+"MB");
				}
			}else if(what == 103){
				/** 网络连接超时*/
				MyToast.showToast(context, "服务器连接异常,请稍后再试！", true, 0);
			}else if(what == 104){
				/** json解析错误*/
				MyToast.showToast(context, "json解析错误", true, 0);
			}else if(what == 105){
				/** 数据请求失败原因*/
				String message = (String) msg.obj;
				if(message == null || message.equals("")){
					MyToast.showToast(context, "数据请求失败原因", true, 0);
				}
			}else if(what == 106){
				/** 网络连接超时*/
				MyToast.showToast(context, "网络连接超时", true, 0);
			}
		};
	};
	/**
	 * 获取版本名称
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public String getVersionName()throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	/**
	 * 获取版本�?
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public int getVersionCode() throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionCode;
	}

	/**
	 * 获取应用名称
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public String getAppName() {
		String verName = context.getResources().getText(R.string.app_name).toString();
		return verName;
	}

	/**
	 * 检查更新
	 * @param context
	 * @return
	 */
	public void doCheckVersion() {
		//判断网络是否连接 true 已连接 false 未连接
		boolean isConnected = NetworkUtil.getInstance().isConnect(context);
		if(isConnected){
			new AsyncTask<String, Void, Void>() {

				@Override
				protected Void doInBackground(String... params) {
					try {
						List<NameValuePair> pairs = new ArrayList<NameValuePair>();
						pairs.add(new BasicNameValuePair("ak", Constants.UPDATE_APK_AK));
						pairs.add(new BasicNameValuePair("type", Constants.UPDATE_APK_TYPE));

						HttpEntity httpEntity = HttpUtils.getEntity(Constants.BASE_URL+Constants.UPDATE_URL, pairs, HttpUtils.METHOD_POST);
						InputStream inputStream = HttpUtils.getStream(httpEntity);
						String json = StringParser.parse(inputStream);
						JSONObject obj = new JSONObject(json);
						int status = obj.getInt("status");
						if (status == 0) {
							String locVersion = getVersionName();
							versionInfo = new VersionInfo();
							versionInfo.setVersionId(obj.optString("versionId", ""));
							versionInfo.setVersionIndex(obj.optString("versionIndex", ""));
							versionInfo.setDownloadPath(obj.optString("uploadFile", ""));
							versionInfo.setCreateTime(obj.optString("createTime", ""));
							versionInfo.setDesc(Html.fromHtml(obj.optString("upgradecontent", "").replace("null", "")).toString() );
							String verion = versionInfo.getVersionIndex().replace("v", "");
							if(StringUtil.getVersion(locVersion, verion)){
								versionInfo.setType(1);
								handler.sendEmptyMessage(101);
							}else{
								if(versionflag){
									versionInfo.setType(2);
									handler.sendEmptyMessage(102);
								}
							}
						}else{
							String message = obj.getString("message");
							handler.obtainMessage(105, message).sendToTarget();
						}

					} catch (Exception e) {
					}
					return null;
				}
			}.execute();
		}else{
			handler.sendEmptyMessage(106);
		}

	}

	// 版本更新
	private void doNewVersionUpdate() {


		try {

			String desc = versionInfo.desc;
			showAlertApkPresssbar("系统升级","发现新版本 : ("+versionInfo.getVersionIndex()+")", desc, context);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//创建文件夹及文件  
	public File CreateText(String gg) throws IOException {  
		File file = new File(gg);  
		if (!file.exists()) {  
			try {  
				//按照指定的路径创建文件夹  
				file.mkdirs();  
			} catch (Exception e) {  
				// TODO: handle exception  
			}  
		}  
		File dir = new File(gg);  
		if (!dir.exists()) {  
			try {  
				//在指定的文件夹中创建文件  
				dir.createNewFile();  
			} catch (Exception e) {  
			}  
		}  

		return dir;

	} 
	String thisPahtApk ="";
	void downFile(final String path) {
		if (pBar !=null) {
			pBar.show();
		}
		new Thread() {
			@Override
			public void run() {

				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					pb_progressbar.setMax(conn.getContentLength());
					getPremaxInt = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File mFile= CreateText(UPDATE_APK_PATH);
					File file = new File(
							mFile,
							Constants.UPDATE_APK_NAME);


					FileOutputStream fos = new FileOutputStream(file);
					BufferedInputStream bis = new BufferedInputStream(is);
					byte[] buffer = new byte[1024];
					int len;
					int total = 0;
					while ((len = bis.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						total += len;
						Message msg = handler.obtainMessage();
						msg.getData().putInt("step", total);

						msg.what = 1023;
						handler.sendMessage(msg);
					}
					fos.flush();

					fos.close();
					bis.close();
					is.close();

					down();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	void down() {
		handler.post(new Runnable() {
			@Override
			public void run() {


				if (pBar != null) {
					pBar.cancel();
				}
				if (dlg!=null) {
					dlg.cancel();
				}
				update();
			}
		});
	}

	void update() {

		Intent updateIntent = new Intent("com.dss.car.launcher.ACTION_UPDATE_VERSION");
		updateIntent.putExtra("extra_apk_absolute_path", UPDATE_APK_PATH+Constants.UPDATE_APK_NAME);
		updateIntent.putExtra("extra_apk_type",  Constants.UPDATE_APK_TYPE);
		context.sendBroadcast(updateIntent);


		ToastUtil.longToast(context, "正在安装"+thisPahtApk);

		//		((Activity) context).finish();
	}

	private void doCurrentVersion() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("当前版本号  "+ getVersionName() +", 已是最新版本。");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		AlertDialogDiy.showAlertApkPresssbar(sb.toString(), "已是最新版本", "", context);
		String desc = versionInfo.desc;

		showAlertApkPresssbar("软件更新",sb.toString(), "已是最新版本", context);

	}


	private void showAlertApkPresssbar(final String vTitle, final String vTextZy ,final String newConten ,  final Context context) {
		//方案二  自定义  start
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		dlg.setCancelable(false);
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容

		window.setContentView(R.layout.public_phone_alert_ok);
		TextView tv_thisaler_ttitle = (TextView) window.findViewById(R.id.tv_thisaler_ttitle);
		TextView tv_thisaler_contenb = (TextView) window.findViewById(R.id.tv_thisaler_contenb);
		TextView tv_thisaler_contena1 = (TextView) window.findViewById(R.id.tv_thisaler_contena1);
		TextView tv_thisaler_contena2 = (TextView) window.findViewById(R.id.tv_thisaler_contena2);
		tv_thisaler_ttitle.setText(vTitle);
		// 关闭alert对话框架
		ImageView cancel = (ImageView) window.findViewById(R.id.btn_cancel);
		Button btn_ok = (Button) window.findViewById(R.id.btn_ok);
		Button cancalBu = (Button) window.findViewById(R.id.cancal);
		if ("系统升级".equals(vTitle)){
			if ("已是最新版本".equals(newConten)) {
				tv_thisaler_contena1.setVisibility(View.GONE);
				tv_thisaler_contena2.setVisibility(View.GONE);
				tv_thisaler_contenb.setText(vTextZy);
				cancalBu.setText("确定");
			}else {
				tv_thisaler_contenb.setVisibility(View.GONE);
				tv_thisaler_contena1.setText(vTextZy);
				String newContenN="是否现在升级?\n"+newConten;
				tv_thisaler_contena2.setText(newContenN);

			}
			btn_ok.setText("现在更新");
			tv_thisaler_ttitle.setText(getAppName()+"升级");
		}





		cancalBu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dlg.cancel();
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				dlg.cancel();
			}
		});

		btn_ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!"已是最新版本".equals(newConten)) {
					showAlertApkPresssbarLoad("", "", "", context);
				}

				dlg.cancel();
			}
		});
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK  
						&& event.getAction() == KeyEvent.ACTION_DOWN  
						&& event.getRepeatCount() == 0) {    
					dlg.cancel();
				}  

				// TODO Auto-generated method stub
				return false;
			}
		} );

	}
	AlertDialog dlg;
	private void showAlertApkPresssbarLoad(final String vTitle, final String vTextZy ,final String newConten ,  final Context context) {
		//方案二  自定义  start
		dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		dlg.setCancelable(false);
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		// 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.public_phone_alert_progressapknew);
		pb_progressbar = (ProgressBar) window.findViewById(R.id.pb_progressbar);
		tv_progressbar_r = (TextView) window.findViewById(R.id.tv_progressbar_r);
		tv_progressbar_l = (TextView) window.findViewById(R.id.tv_progressbar_l);
		tv_progressbar_l.setVisibility(View.INVISIBLE);
		downFile(versionInfo.downloadPath);

		// 关闭alert对话框架
		dlg.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK  
						&& event.getAction() == KeyEvent.ACTION_DOWN  
						&& event.getRepeatCount() == 0) {    

					dlg.cancel();
				}  

				// TODO Auto-generated method stub
				return false;
			}
		} );

	}
	private FrameLayout ll_main_fl_jiejia;
	private PopupWindow popupwindow;
	TextView tv_progressbar_l ;
	TextView tv_progressbar_r ;
	Button btn_cancel;
	TextView tv_zuobiao_text;
	ProgressBar pb_progressbar;
	int getPremaxInt = 0;
}


