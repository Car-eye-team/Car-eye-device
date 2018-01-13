/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

import com.sh.camera.ServerManager.ServerManager;
import com.sh.camera.util.CameraFileUtil;
import com.sh.camera.util.CameraUtil;
import com.sh.camera.util.Constants;
import com.sh.camera.util.DateUtil;


public class CommandService extends Service  {
    private static final String TAG = "CMD";
    private static final int STATE_CONNECTING = 2;
    private static final String ACTION_COMMOND_STATE_CHANGED = "state-changed";
    private static final String ACTION_COMMOND_KEEPALIVE = "keep-alive";
    public static final String ACTION_COMMOND_RESTART_THREAD = "restart-thread";
    public static final String ACTION_SNAPSHOT_REQ = "action-snapshot-request";
    private static final String KEY_STATE = "key-state";
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECT_ERROR = -1;
    public static final String ACTION_START_STREAM = "action_start_stream";
    public static final String ACTION_STOP_STREAM = "action_stop_stream";
    private Socket mSoc;
    private InputStreamReader mIS;
    private OutputStreamWriter mOS;
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver mReceiver_DVR;	 
    private int mSeq = 100000;
    private SparseArray<String> mResp = new SparseArray<String>();
    private static final int WAIT_RESPONSE_TIMEOUT_VALUE = 10;
    private static final int WAIT_RESTART_TIMEOUT_VALUE = 15;
    private  static CommandService instance;	
    
    public static  CommandService getInstance() {
		
		  if (instance == null) {
				instance = new CommandService();
			}
			return instance;
		}	   
   
    
    private static String readLine( InputStreamReader is,  int contentLe) throws IOException {
        ArrayList lineByteList = new ArrayList();
        int total = 0x0;  
        int count =0; 
        int timeout = 0;	             
        if(contentLe != 0) {
        	for(;;)
        	{
        		int value = is.read();
        		if(value!= -1)
        		{
		            byte readByte = (byte)value;
		            lineByteList.add(Byte.valueOf(readByte));
		            total = total + 0x1;
		            if(total >= contentLe) {
		            	break;
		            }
        		}else
        		{
        			try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			return null;
        		}
	            
        	}
        } else {
        	for(;;)
        	{
        		int value = is.read();
        		if(value!= -1)
        		{
        		    byte readByte = (byte)value;			            
		            lineByteList.add(Byte.valueOf(readByte));
		            if(readByte == 0xa) {
		            	break;
		            }
        		}else
        		{
        			try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			return null;
        		}
        	}      
           
        }
       
        byte[] tmpByteArr = new byte[lineByteList.size()];
        for(int i = 0x0; i < lineByteList.size(); i = i + 0x1) {
            tmpByteArr[i] = ((Byte)lineByteList.get(i)).byteValue();
        }
        lineByteList.clear();        
        return new String(tmpByteArr, "UTF-8");       
    }

    private String SendNVRRegister(String name , String serialno, String tag, String key, int seq, int channelcount )
    {  	  
  	  String str;
  	  str = String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n   " +
  	  		"   \"Name\": \"%s\",\n      \"Serial\": \"%s\",\n   " +
  	  		"   \"Tag\": \"%s\",\n      \"Token\": \"%s\",\n      \"ChannelCount\": \"4\",\n       \"Channels\":\n       [\n        {\n         \"Channel\": \"1\",\n         \"Name\": \"chanel01\",\n         \"Status\": \"online\"\n        },\n        {\n         \"Channel\": \"2\",\n         \"Name\": \"chanel02\",\n         \"Status\": \"online\"\n        },\n        {\n         \"Channel\": \"3\",\n         \"Name\": \"chanel03\",\n         \"Status\": \"online\"\n        },\n        {\n         \"Channel\": \"4\",\n         \"Name\": \"chanel04\",\n         \"Status\": \"online\"\n        }\n      ]\n     },\n " +
  	  		"   \"Header\": {\n      \"AppType\": \"EasyNVR\",\n     " +
  	  		" \"CSeq\": \"%d\",\n      \"MessageType\": \"MSG_DS_REGISTER_REQ\",\n   " +
  	  		"   \"TerminalType\": \"ARM_Linux\",\n      \"Version\": \"1.0\"\n    }\n  }\n}",
  	  		new Object[] { name, serialno, tag, key,  seq });     	
  	 
  	  return str;
  	  
    }
    /**********************************/
    
    /* Test code for play media			*/
    
    /*********************************/      

   
    
    public void TestPlayer()
    {        	
    	  String Name;
		  Name = "/mnt/extsd1/CarDVR/1-1499240728475.mp4";
		  int start, end;
		  start =  0;
		  end = 100;
		  Object[] arrayOfObject6 = new Object[7];
		  arrayOfObject6[0] = 1;
   		  arrayOfObject6[1] = "dsdsdds";
          arrayOfObject6[2] = "dsdsdds";
          arrayOfObject6[3] = 123;
          arrayOfObject6[4] = "FDfd";
          arrayOfObject6[5] = "FDfd";
          arrayOfObject6[6] = 1;    			  
 		  File picture;            	
 		  String data;
          picture = new File(Name);	            				
	      if(picture.exists())
		  {
	    	  Intent Intent1 = new Intent(Constants.ACTION_VIDEO_FILE_PLAYBACK);
	    	  Intent1.putExtra("Channel", 1);
	    	  Intent1.putExtra("Name", Name);
	    	  Intent1.putExtra("Start", start);
	    	  Intent1.putExtra("End", end);
	    	  sendBroadcast(Intent1);  			    	  
	    	  data =  buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\"\n    },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"200\",\n      \"ErrorString\": \"Success OK\",\n      \"MessageType\": \"MSG_DS_START_RECORD_PLAY_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));
	           		
		  } else
		  {
		  	  data = buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\"\n    },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"404\",\n      \"ErrorString\": \"Not Find\",\n      \"MessageType\": \"MSG_DS_START_RECORD_PLAY_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));	
		  }			  
           			      
    }   
    public class BlockCommandThread extends Thread {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Intent i = new Intent(ACTION_COMMOND_STATE_CHANGED);
            i.putExtra(KEY_STATE, STATE_CONNECTING);
            LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(i);           
            try {
                doRun();
            } catch (Exception e) {        
                e.printStackTrace();
                Intent intent = new Intent(ACTION_COMMOND_STATE_CHANGED);
                intent.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(intent);
            }
        }

        private String requestAndResp(String requestJson, int seq) throws IOException, InterruptedException {
            String sb = buildOutStreamContent(requestJson);
            mResp.put(seq, null);
            if(mOS!= null)
            {
	            synchronized (mOS) {
	                if (mOS == null) throw new IOException("channel not valid.");
	                mOS.write(sb);
	                mOS.flush();
	            }
            }
            
          /*  synchronized (mResp) {
                int totalMillis = WAIT_RESPONSE_TIMEOUT_VALUE * 1000;
                long timeBegin = System.currentTimeMillis();
                String resp = "";
                while (mReadThread != null && mResp.get(seq) == null) {
                    long waitedMillis = System.currentTimeMillis() - timeBegin;
                    long leftMillis = totalMillis - waitedMillis;
                    if (leftMillis > 0) {
                        mResp.wait(leftMillis);
                    }
                }
                resp = mResp.get(seq);
                mResp.remove(seq);
                return resp;
            }
        }*/
            synchronized (mResp) {               
                int count = 0;
                String resp = null;     
                while (mReadThread != null ) {
                  resp = mResp.get(seq);       
                  if (((resp != null)&&resp!="") || count > WAIT_RESPONSE_TIMEOUT_VALUE)
          	              break;   
                  mResp.wait(1000);
                  count++;                        
                }                
                mResp.remove(seq);
                return resp;
            }
        }     
                 

        private void doRun() throws IOException, JSONException {
            String info = "";                
            String ip = ServerManager.getInstance().getIp();
            String port = ServerManager.getInstance().getAddport();
            String name = ServerManager.getInstance().getDevicename();
            String serial =  ServerManager.getInstance().getStreamname();
            String tag =  ServerManager.getInstance().getDeviceTag();
            String token =  ServerManager.getInstance().getDeviceKey();
            InetAddress inetAddress = Inet4Address.getByName(ip);
            byte[] addr = inetAddress.getAddress();
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < addr.length; j++) {
                short sh = (short) (addr[j] >= 0 ? addr[j] : 256 + addr[j]);
                sb.append(sh);
                if (j != addr.length - 1) {
                    sb.append('.');
                }
            }
            ip = sb.toString();
            int nPort = Integer.parseInt(port);
            Socket s = null;
            int retryCount = 0;
            while (mReadThread != null && s == null) {
                try {
                    Socket tempS = new Socket();
                    tempS.connect(new InetSocketAddress(ip, nPort), 10000);
                    s = tempS;
                } catch (Exception e) {
                    try {
                        retryCount++;
                        Thread.sleep(1000);
                        Log.d(TAG, "kim retryCount=" + retryCount);
                        if (retryCount == 1) {
      
                        }
                    } catch (Exception e1) {
                        //
                    }
                }
            }

            Log.d(TAG, "kim Connect server success");
            mSoc = s;
            s.setSoTimeout(0);
            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());                          
            String registerJson = buildOutStreamContent( SendNVRRegister(name, serial, tag,token,1, 4));          
            osw.write(registerJson);
            osw.flush();
            //--输出服务器传回的消息的头信息
            InputStreamReader is = new InputStreamReader(s.getInputStream());
            mIS = is;
            String line = null;
            int contentLength = 0;//服务器发送回来的消息长度
            // 读取所有服务器发送过来的请求参数头部信息
            do {
                line = readLine(is, 0);
                //如果有Content-Length消息头时取出
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
                //打印请求部信息
                System.out.print(line);
                if (line.startsWith("HTTP/1.1")) {
                    if (line.equals("HTTP/1.1 200 OK\r\n") == false) {
                     
                        Intent i = new Intent(ACTION_COMMOND_STATE_CHANGED);
                        i.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                        LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(i);
                        return;
                    }
                }
                //如果遇到了一个单独的回车换行，则表示请求头结束
            } while (!line.equals("\r\n"));
            Log.d(TAG, "register sucssess");            
            //--输消息的体
            final String str = readLine(is, contentLength);
            if (str.indexOf("\"ErrorNum\" : \"200\"") < 0) {   
                Intent i = new Intent(ACTION_COMMOND_STATE_CHANGED);
                i.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(i);
                return;
            } else {
                mIS = is;
                mOS = osw;         
                Intent i = new Intent(ACTION_COMMOND_STATE_CHANGED);
                i.putExtra(KEY_STATE, STATE_CONNECTED);
                LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(i);
		           try
		  	      {
		  	        Thread.sleep(5000);
		  	      }catch (Exception localException)
		  	      {        
		  	          
		  	      }
                Intent localIntentsnap = new Intent(ACTION_SNAPSHOT_REQ);
                localIntentsnap.putExtra("Channel", 0);
                LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(localIntentsnap);
	          // MainService.TakePictureAll(0);            
            }
            Log.d(TAG, String.format(" register result:\n%s", str));
            while (mReadThread != null) {
                do {
                    line = readLine(mIS, 0);
                    //如果有Content-Length消息头时取出
                    if (line.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                    Log.d(TAG, line);
                } while (!line.equals("\r\n"));

                //--输消息的体
                String resp = readLine(mIS, contentLength);
                Log.d(TAG, resp);
                JSONObject receivedJson = new JSONObject(resp);
                final JSONObject easyDarwin = receivedJson.getJSONObject("EasyDarwin");
                final JSONObject body = easyDarwin.getJSONObject("Body");
                final JSONObject header = easyDarwin.getJSONObject("Header");
                int seq = header.getInt("CSeq");
                if (mResp.indexOfKey(seq) != -1) {
                    mResp.put(seq, resp);
                    synchronized (mResp) {
                        mResp.notifyAll();
                    }
                } else {
                    if ("MSG_SD_PUSH_STREAM_REQ".equals(header.get("MessageType"))) {  // 请求视频
                        final Intent intent = new Intent(ACTION_START_STREAM);
                        intent.putExtra("Server_IP", body.getString("Server_IP"));
                        intent.putExtra("Channel", body.getString("Channel"));
                        intent.putExtra("Server_PORT", body.getString("Server_PORT"));
                        intent.putExtra("Serial", body.getString("Serial"));
                        intent.putExtra("body", body.toString());
                        intent.putExtra("seq", seq);                     
                        int i1;
                        LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(intent);               
            
                    } else if ("MSG_SD_STREAM_STOP_REQ".equals(header.get("MessageType"))) {  // 终止视频
                     
                    	Intent localIntent2 = new Intent(ACTION_STOP_STREAM);
                    	localIntent2.putExtra("Channel", body.getString("Channel"));
                    	LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(localIntent2);
                        String respStr = String.format("{\n" +
                                "  \"EasyDarwin\": {\n" +
                                "    \"Body\": {\n" +
                                "      \"Channel\": \"%s\",\n" +
                                "      \"From\" : \"%s\",\n" +
                                "      \"Protocol\": \"%s\",\n" +
                                "      \"Serial\": \"%s\",\n" +
                                "      \"To\" : \"%s\",\n" +
                                "      \"Via\" : \"%s\"\n" +
                                "    },\n" +
                                "    \"Header\": {\n" +
                                "      \"CSeq\": \"%d\",\n" +
                                "      \"ErrorNum\": \"200\",\n" +
                                "      \"ErrorString\": \"Success OK\",\n" +
                                "      \"MessageType\": \"MSG_DS_STREAM_STOP_ACK\",\n" +
                                "      \"Version\": \"1.0\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}", body.get("Channel"), body.get("To"), body.get("Protocol"), body.get("Serial"), body.get("From"), body.get("Via"), seq);
                        respStr = buildOutStreamContent(respStr); 
                        Log.d(TAG, respStr);
                        synchronized (mOS) {
                            mOS.write(respStr);
                            mOS.flush();
                        }
                    }else if("MSG_SD_GET_RECORD_LIST_REQ".equals(header.get("MessageType")))
          		    { 	  
          	      	   	String starttime;
	         			String endtime;
	         			String Redult ="";
	         			int count;
	         			starttime=DateUtil.dateToNumber(body.getString("StartTime"));
                        endtime = DateUtil.dateToNumber(body.getString("EndTime"));  
                        int channel = body.getInt("Channel");                  
                        Redult = CameraFileUtil.screenVideoFile(starttime, endtime, channel);
                        Log.d("CMD", starttime);
                        if(Redult != null && !Redult.equals("")) 
                        {
                      	    Object[] arrayOfObject6 = new Object[9];
                            arrayOfObject6[0] = body.get("Channel");
                            arrayOfObject6[1] = body.get("To");
                            arrayOfObject6[2] = body.get("Protocol");                            
                            arrayOfObject6[3] = body.get("Serial");
                            arrayOfObject6[4] = body.get("From");
                            arrayOfObject6[5] = body.get("Via");
                            arrayOfObject6[6] = 1;
                            arrayOfObject6[7] = Redult;
                            arrayOfObject6[8] = Integer.valueOf(seq);
                            String str28 = buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\",\n    \"FileCount\" : \"%d\",\n   \"FileList\" :\n      [\n    %s\n     ]\n   },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"200\",\n      \"ErrorString\": \"Success OK\",\n      \"MessageType\": \"MSG_DS_GET_RECORD_LIST_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));
                            synchronized (CommandService.this.mOS)
                            {
                              CommandService.this.mOS.write(str28);
                              CommandService.this.mOS.flush();
                            }
                            Log.d("CMD", str28);
                        }else
                        {
                        	  Object[] arrayOfObject6 = new Object[9];
                              arrayOfObject6[0] = body.get("Channel");
                              arrayOfObject6[1] = body.get("To");
                              arrayOfObject6[2] = body.get("Protocol");                            
                              arrayOfObject6[3] = body.get("Serial");
                              arrayOfObject6[4] = body.get("From");
                              arrayOfObject6[5] = body.get("Via");
                              arrayOfObject6[6] = 0;
                              arrayOfObject6[7] = "";
                              arrayOfObject6[8] = Integer.valueOf(seq);
                             String str28 = buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\",\n    \"FileCount\" : \"%d\",\n   \"FileList\" :\n      [\n    %s\n     ]\n   },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"200\",\n      \"ErrorString\": \"Success OK\",\n      \"MessageType\": \"MSG_DS_GET_RECORD_LIST_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));
                             synchronized (CommandService.this.mOS)
                             {
                               CommandService.this.mOS.write(str28);
                               CommandService.this.mOS.flush();
                             }
                             Log.d("CMD", str28);                        	
                        }
                           
           		  }else if("MSG_SD_START_RECORD_PLAY_REQ".equals(header.get("MessageType")))    		  {
           		  
           			  String Name;
           			  String URL;
           			  String ipstream;
          			  Name = body.getString("Name");
          			  int start, end;
          			  start = Integer.valueOf(body.getInt("StartTime"));
          			  end = Integer.valueOf(body.getInt("EndTime"));
          			  ipstream =  ServerManager.getInstance().getPort();
          			  URL = String.format("rtsp://%s:%s/%s-channel=%s.sdp", ip, ipstream, serial, body.get("Channel"));
          			  Object[] arrayOfObject6 = new Object[9];
          			  arrayOfObject6[0] = body.get("Channel");
       	       		  arrayOfObject6[1] = body.get("To");
       	              arrayOfObject6[2] = body.get("Protocol");
       	              arrayOfObject6[3] = body.get("Serial");
       	              arrayOfObject6[4] = body.get("From");
       	              arrayOfObject6[5] = body.get("Via");
       	              arrayOfObject6[6] = body.get("Name");
       	              arrayOfObject6[7] = URL;
       	              arrayOfObject6[8] = Integer.valueOf(seq);       			  
              		  File picture;            	
              		  String data;
       		          picture = new File(Name);	
	       		
       		         
       			      if(picture.exists())
       				  {
       			    	  Intent Intent1 = new Intent(Constants.ACTION_VIDEO_FILE_PLAYBACK);
       			    	  Intent1.putExtra("Channel", body.getInt("Channel"));
       			    	  Intent1.putExtra("Name", Name);
       			    	  Intent1.putExtra("Start", start);
       			    	  Intent1.putExtra("End", end);
       			    	  sendBroadcast(Intent1);       			    	  
       			    	  data =  buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\",\n    \"Name\" : \"%s\",\n   \"URL\" : \"%s\"\n  },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"200\",\n      \"ErrorString\": \"Success OK\",\n      \"MessageType\": \"MSG_DS_START_RECORD_PLAY_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));
       			    		
       				  } else
       				  {
       				  	  data = buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\",\n     \"Name\" : \"%s\",\n  \"URL\" : \"%s\"\n   },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"404\",\n      \"ErrorString\": \"Not Find\",\n      \"MessageType\": \"MSG_DS_START_RECORD_PLAY_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));	
       				  }	
       			      synchronized (CommandService.this.mOS)
                      {
                           CommandService.this.mOS.write(data);
                           CommandService.this.mOS.flush();
                      }
                      Log.d("CMD", data);       	
                      
              			 
           		  } else if ("MSG_SD_STOP_RECORD_PLAY_REQ".equals(header.get("MessageType"))) 
           		  {
           			     Intent Intent1 = new Intent(Constants.ACTION_VIDEO_STOP_PLAYBACK);       		
                         Intent1.putExtra("EXTRA_ID", body.getInt("Channel")); 
                         //需要存储其中session from client                  
                         sendBroadcast(Intent1);      
                         // return ACK       
                         Object[] arrayOfObject6 = new Object[7];
                         String data;
                         arrayOfObject6[0] = body.get("Channel");
                         arrayOfObject6[1] = body.get("To");
	       	              arrayOfObject6[2] = body.get("Protocol");
	       	              arrayOfObject6[3] = body.get("Serial");
	       	              arrayOfObject6[4] = body.get("From");
	       	              arrayOfObject6[5] = body.get("Via");
	       	              arrayOfObject6[6] = Integer.valueOf(seq);
                         data = buildOutStreamContent(String.format("{\n  \"EasyDarwin\": {\n    \"Body\": {\n      \"Channel\": \"%s\",\n      \"From\" : \"%s\",\n      \"Protocol\": \"%s\",\n      \"Serial\": \"%s\",\n      \"To\" : \"%s\",\n      \"Via\" : \"%s\"\n    },\n    \"Header\": {\n      \"CSeq\": \"%d\",\n      \"ErrorNum\": \"200\",\n      \"ErrorString\": \"Success OK\",\n      \"MessageType\": \"MSG_DS_STOP_RECORD_PLAY_ACK\",\n      \"Version\": \"1.0\"\n    }\n  }\n}", arrayOfObject6));
                         synchronized (CommandService.this.mOS)
                         {
                           CommandService.this.mOS.write(data);
                           CommandService.this.mOS.flush();
                         }
                         Log.d("CMD", data);   
                         
           		  }
             
                }
            }
        }
    }
    
    public class KeepAliveThread extends Thread {
        private int count = 0;

        @Override
        public void run() {
        	 try
	  	      {
	  	        Thread.sleep(3000);
	  	      }catch (Exception localException)
	  	      {        
	  	          
	  	      }
            while (mKeepAliveThread != null) {
               // String timeStr = EasyApplication.getEasyApplication().getKeepAliveInterval();
                int time = Integer.parseInt(ServerManager.getInstance().getAliveInterval());
                try {
                    Thread.sleep(time * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "kim retry exception!");
                }
                if (mOS == null || mIS == null)
                    continue;
                if (++count % 20 == 0) {
                    Log.d(TAG, " send : " + ACTION_SNAPSHOT_REQ);                    
                     doSendSnap(1);                   
                } else {
                    Log.d(TAG, " send : " + ACTION_COMMOND_KEEPALIVE);                   
                    doSendKeepalive();
                  
                }
            }
        }
    }

    @NonNull
    private String buildOutStreamContent(String requestJson) {
        StringBuffer sb = new StringBuffer();
        sb.append("POST / HTTP/1.1\r\n");
        sb.append("User-Agent:Android device\r\n");
        sb.append("Connection: Keep-Alive\r\n");
        sb.append("Content-Length: ");
        sb.append(requestJson.length());
        sb.append("\r\n");

   
        sb.append("\r\n");
        sb.append(requestJson);
        return sb.toString();
    }

    BlockCommandThread mReadThread = null;
    KeepAliveThread mKeepAliveThread = null;


    public CommandService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    static private void saveSnap(final byte[] data,final int channel, final int width, final int height)
    {
    	new Thread(new Runnable() {
			@Override
			public void run() {	
							
				File picture;
				Log.d(TAG, "kim snap save=" + channel);
				picture = new File(Constants.SNAP_FILE_PATH+(channel)+"-snap.jpg");
			  	if(picture.exists())
				{
		       		picture.delete();
				}     	
		        try {
    		        	picture.createNewFile();      
    		        	FileOutputStream filecon = new FileOutputStream(picture);
    		            YuvImage image = new YuvImage(data,
    		                    ImageFormat.NV21, width, height,
    		                    null);	   		            
    		            image.compressToJpeg(
    		                    new Rect(0, 0, image.getWidth(), image.getHeight()),
    		                    90, filecon);   // 将NV21格式图片，以质量70压缩成Jpeg，并得到JPEG数据流    		            
    		            filecon.close();       	 
    		            
    		     	}catch (IOException e)
    		        {
    		            e.printStackTrace();
    		            return ;
    		        }	
			}
		}).start();	

    }       
    
    private void doSendSnap(int channel) {
          if (mOS != null) {
            Log.d(TAG, "kim AsyncTask.execute run");
            BlockCommandThread thread = mReadThread;
            if (thread != null) {
                try {
                    
                	Thread.sleep(100);
                	File file = new File(Constants.SNAP_FILE_PATH+(channel)+"-snap.jpg");
                    Log.d(TAG, " snap exists=" + file.exists()+channel);
                    if (file.exists()) {
                        Log.d(TAG, "kim mSoc.isClosed()=" + mSoc.isClosed() + ", mSoc.isConnected()=" + mSoc.isConnected());
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        fis.close();
                        String base64 = Base64.encodeToString(buffer, Base64.NO_PADDING | Base64.NO_WRAP);
                        String serial = ServerManager.getInstance().getStreamname(); 
                        String resp =null;
                                            
                     resp = thread.requestAndResp(String.format("{\n" +
                            "  \"EasyDarwin\": {\n" +
                            "    \"Body\": {\n" +
                            "      \"Image\": \"%s\",\n" +
                            "      \"Serial\": \"%s\",\n" +
                            "      \"Time\": \"\",\n" +
                            "      \"Type\": \"JPEG\"\n" +
                            "    },\n" +
                            "    \"Header\": {\n" +
                            "      \"CSeq\": \"%d\",\n" +
                            "      \"MessageType\": \"MSG_DS_POST_SNAP_REQ\",\n" +
                            "      \"Version\": \"1.0\"\n" +
                            "    }\n" +
                            "  }\n" +
                            "}", base64, serial, mSeq), mSeq++);
                        Log.d(TAG, "kim 上传快照响应：" + resp);
                         
                        file.delete();         	            
                        if (resp == null || resp.equals("")) 
			            {
                        	Intent intent = new Intent(ACTION_COMMOND_STATE_CHANGED);
                            intent.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                            LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(intent);
    				        return;		            	
			            }                    
                        JSONObject receivedJson = new JSONObject(resp);
                        final JSONObject easyDarwin = receivedJson.getJSONObject("EasyDarwin");
                        final JSONObject body = easyDarwin.getJSONObject("Body");
                        final JSONObject header = easyDarwin.getJSONObject("Header");
                        int err = header.optInt("ErrorNum");
                        if (err == 200) { 
                        	Log.d(TAG, " snap response sucess" );                         
                        } else {      
                        	 Log.d(TAG, " snap response fail" );                   
                        }
                    }
                } catch (Exception e) {                
                    e.printStackTrace();
                    Intent intent = new Intent(ACTION_COMMOND_STATE_CHANGED);
                    intent.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                    LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(intent);
                }
            }
        }
    }

    private void doSendKeepalive() {
        String info = null;
        String ip = ServerManager.getInstance().getIp();
        String port = ServerManager.getInstance().getAddport();
        String name = ServerManager.getInstance().getDevicename();
        String serial =  ServerManager.getInstance().getStreamname();
        String tag =  ServerManager.getInstance().getDeviceTag();
        String token =  ServerManager.getInstance().getDeviceKey(); 
        Log.d(TAG, "keepalive1");        
        if (mOS != null) {
            BlockCommandThread thread = mReadThread;
            if (thread != null) {
                try {
                	int j;
                	String resp =null ;
                    String  registerJson = SendNVRRegister(name,serial, tag, token, mSeq,4);               
                    Log.d(TAG, registerJson);
                                      
                    resp = thread.requestAndResp(registerJson, mSeq++);           
                    if (resp == null || resp.equals("")) {                      
                    	Intent i = new Intent(ACTION_COMMOND_STATE_CHANGED);
                        i.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                        LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(i);
                        Log.d(TAG, "keep live response error");
                    	return;
                    }               	
                    Log.d(TAG, "keep live response" + resp);                   
                    JSONObject receivedJson = new JSONObject(resp);
                    final JSONObject easyDarwin = receivedJson.getJSONObject("EasyDarwin");
                    final JSONObject body = easyDarwin.getJSONObject("Body");
                    final JSONObject header = easyDarwin.getJSONObject("Header");                
                    int err = header.optInt("ErrorNum");
                    if (err == 200) {                   	
                    	                
                    } else {                  
                        Intent i = new Intent(ACTION_COMMOND_STATE_CHANGED);
                        i.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                        LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(i);
                    }
                } catch (Exception e) {              
                    e.printStackTrace();
                    Intent intent = new Intent(ACTION_COMMOND_STATE_CHANGED);
                    intent.putExtra(KEY_STATE, STATE_CONNECT_ERROR);
                    LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(intent);
                }
            }
        }
    }
    public void onPushOK(JSONObject body, int  seq) {
       
        Object channel = body.opt("Channel");
        Object to = body.opt("To");
        Object protocol = body.opt("Protocol");
        Object reserve = body.opt("Reserve");
        Object serial1 = body.opt("Serial");
        Object server_ip = body.opt("Server_IP");
        Object server_port = body.opt("Server_PORT");
        Object from = body.opt("From");
        Object via = body.opt("Via");
        String respStr = String.format("{\n" +
                "  \"EasyDarwin\": {\n" +
                "    \"Body\": {\n" +
                "      \"Channel\": \"%s\",\n" +
                "      \"From\" : \"%s\",\n" +
                "      \"Protocol\": \"%s\",\n" +
                "      \"Reserve\": \"%s\",\n" +
                "      \"Serial\": \"%s\",\n" +
                "      \"Server_IP\": \"%s\",\n" +
                "      \"Server_PORT\": \"%s\",\n" +
                "      \"To\" : \"%s\",\n" +
                "      \"Via\" : \"%s\"\n" +
                "    },\n" +
                "    \"Header\": {\n" +
                "      \"CSeq\": \"%d\",\n" +
                "      \"ErrorNum\": \"200\",\n" +
                "      \"ErrorString\": \"Success OK\",\n" +
                "      \"MessageType\": \"MSG_DS_PUSH_STREAM_ACK\",\n" +
                "      \"Version\": \"1.0\"\n" +
                "    }\n" +
                "  }\n" +
                "}", channel, to, protocol, reserve, serial1, server_ip, server_port, from, via, seq);
        Log.d(TAG, " stream ACK"+channel);
        respStr = buildOutStreamContent(respStr);     
        final String finalRespStr = respStr;     
	    try {
	        synchronized (mOS) {
	            mOS.write(finalRespStr);
	            mOS.flush();
	        }
	    } catch (Exception ex) {
	        ex.fillInStackTrace();
	    }

    }
    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "kim onReceive : " + intent.getAction());
                String action = intent.getAction();
                           
                    if(  action.equals( ACTION_COMMOND_STATE_CHANGED))                       
                    {
                    	int state = intent.getExtras().getInt(KEY_STATE);
                        if (state == STATE_CONNECT_ERROR) {
                            stopService();
                            AsyncTask.execute(new Runnable() {
	                                 @Override
	                                 public void run() {	                                     
                                    
                                     	try
									      {
									         Thread.sleep(WAIT_RESTART_TIMEOUT_VALUE*1000);
									         Log.d(TAG, "restart server " );
									      }catch (Exception localException)
									      {      
									          
									      }                                     	
                                    	
                                    	start();              
	                                 }
	                             });                       
	                             
                        }
                       
                    }   	                            
	                 if (action.equals(ACTION_SNAPSHOT_REQ))
	                 {
	                	int channel = intent.getIntExtra("Channel", 0);
	                 	Log.d(TAG, " action-snapshot-request"+channel);
	                 	//CameraUtil.cameraTakePicture(channel, 0);  
	                 	//MediaCodecManager.TakePicture(channel, 0);   
	                 	MainService.TakePictureAll(0);           	
	                 }
	                 else if(action.equals(ACTION_START_STREAM))   
	                 {
	                 	 String str1 = intent.getStringExtra("Server_IP");
	                 	 String port = intent.getStringExtra("Server_PORT");
	                 	 final int mStartStreamingReqSeq;
	                 	 final  JSONObject mStartStreamingReqBody;
	                 	int  channel_1 = 0;
	                 	 channel_1 =Integer.parseInt(intent.getStringExtra("Channel"));	                 	
	                 	if(channel_1>=1 && channel_1<= 4 )
	                 	{
	                     // MainService.getInstance().startVideoUpload2(str1, port, paramAnonymousIntent.getStringExtra("Serial"),paramAnonymousIntent.getIntExtra("Channel", 0));
	                      CameraUtil.startVideoUpload2(str1, port, intent.getStringExtra("Serial"),channel_1);  
	                 	}
	                 	 Log.d("CMD", " ACTION_START_STREAM:"+channel_1);
	                 	 try {
	                 		 	mStartStreamingReqSeq = intent.getIntExtra("seq", 0);
	                 		 	mStartStreamingReqBody = new JSONObject(intent.getStringExtra("body"));
                         		onPushOK(mStartStreamingReqBody, mStartStreamingReqSeq);                      	
                     
	                         
	                     } catch (JSONException e) {
	                         e.printStackTrace();
	                     }  	  
	                 
	                 }else if(action.equals(ACTION_STOP_STREAM))
	                 {
	                	 int  channel_1 = 0;	                	
	                	 channel_1 =Integer.parseInt(intent.getStringExtra("Channel"));	                	 
	                 	 Log.d("CMD", " ACTION_STOP_STREAM:"+channel_1);
	                 	 if(channel_1>=1 && channel_1<= 4 )
	                 	 {
	                 		 // MainService.getInstance().stopVideoUpload(paramAnonymousIntent.getIntExtra("Channel", 0)-1);       	
	                 		CameraUtil.stopVideoUpload(channel_1-1); 
	                 	 }
	                 }
                 
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SNAPSHOT_REQ);
        filter.addAction(ACTION_COMMOND_STATE_CHANGED);
        filter.addAction(ACTION_START_STREAM);
        filter.addAction(ACTION_STOP_STREAM);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter); 
        this.mReceiver_DVR = new BroadcastReceiver()
        {
        		public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        		{
        			  String str = paramAnonymousIntent.getAction();
        	               	          
        	            if (str.equals("Phototake"))
        	            {
        	            	File picture;
            			 	final int index;
							int CAMERA_OPER_MODE;
        	            	byte[] data;
            			 	Bundle bundle = paramAnonymousIntent.getExtras();
            			 	index = bundle.getInt("index");
            			 	CAMERA_OPER_MODE = bundle.getInt("CAMERA_OPER_MODE");      			 	
        	            	
        	            	if(CAMERA_OPER_MODE == 0)
        	            	{
        	            		data = bundle.getByteArray("data");    	       		    
            	            	Log.d(TAG, " Phototake commandservice");
            	            	int width=  bundle.getInt("width");
            				 	int height = bundle.getInt("height");
	        	            	CommandService.this.saveSnap(data, index+1, width,height); 	        	            	            
	        	            	 AsyncTask.execute(new Runnable() {
	                                 @Override
	                                 public void run() {
	                                     doSendSnap(index+1);
	                                 }
	                             });
        	            	}        	            	
        	            	
        	            } 
        		}
        };	
              	
        
      IntentFilter localIntentFilter1 = new IntentFilter();
      localIntentFilter1.addAction("Phototake");
      localIntentFilter1.addAction(Constants.ACTION_VIDEO_PLAYBACK_LIST);      
      LocalBroadcastManager.getInstance(this).registerReceiver(this.mReceiver_DVR, localIntentFilter1);           
      instance = this;    
        
    }
    
    
    public void snapshot(final int index, int width, int height,byte[] data)
   {
	   
       Log.d(TAG, " Phototake commandservice");
	   saveSnap(data, index+1, width,height);  
       
	   AsyncTask.execute(new Runnable() {
           @Override
           public void run() {
               doSendSnap(index+1);
           }
       });
	   
   }

    private void closeSocket() {
        try {
            if (mSoc != null) {
                mSoc.close();
                mSoc = null;
            }
            if (mOS != null) {
                mOS.close();
                mOS = null;
            }
            if (mIS != null) {
                mIS.close();
                mIS = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopService() {

        if (mKeepAliveThread != null) {
            mKeepAliveThread.interrupt();
            mKeepAliveThread = null;
        }

        Thread thread = mReadThread;
        if (thread != null) {
            mReadThread = null;
            thread.interrupt();
            try {
                if (mSoc != null) {
                    mSoc.close();
                    mSoc = null;
                }

                if (mOS != null) {
                    mOS.close();
                    mOS = null;
                }
                if (mIS != null) {
                    mIS.close();
                    mIS = null;
                }
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        start();
        return result;
    }

    private void start() {
    	
    	
    	Log.d("CMD", "onStartCommand");
	    
        if (mReadThread == null) {
            mReadThread = new BlockCommandThread();
            mReadThread.start();
        }    
        

        if (mKeepAliveThread == null) {
            mKeepAliveThread = new KeepAliveThread();
            mKeepAliveThread.start();
        }
    }

    @Override
    public void onDestroy() {  
        Log.d(TAG, "onDestroy begin");
        stopService();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);       
        super.onDestroy();
        LocalBroadcastManager.getInstance(CommandService.this).sendBroadcast(new Intent(ACTION_COMMOND_RESTART_THREAD));
        Log.d(TAG, "onDestroy end");
    }

}
