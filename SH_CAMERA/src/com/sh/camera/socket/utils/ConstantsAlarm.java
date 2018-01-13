/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */

package com.sh.camera.socket.utils;

import com.sh.camera.util.AppLog;
import com.sh.camera.util.ExceptionUtil;

/**
 * 项目名称：DSS_808    
 * 类名称：ConstantsAlarm    
 * 类描述：报警标志    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:39:11    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:39:11    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class ConstantsAlarm {
	
	private static final String TAG = "ConstantsAram.";

	/**1：紧急报警，触动报警开关后触发 收到应答后清零*/
	public static int a0 = 0;

	/**1：超速报警 标志维持至报警条件解除*/
	public static int a1 = 0;

	/**1：疲劳驾驶 标志维持至报警条件解除*/
	public static int a2 = 0;

	/**1：危险预警 收到应答后清零*/
	public static int a3 = 0;

	/**1：GNSS 模块发生故障 标志维持至报警条件解除*/
	public static int a4 = 0;

	/**1：GNSS 天线未接或被剪断 标志维持至报警条件解除*/
	public static int a5 = 0;

	/**1：GNSS 天线短路 标志维持至报警条件解除*/
	public static int a6 = 0;

	/**1：终端主电源欠压 标志维持至报警条件解除*/
	public static int a7 = 0;

	/**1：终端主电源掉电 标志维持至报警条件解除*/
	public static int a8 = 0;

	/**1：终端LCD 或显示器故障 标志维持至报警条件解除*/
	public static int a9 = 0;

	/**1：TTS 模块故障 标志维持至报警条件解除*/
	public static int a10 = 0;

	/**1：摄像头故障 标志维持至报警条件解除*/
	public static int a11 = 0;

	/**1：道路运输证IC 卡模块故障 标志维持至报警条件解除*/
	public static int a12 = 0;

	/**1：超速预警 标志维持至报警条件解除*/
	public static int a13 = 0;

	/**1：疲劳驾驶预警 标志维持至报警条件解除*/
	public static int a14 = 0;

	/**保留*/
	public static int a15 = 0;

	/**保留*/
	public static int a16 = 0;

	/**保留*/
	public static int a17 = 0;

	/**1：当天累计驾驶超时 标志维持至报警条件解除*/
	public static int a18 = 0;

	/**1：超时停车 标志维持至报警条件解除*/
	public static int a19 = 0;

	/**1：进出区域 收到应答后清零*/
	public static int a20 = 0;

	/**1：进出路线 收到应答后清零*/
	public static int a21 = 0;

	/**1：路段行驶时间不足/过长 收到应答后清零*/
	public static int a22 = 0;

	/**1：路线偏离报警 标志维持至报警条件解除*/
	public static int a23 = 0;

	/**1：车辆VSS 故障 标志维持至报警条件解除*/
	public static int a24 = 0;

	/**1：车辆油量异常 标志维持至报警条件解除*/
	public static int a25 = 0;

	/**1：车辆被盗(通过车辆防盗器) 标志维持至报警条件解除*/
	public static int a26 = 0;

	/**1：车辆非法点火 收到应答后清零*/
	public static int a27 = 0;

	/**1：车辆非法位移 收到应答后清零*/
	public static int a28 = 0;

	/**1：碰撞预警 标志维持至报警条件解除*/
	public static int a29 = 0;

	/**1：侧翻预警 标志维持至报警条件解除*/
	public static int a30 = 0;

	/**1：非法开门报警（终端未设置区域时，不
	判断非法开门）
	收到应答后清零*/
	public static int a31 = 0;

	/**
	 * 设置报警值
	 * @param i 设置报警项
	 * @param value 值
	 */
	public static void setAlarmStateValue(int i,int value){
		switch (i) {
		case 0:
			a0=1;
			break;
		case 1:
			a1=1;
			break;
		case 2:
			a2=1;
			break;
		case 3:
			a3=1;
			break;
		case 4:
			a4=1;
			break;
		case 5:
			a5=1;
			break;
		case 6:
			a6=1;
			break;
		case 7:
			a7=1;
			break;
		case 8:
			a8=1;
			break;
		case 9:
			a9=1;
			break;
		case 10:
			a10=1;
			break;
		case 11:
			a11=1;
			break;
		case 12:
			a12=1;
			break;
		case 13:
			a13=1;
			break;
		case 14:
			a14=1;
			break;
		case 15:
			a15=1;
			break;
		case 16:
			a16=1;
			break;
		case 17:
			a17=1;
			break;
		case 18:
			a18=1;
			break;
		case 19:
			a19=1;
			break;
		case 20:
			a20=1;
			break;
		case 21:
			a21=1;
			break;
		case 22:
			a22=1;
			break;
		case 23:
			a23=1;
			break;
		case 24:
			a24=1;
			break;
		case 25:
			a25=1;
			break;
		case 26:
			a26=1;
			break;
		case 27:
			a27=1;
			break;
		case 28:
			a28=1;
			break;
		case 29:
			a29=1;
			break;
		case 30:
			a30=1;
			break;
		case 31:
			a31=1;
			break;

		default:
			break;
		}
	}

	/**
	 * 清除报警值
	 */
	public static void clearAlarmStateValue(){
		a0 =0;
		a1 =0;
		a2 =0;
		a3 =0;
		a4 =0;
		a5 =0;
		a6 =0;
		a7 =0;
		a8 =0;
		a9 =0;
		a10 =0;
		a11 =0;
		a12 =0;
		a13 =0;
		a14 =0;
		a15 =0;
		a16 =0;
		a17 =0;
		a18 =0;
		a19 =0;
		a20 =0;
		a21 =0;
		a22 =0;
		a23 =0;
		a24 =0;
		a25 =0;
		a26 =0;
		a27 =0;
		a28 =0;
		a29 =0;
		a30 =0;
		a31 =0;
	}

	/**
	 * 获取标志位
	 * @return
	 */
	public static byte[] getAlarmStateByte(){

		byte[] alarmstate = new byte[4];
		try {
			//31-24位
			StringBuffer alarmstate0 = new StringBuffer();
			alarmstate0.append(a31);
			alarmstate0.append(a30);
			alarmstate0.append(a29);
			alarmstate0.append(a28);
			alarmstate0.append(a27);
			alarmstate0.append(a26);
			alarmstate0.append(a25);
			alarmstate0.append(a24);
			alarmstate[0] = ParseUtil.BitToByte(alarmstate0.toString());

			//23-16位
			StringBuffer alarmstate1 = new StringBuffer();
			alarmstate1.append(a23);
			alarmstate1.append(a22);
			alarmstate1.append(a21);
			alarmstate1.append(a20);
			alarmstate1.append(a19);
			alarmstate1.append(a18);
			alarmstate1.append(a17);
			alarmstate1.append(a16);
			alarmstate[1] = ParseUtil.BitToByte(alarmstate1.toString());

			//15-8位
			StringBuffer alarmstate2 = new StringBuffer();
			alarmstate2.append(a15);
			alarmstate2.append(a14);
			alarmstate2.append(a13);
			alarmstate2.append(a12);
			alarmstate2.append(a11);
			alarmstate2.append(a10);
			alarmstate2.append(a9);
			alarmstate2.append(a8);
			alarmstate[2] = ParseUtil.BitToByte(alarmstate2.toString());

			//7-0位
			StringBuffer alarmstate3 = new StringBuffer();
			alarmstate3.append(a7);
			alarmstate3.append(a6);
			alarmstate3.append(a5);
			alarmstate3.append(a4);
			alarmstate3.append(a3);
			alarmstate3.append(a2);
			alarmstate3.append(a1);
			alarmstate3.append(a0);
			alarmstate[3] = ParseUtil.BitToByte(alarmstate3.toString());

			//清除报警
			clearAlarmStateValue();

		} catch (Exception e) {
			e.printStackTrace();
			AppLog.e(ExceptionUtil.getInfo(e), e);
		}
		return alarmstate;
	}

}
