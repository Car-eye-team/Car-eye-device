/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.version;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.sh.camera.R;
import com.sh.camera.SetActivity;
import com.sh.camera.bll.ParamsBiz;
import com.sh.camera.util.Constants;
import com.sh.camera.util.DialogUtil;
import com.sh.camera.util.MyToast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;

public class VersionBiz {

	private static final String TAG = "VersionBiz";
	private Context context;
	private boolean versionflag = false;
	private VersionInfo versionInfo;
	private ProgressDialog pBar;
	public static final String UPDATE_SAVENAME = "camera.apk";
	private boolean fromUpdateVersion;

	public VersionBiz(Context context) {
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
				Log.d("", "step->" + nStep);
				if (nStep == -1)
					pBar.dismiss();
				else
					pBar.setProgress(nStep);
			}else if(what == 103){
				/** 网络连接超时*/
				MyToast.showToast(context, "服务器连接异常,请稍后再试！", true, 0);
				MyToast.showToast(context, "网络连接超时", true, 0);
			}else if(what == 104){
				/** json解析错误*/
				MyToast.showToast(context, "json解析错误", true, 0);
			}else if(what == 105){
				/** 数据请求失败原因*/
				String message = (String) msg.obj;
				if(message == null || message.equals("")){
					MyToast.showToast(context, "数据请求失败原因:"+message, true, 0);
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
	public static String getVersionName(Context context)throws Exception {
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
	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}

	/**
	 * 检查更新
	 * @param context
	 * @return
	 */
	public void doCheckVersion(final boolean needDialog, boolean fromUpdateVersion) {
		this.fromUpdateVersion = fromUpdateVersion;
		if(needDialog){
			versionflag = true;
			DialogUtil.popProgress((Activity)context, "正在检测版本");
		}
		new AsyncTask<String, Void, Void>() {

			@Override
			protected Void doInBackground(String... params) {
				try {

					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					pairs.add(new BasicNameValuePair("ak", Constants.UPDATE_APK_AK));
					pairs.add(new BasicNameValuePair("type", Constants.UPDATE_APK_TYPE));
					String url = "http://"+ParamsBiz.getUpdateIp()+":"+ParamsBiz.getUpdatePort()+Constants.UPDATE_URL;
					HttpEntity httpEntity = HttpUtils.getEntity(url, pairs, HttpUtils.METHOD_POST);
					InputStream inputStream = HttpUtils.getStream(httpEntity);
					String json = StringParser.parse(inputStream);
					JSONObject obj = new JSONObject(json);
					int status = obj.getInt("status");
					if (status == 0) {
						String locVersion = getVersionName(context);
						versionInfo = new VersionInfo();
						versionInfo.setVersionId(obj.optString("versionId", ""));
						versionInfo.setVersionIndex(obj.optString("versionIndex", ""));
						versionInfo.setDownloadPath(obj.optString("uploadFile", ""));
						versionInfo.setCreateTime(obj.optString("createTime", ""));
						versionInfo.setDesc(Html.fromHtml(obj.optString("upgradecontent", ""))
								.toString());
						//							String verionid = versionInfo.getVersionId().toLowerCase();
						String verion = versionInfo.getVersionIndex().replace("v", "");
						if(StringUtil.getVersion(locVersion, verion)){
							//if (!("v"+locVersion).equals(verion)) {
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

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					if(needDialog){
						handler.sendEmptyMessage(104);
					}
					e.printStackTrace();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(needDialog){
						handler.sendEmptyMessage(103);
					}
					e.printStackTrace();
				} finally{
					DialogUtil.dismissCurrentDlg();
				}
				return null;
			}
		}.execute();

	}
	/**
	 * 检查更新
	 * @param context
	 * @return
	 */
	public static void doCheckVersionFirst(final Context context,final Handler handler) {
		new AsyncTask<String, Void, Void>() {

			@Override
			protected Void doInBackground(String... params) {
				try {

					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
					pairs.add(new BasicNameValuePair("ak", Constants.UPDATE_APK_AK));
					pairs.add(new BasicNameValuePair("type", Constants.UPDATE_APK_TYPE));
					String url = "http://"+ParamsBiz.getUpdateIp()+":"+ParamsBiz.getUpdatePort()+Constants.UPDATE_URL;
					HttpEntity httpEntity = HttpUtils.getEntity(url, pairs, HttpUtils.METHOD_POST);
					InputStream inputStream = HttpUtils.getStream(httpEntity);
					String json = StringParser.parse(inputStream);
					JSONObject obj = new JSONObject(json);
					int status = obj.getInt("status");
					if (status == 0) {
						String locVersion = getVersionName(context);
						String verion = obj.optString("versionIndex", "").replace("v", "");
						if(StringUtil.getVersion(locVersion, verion)){
							handler.obtainMessage(1022).sendToTarget();
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				return null;
			}
		}.execute();

	}

	// 版本更新
	private void doNewVersionUpdate() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("发现新版本 : "+versionInfo.getVersionId()+ "\n\n");
			String desc = versionInfo.desc;
			if(desc != null && desc.length() != 0 && !desc.equals("null")){
				sb.append("更新内容:" + "\n\n");
				sb.append(desc);
			}
			Dialog dialog = new AlertDialog.Builder(context)
			.setTitle("软件更新")
			.setMessage(sb.toString())
			// 设置内容
			.setPositiveButton("更新",// 设置确定按钮
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					pBar = new ProgressDialog(context);
					pBar.setCanceledOnTouchOutside(false);
					pBar.setTitle("正在下载");
					pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					downFile(versionInfo.downloadPath);
					//downFile("http://192.168.1.165/Dss_Driver.apk");
				}
			})
			.setNegativeButton("暂不更新",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int whichButton) {
					dialog.dismiss();
					if(fromUpdateVersion && SetActivity.instance != null){
						fromUpdateVersion = false;
						SetActivity.instance.finish();
					}
				}
			}).create();
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void downFile(final String path) {
		pBar.show();
		new Thread() {
			@Override
			public void run() {

				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					pBar.setMax(conn.getContentLength());

					InputStream is = conn.getInputStream();
					File file = new File(
							Environment.getExternalStorageDirectory(),
							UPDATE_SAVENAME);
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
				pBar.cancel();
				update();
			}
		});
	}

	void update() {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.fromFile(new File(
						Environment.getExternalStorageDirectory(),
						UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);

		/*SharedPreferences pre = context.getSharedPreferences(
				Constants.PREFS_NAME, context.MODE_PRIVATE);
		Editor edit = pre.edit();
		edit.putBoolean("firstTime", true);
		edit.commit();*/

//		((Activity) context).finish();
	}

	private void doCurrentVersion() {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("当前版本号  "+ getVersionName(context) +", 已是最新版本。");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Dialog dialog = new AlertDialog.Builder(context)
		.setTitle("软件更新").setMessage(sb.toString())
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).create();
		dialog.show();
	}
}
