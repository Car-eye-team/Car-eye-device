/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.faceRecognition12.FaceRecognition12Activity;
import com.sh.camera.faceRecognition12.RegisterActivity;
import com.sh.camera.service.MainService;
import com.sh.camera.socket.CommCenterUsers;
import com.sh.camera.socket.utils.SPutil;
import com.sh.camera.util.Constants;
import com.sh.camera.util.Tools;
import com.sh.camera.version.VersionBiz;

import static com.sh.camera.faceRecognition12.FaceRecognition12Activity.getDataColumn;
import static com.sh.camera.faceRecognition12.FaceRecognition12Activity.isDownloadsDocument;
import static com.sh.camera.faceRecognition12.FaceRecognition12Activity.isExternalStorageDocument;
import static com.sh.camera.faceRecognition12.FaceRecognition12Activity.isMediaDocument;
import static org.push.hw.CodecManager.TAG;

public class SetActivity extends Activity implements OnClickListener {

	EditText et1,et2,et3,et4,et_editTextkzport;
	/**平台 */
	EditText et_ptserviceip ,et_ptserviceport;
	Button bt1,bt2;
	CheckBox[] cbs;
	CheckBox[] cbsmodel;
	int[] cbids = {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3, R.id.checkBox4};
	//int[] cbidsmodel = {R.id.radio2, R.id.radio3};
	RadioGroup rg, rg1;
	public static int[] rgids = {R.id.radio0, R.id.radio1};
	public static int[] rgids1 = {R.id.radio2, R.id.radio3};
	SharedPreferences sp;
	SharedPreferences.Editor sped;
	private TextView tv_version;

	/**是否有版本检测跳转至该界面*/
	private boolean fromUpdateVersion = false;
	public static SetActivity instance;


	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.set_activity);
		instance = this;
		tv_version = (TextView) findViewById(R.id.tv_version);
		et1 = (EditText) findViewById(R.id.set_editText1);
		et2 = (EditText) findViewById(R.id.set_editText2);
		///平台 EditText et_ptserviceip ,et_ptserviceport;
		 et_ptserviceip = (EditText) findViewById(R.id.et_ptserviceip);
		 et_ptserviceport = (EditText) findViewById(R.id.et_ptserviceport);
		et_editTextkzport = (EditText) findViewById(R.id.et_editTextkzport);
		et3 = (EditText) findViewById(R.id.set_editText3);
		et4 = (EditText) findViewById(R.id.set_editText4);
		bt1 = (Button) findViewById(R.id.set_button1);
		bt2 = (Button) findViewById(R.id.set_button2);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		rg1 = (RadioGroup) findViewById(R.id.radioGroup2);
		findViewById(R.id.btn_register_face).setOnClickListener(this);

		cbs = new CheckBox[4];
		for (int i = 0; i < cbs.length; i++) {
			cbs[i] = (CheckBox) findViewById(cbids[i]);
		}
		//模式
		//cbsmodel = new CheckBox[2];
		//for (int i = 0; i < cbsmodel.length; i++) {
		//	cbsmodel[i] = (CheckBox) findViewById(cbidsmodel[i]);
		//}

		sp = getSharedPreferences("fcoltest", MODE_PRIVATE);
		sped = sp.edit();

		rg.check(ServerManager.getInstance().getMode());
		if(ServerManager.getInstance().getprotocol()==Constants.CAREYE_RTP_PROTOCOL) {
			rg1.check(rgids1[0]);
		}else
		{
			rg1.check(rgids1[1]);
		}
		et1.setText(ServerManager.getInstance().getIp());
		et2.setText(ServerManager.getInstance().getPort());
		et3.setText(ServerManager.getInstance().getStreamname());
		et_editTextkzport.setText(ServerManager.getInstance().getAddport());
		
		//et4.setText(ServerManager.getInstance().getFramerate());
		et4.setText(sp.getString(Constants.fps, String.valueOf(Constants.FRAMERATE)));
		et_ptserviceip.setText(ServerManager.getInstance().getServiceIp());
		et_ptserviceport.setText(ServerManager.getInstance().getServicePort());

		String rulestr = ServerManager.getInstance().getRule();
		for (int i = 0; i < rulestr.length(); i++) {
			int id = Integer.parseInt(rulestr.substring(i, i+1));
			cbs[id].setChecked(true);
		}
		if(rg.getCheckedRadioButtonId()==rgids[0]){
			cbs[2].setChecked(false);
			cbs[3].setChecked(false);
			cbs[2].setVisibility(View.GONE);
			cbs[3].setVisibility(View.GONE);
		}else if(rg.getCheckedRadioButtonId()==rgids[1]){
			cbs[2].setVisibility(View.VISIBLE);
			cbs[3].setVisibility(View.VISIBLE);
		}
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup r, int i) {
				if(r.getCheckedRadioButtonId()==rgids[0]){
					cbs[2].setChecked(false);
					cbs[3].setChecked(false);
					cbs[2].setVisibility(View.GONE);
					cbs[3].setVisibility(View.GONE);
				}else if(r.getCheckedRadioButtonId()==rgids[1]){
					cbs[2].setVisibility(View.VISIBLE);
					cbs[3].setVisibility(View.VISIBLE);
				}
			}
		});


		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String rstr = "";
				for (int i = 0; i < cbs.length; i++) {
					if(cbs[i].isChecked()){
						rstr+=String.valueOf(i);
					}
				}
				sped.putString(Constants.rule, rstr);
				sped.putString(Constants.ip, et1.getText().toString());
				sped.putString(Constants.port, et2.getText().toString());
				sped.putString(Constants.name, et3.getText().toString());
				sped.putString(Constants.addPort, et_editTextkzport.getText().toString());
				sped.putString(Constants.fps, et4.getText().toString());
				sped.putInt(Constants.mode, rg.getCheckedRadioButtonId());
				if(rg1.getCheckedRadioButtonId() == rgids1[1]) {
					sped.putInt(Constants.protocol_type, Constants.CAREYE_RTMP_PROTOCOL);
				}else
				{
					sped.putInt(Constants.protocol_type, Constants.CAREYE_RTP_PROTOCOL);
				}
				//服务端ip端口
				sped.putString(Constants.PTSERVICE_IP, et_ptserviceip.getText().toString());
				sped.putString(Constants.PTSERVICE_PORT, et_ptserviceport.getText().toString());
				sped.commit();
				Editor commEditor = SPutil.getCommEditor();
				commEditor.putString("comm_terminal", et3.getText().toString());
				commEditor.putString("master_server_ip", et_ptserviceip.getText().toString());
				commEditor.putString("master_server_port", et_ptserviceport.getText().toString());
				commEditor.commit();
				//重启通讯
				CommCenterUsers.restartTimerConnectSvr();
				Intent intent = new Intent(MainService.ACTION);
				intent.putExtra("type", MainService.RESTART);
				sendBroadcast(intent);
				isSend = true;
				finish();
			}
		});

		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		VersionBiz v = new VersionBiz(this);
		String version = "";
		try {
			version = v.getVersionName(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv_version.setText("v"+version);
		
		fromUpdateVersion = (Boolean) getIntent().getBooleanExtra("fromUpdateVersion", false);
		v.doCheckVersion(false,fromUpdateVersion);

		/*boolean acc_uptaapp_init = new PrefBiz(this).getBooleanInfo("acc_uptaapp_init", false);
		try {
			if (acc_uptaapp_init) {
				VersionBiz v = new VersionBiz(this);
				v.doCheckVersion(true);
				new PrefBiz(this).putBooleanInfo("acc_uptaapp_init", false);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}

	/**
	 * 给设置端口 输入框赋值
	 * @param text
	 */
	private void setSetttingport(String text){
		et_editTextkzport.setText(text);
	}
	/**
	 * 获取设置端口 输入框赋值
	 * @param
	 */
	private String getSetttingPort(){
		return et_editTextkzport.getText().toString().trim();
	}
	private String getSettingPortPre(){

		String settingPort = sp.getString(Constants.addPort,Constants.SERVER_ADDPORT);
		return settingPort;
	}


	boolean isSend = false;
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		instance = null;
		if(!isSend){
			Intent intent = new Intent(MainService.ACTION);
			intent.putExtra("type", MainService.PASSWINFULL);
			sendBroadcast(intent);
			MainService.getInstance().openCamera(0,0,null);
			MainService.getInstance().reconnectCameras();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_register_face:
				showOpenCaptureOrAlbumDialog();
				break;
		}
	}

	/**
	 * 展示打开相机或相册的对话框
	 */
	private void showOpenCaptureOrAlbumDialog() {
		new AlertDialog.Builder(this,R.style.MyDialogStyle)
				.setTitle("请选择注册方式")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setItems(new String[]{"打开图片", "拍摄照片"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
							case 1:
								Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
								ContentValues values = new ContentValues(1);
								values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
								Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
								((BaseApp)(SetActivity.this.getApplicationContext())).setCaptureImage(uri);
								intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
								startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
								break;
							case 0:
								Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
								getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
								getImageByalbum.setType("image/jpeg");
								startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
								break;
							default:;
						}
					}
				})
				.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
			Uri mPath = data.getData();
			String file = getPath(mPath);
			Bitmap bmp = Tools.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "RESULT =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			String path = bundle.getString("imagePath");
			Log.i(TAG, "path="+path);
		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
			Uri mPath = ((BaseApp)(SetActivity.this.getApplicationContext())).getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = Tools.decodeImage(file);
			startRegister(bmp, file);
		}
	}

	/**
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(this, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}

					// TODO handle non-primary volumes
				} else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

					return getDataColumn(this, contentUri, null, null);
				} else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] {
							split[1]
					};

					return getDataColumn(this, contentUri, selection, selectionArgs);
				}
			}
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		String end = img_path.substring(img_path.length() - 4);
		if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
			return null;
		}
		return img_path;
	}

	/**
	 * @param mBitmap
	 */
	private void startRegister(Bitmap mBitmap, String file) {
		Intent it = new Intent(SetActivity.this, RegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}
}
