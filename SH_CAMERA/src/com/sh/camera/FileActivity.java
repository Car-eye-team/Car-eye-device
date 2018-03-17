/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sh.camera.service.MainService;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.Constants;
import com.sh.camera.util.DateUtil;
import com.sh.camera.util.StringUtil;
import com.sh.camera.vedioPlay.MoviePlayer;

public class FileActivity extends Activity {

	private LayoutInflater inflater;
	private ListView lv;
	private LinearLayout ly1,ly2,ly3;
	private ImageView iv;
	private TextView tv1,tv2;
	private MyAd ad;
	private Builder b;
	private AlertDialog dialog;
	private boolean isallcheck = false;
	private ArrayList<String> mFileList;
	private TextView tv_time;

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1001){
				ad.notifyDataSetChanged();
			}
			if(msg.what==1002){
				iv.setImageResource(R.drawable.bs1down);
				handler.sendMessage(handler.obtainMessage(1001));
			}
			if(msg.what==1003){
				initData();
			}
			if(msg.what==1004){
				iv.setImageResource(R.drawable.bs1up);
				handler.sendMessage(handler.obtainMessage(1001));
			}
			if(msg.what==1005){
				//点击上传处理事件
				int index = (Integer) msg.obj;
				HashMap<String, String> map = data.get(index);
				String filename = map.get("name");
				String abs_name = map.get("path");
				Log.d("CMD", " filePath name"+filename);
				int cameraid = Integer.parseInt(filename.split("-")[0]);
				CameraUtil.startVideoFileStream(cameraid, 0, 0, abs_name,handler);
				b = new Builder(FileActivity.this);
				b.setView(inflater.inflate(R.layout.dialog_sc, null));
				b.setPositiveButton("停止", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//点击停止结束上传
						CameraUtil.stopVideoFileStream();
					}
				});
				dialog = b.create();
				dialog.show();
				
			}
			if(msg.what==1006){
				dialog.dismiss();
			}
		};
	};

	private String sTime;// 开始时间
	private String eTime;// 结束时间
	private int camera = 0;//0全部，1234摄像头筛选
	private int type = 0;//0全部，1照片，2录像

	private ArrayList<HashMap<String, String>> data;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file);
		inflater = LayoutInflater.from(this);
		initView();
		initData();

	}

	private void initView() {
		lv = (ListView) findViewById(R.id.huifang_listView1);
		ly1 = (LinearLayout) findViewById(R.id.huifang_lybt1);
		ly2 = (LinearLayout) findViewById(R.id.huifang_lybt2);
		ly3 = (LinearLayout) findViewById(R.id.huifang_lybt3);
		iv = (ImageView) findViewById(R.id.huifang_bsiv);
		tv1 = (TextView) findViewById(R.id.huifang_tv_1);
		tv2 = (TextView) findViewById(R.id.huifang_tv_2);
		tv_time = (TextView) findViewById(R.id.tv_time);

		ly1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(isallcheck){
					isallcheck = false;
					for (int i = 0; i < data.size(); i++) {
						data.get(i).put("click", "0");
					}
					handler.sendMessage(handler.obtainMessage(1004));
				}else{
					isallcheck = true;
					for (int i = 0; i < data.size(); i++) {
						data.get(i).put("click", "1");
					}
					handler.sendMessage(handler.obtainMessage(1002));
				}
			}
		});

		ly2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ArrayList<Integer> ds = new ArrayList<Integer>();
				for (int i = 0; i < data.size(); i++) {
					if(data.get(i).get("click").equals("1")){
						try {
							File f = new File(data.get(i).get("path"));
							if(f.exists()){
								f.delete();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				handler.sendMessage(handler.obtainMessage(1003));
			}
		});

		ly3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SelectTimeActivity.calendarSTCache = null;
				SelectTimeActivity.calendarETCache = null;
				finish();
			}
		});

		tv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] items = {"全部","摄像头一","摄像头二","摄像头三","摄像头四"};
				AlertDialog.Builder buil = new AlertDialog.Builder(FileActivity.this);
				buil.setTitle("选择文件来源");
				buil.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int i) {
						camera = i;
						initData();
						if(i==0){
							tv1.setText("全部");
						}else{
							tv1.setText("摄像头-"+i);
						}
					}
				});
				buil.show();
			}
		});

		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] items = {"全部","照片","录像"};
				AlertDialog.Builder buil = new AlertDialog.Builder(FileActivity.this);
				buil.setTitle("选择文件类型");
				buil.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int i) {
						type = i;
						initData();
						switch (i) {
						case 0:
							tv2.setText("全部");
							break;
						case 1:
							tv2.setText("照片");
							break;
						case 2:
							tv2.setText("录像");
							break;
						}
					}
				});
				buil.show();
			}
		});

		tv_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(FileActivity.this, SelectTimeActivity.class),1001);
			}
		});
	}

	private void initData() {
		data = new ArrayList<HashMap<String, String>>();
		mFileList =new ArrayList<String>();

		for(int j = 0; j < MainService.disk.getDiskCnt(); j++)
		{
			String FileName;
			FileName = MainService.disk.getDiskDirectory(j)+Constants.CAMERA_FILE_DIR;
			//File f = new File(Constants.CAMERA_FILE_PATH);
			File f = new File(FileName);
			if(f.exists()){
				File[] fs = f.listFiles();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(fs!=null&&fs.length>0){
					for (int i = 0; i < fs.length; i++) {

						//Log.d("CMD", "file dirtionary:"+fs[i].getAbsolutePath());

						String name = fs[i].getName();		
						if(!name.endsWith("jpg") && !name.endsWith("mp4"))
							continue;
						if(camera==0||name.subSequence(0, 1).equals(String.valueOf(camera))){
							if(type==0||(type==1&&name.endsWith("jpg"))||(type==2&&name.endsWith("mp4"))){
								File file = fs[i];
								// 是否在时间范围内
								boolean inTime = true;
								if(!StringUtil.isNull(sTime) && !StringUtil.isNull(eTime)){
									inTime = DateUtil.isBetweenDate1AndDate2(sTime, eTime, new Date(file.lastModified()));
								}
								if(inTime){
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("name", name);
									map.put("time", sdf.format(new Date(file.lastModified())));
									map.put("sord", String.valueOf(file.lastModified()));
									map.put("path", file.getAbsolutePath());
									mFileList.add(file.getAbsolutePath());
									map.put("type", name.substring(name.length()-3, name.length()));
									double filesize = ((file.length()/1024)*1.00/1024);
									filesize =getDecimalThree(filesize);
									map.put("size", filesize+" M");
									map.put("click", "0");
									data.add(map);
								}
							}
						}
					}			

				}		

			}	
		}

		if(data.size() == 0)
		{
			nofile();
		}else
		{
			for (int i = 0; i < data.size()-1; i++) {
				for (int j = i+1; j < data.size(); j++) {
					if(Long.parseLong(data.get(i).get("sord"))>Long.parseLong(data.get(j).get("sord"))){
						HashMap<String, String> map = data.get(i);
						data.set(i, data.get(j));
						data.set(j, map);
					}
				}
			}
			Collections.reverse(data);
		}

		ad = new MyAd();
		lv.setAdapter(ad);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int i, long l) {
				if(data.get(i).get("type").equals("mp4")){
					openmp4(i);
				}else{
					openjpg(i);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent(MainService.ACTION);
		intent.putExtra("type", MainService.PASSWINFULL);
		sendBroadcast(intent);
	}


	void openmp4(int i){

		if(Constants.ExtPlayer==true)
		{
			Intent intent = new Intent(Intent.ACTION_VIEW); 
			Uri uri = Uri.parse("file://"+data.get(i).get("path")); 
			Log.d("PLAY","file uri= "+uri);
			intent.setDataAndType(uri, "video/*"); 
			startActivity(intent);
		}else
		{		
			Intent movieIntent = new Intent();
			movieIntent.putExtra("uri",(Uri.parse("file://"+data.get(i).get("path"))).toString());
			movieIntent.putStringArrayListExtra("filelist",mFileList);
			//Log.d(TAG,"filelist = "+mFileList);
			movieIntent.setClass(this,MoviePlayer.class); 	    	
			startActivity(movieIntent);
		}


	}
	void openjpg(int i){
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		Uri uri = Uri.parse("file://"+data.get(i).get("path")); 
		intent.setDataAndType(uri, "image/*"); 
		startActivity(intent);
	}


	void nofile(){
		Toast.makeText(FileActivity.this, "摄像头文件不存在", 2000).show();
		finish();
	}


	class MyAd extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int i, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(v==null){
				v = inflater.inflate(R.layout.item_huifang, null);
				holder = new ViewHolder();
				holder.tv1 = (TextView) v.findViewById(R.id.huifang_item_textView1);
				holder.tv2 = (TextView) v.findViewById(R.id.huifang_item_textView2);
				holder.tv3 = (TextView) v.findViewById(R.id.huifang_item_textView3);
				holder.ly = (LinearLayout) v.findViewById(R.id.huifang_item_ly);
				holder.ly2 = (LinearLayout) v.findViewById(R.id.huifang_item_ly_sc);
				holder.iv = (ImageView) v.findViewById(R.id.huifang_item_imageView1);
				v.setTag(holder);
			}else{
				holder = (ViewHolder) v.getTag();
			}
			HashMap<String, String> map = data.get(i);
			holder.tv1.setText(map.get("name"));
			holder.tv2.setText(map.get("size"));
			holder.tv3.setText(map.get("time"));
			if(map.get("click").equals("1")){
				holder.iv.setImageResource(R.drawable.bs1down);
			}else{
				holder.iv.setImageResource(R.drawable.bs1up);
			}
			final int index = i;
			holder.ly.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(data.get(index).get("click").equals("1")){
						data.get(index).put("click", "0");
					}else{
						data.get(index).put("click", "1");
					}
					handler.sendMessage(handler.obtainMessage(1001));
				}
			});
			holder.ly2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					handler.sendMessage(handler.obtainMessage(1005, index));
				}
			});
			return v;
		}

	}


	class ViewHolder{
		TextView tv1,tv2,tv3;
		LinearLayout ly,ly2;
		ImageView iv;
	}
	public   double getDecimalThree(double num) {
		double numStr =0.0;
		DecimalFormat df = new DecimalFormat();
		double d = 123.9078;
		String db = df.format(d);
		//则db=123.90;
		if (Double.isNaN(num)) {
			return 0.0;
		}
		BigDecimal bd = new BigDecimal(num);
		num = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		numStr =  num;

		return numStr;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1001 && resultCode == Activity.RESULT_OK){
			//时间格式：yyyy-MM-dd HH:mm:ss
			sTime = data.getStringExtra("stime");
			eTime = data.getStringExtra("etime");
			initData();
		}
	}
}
