/*  car eye 车辆管理平台 
/*  car eye 车辆管理平台
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket;

import java.util.List;

import com.sh.camera.service.ShCommService;
import com.sh.camera.socket.db.DataMsgDao;
import com.sh.camera.socket.model.DSCommData;
import com.sh.camera.socket.utils.ParseUtil;
import com.sh.camera.util.AppLog;
import com.sh.camera.util.Constants;

/**
 *     
 * 项目名称：DSS_808    
 * 类名称：BussinessProcessUtil    
 * 类描述：业务处理工具类    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:53:49    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:53:49    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class BussinessProcessUtil {
	private static final String TAG = "BussinessProcessUtil";

	/**
	 * 补发数据表中未发送成功的数据
	 */
	public static void dataReissue() {
		new Thread() {
			@Override
			public void run() {

				try {
					List<DSCommData> list = DataMsgDao.getInstance(ShCommService.getInstance()).findAll();
					if(list != null){
						AppLog.i(TAG, "补发"+list.size()+",条数据");
						for (DSCommData aaData : list) {
							Thread.sleep(1000);
							AppLog.i(TAG, "补发[msgid:"+aaData.getMsgid()+"],数据："+aaData.getDatahex());
							//数据补传
							CommCenterUsers.witeMsg(ParseUtil.parseHexStr2Byte(aaData.getDatahex()),2);
							
							DataMsgDao.getInstance(ShCommService.getInstance()).delete(aaData.getSeq(), aaData.getMsgid());
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
	
}
