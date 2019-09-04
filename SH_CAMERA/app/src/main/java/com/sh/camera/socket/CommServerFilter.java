package com.sh.camera.socket;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import com.sh.camera.util.AppLog;

public class CommServerFilter extends IoFilterAdapter {
	private static final String TAG = "CommServerFilter";
	public static Map<IoSession, IoSession> lastsSessionMap = new HashMap<IoSession, IoSession>();
	public static Object object = new Object();

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,Object message) throws Exception {
		//AppLog.i(TAG,"&&&&&mes&&&&:"+message+",session:"+session);
		IoSession lastsSession = lastsSessionMap.get(session);
		if(lastsSession != null){
			//如果上一次session与下一次session数据一样，做个延时处理
			if(session.equals(lastsSession)){
				//AppLog.i(TAG,"*********************"+lastsSession+"*********************");
				synchronized (object) {
					Thread.sleep(30);
					nextFilter.messageReceived(session, message);
				}
			}else{
				nextFilter.messageReceived(session, message);
			}
		}else{
			nextFilter.messageReceived(session, message);
		}
		lastsSessionMap.put(session, session);

	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session,WriteRequest writeRequest) throws Exception {
		nextFilter.messageSent(session, writeRequest);
	}


}
