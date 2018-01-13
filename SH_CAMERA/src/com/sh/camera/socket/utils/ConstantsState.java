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
 * 类名称：ConstantsState    
 * 类描述：状态标志位    
 * 创建人：Administrator    
 * 创建时间：2016-6-20 下午2:40:13    
 * 修改人：Administrator    
 * 修改时间：2016-6-20 下午2:40:13    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class ConstantsState {

	private static final String TAG = "ConstantsState.";

	/**0：ACC 关；1： ACC 开*/
	public static int s0 = 1;

	/**0：未定位；1：定位*/
	public static int s1 = 0;

	/**0：北纬；1：南纬*/
	public static int s2 = 0;

	/**0：东经；1：西经*/
	public static int s3 = 0;

	/**0：运营状态；1：停运状态*/
	public static int s4 = 0;

	/**0：经纬度未经保密插件加密；1：经纬度已经保密插件加密*/
	public static int s5 = 0;

	/**保留*/
	public static int s6 = 0;

	/**保留*/
	public static int s7 = 0;

	/**00：空车；01：半载；10：保留；11：满载*/
	public static String s89 = "00";

	/**0：车辆油路正常；1：车辆油路断开*/
	public static int s10 = 0;

	/**0：车辆电路正常；1：车辆电路断开*/
	public static int s11 = 0;

	/**0：车门解锁；1：车门加锁*/
	public static int s12 = 0;

	/**0：门1 关；1：门1 开（前门）*/
	public static int s13 = 0;

	/**0：门2 关；1：门2 开（中门）*/
	public static int s14 = 0;

	/**0：门3 关；1：门3 开（后门）*/
	public static int s15 = 0;

	/**0：门4 关；1：门4 开（驾驶席门）*/
	public static int s16 = 0;

	/**0：门5 关；1：门5 开（自定义）*/
	public static int s17 = 0;

	/**0：未使用GPS 卫星进行定位；1：使用GPS 卫星进行定位*/
	public static int s18 = 1;

	/**0：未使用北斗卫星进行定位；1：使用北斗卫星进行定位*/
	public static int s19 = 0;

	/**0：未使用GLONASS 卫星进行定位；1：使用GLONASS 卫星进行定位*/
	public static int s20 = 0;

	/**0：未使用Galileo 卫星进行定位；1：使用Galileo 卫星进行定位*/
	public static int s21 = 0;

	/**保留*/
	public static int s22 = 0;

	/**保留*/
	public static int s23 = 0;

	/**保留*/
	public static int s24 = 0;

	/**保留*/
	public static int s25 = 0;

	/**保留*/
	public static int s26 = 0;

	/**保留*/
	public static int s27 = 0;

	/**保留*/
	public static int s28 = 0;

	/**保留*/
	public static int s29 = 0;

	/**保留*/
	public static int s30 = 0;

	/**保留*/
	public static int s31 = 0;

	/**
	 * 设置状态值
	 * @param i 设置报警项
	 * @param value 值
	 */
	public static void setStateValue(int i,String value){
		switch (i) {
		case 0:
			s0=Integer.parseInt(value);
			break;
		case 1:
			s1=Integer.parseInt(value);
			break;
		case 2:
			s2=Integer.parseInt(value);
			break;
		case 3:
			s3=Integer.parseInt(value);
			break;
		case 4:
			s4=Integer.parseInt(value);
			break;
		case 5:
			s5=Integer.parseInt(value);
			break;
		case 6:
			s6=Integer.parseInt(value);
			break;
		case 7:
			s7=Integer.parseInt(value);
			break;
		case 8:
			s89=value;
			break;
		case 9:
			s89=value;
			break;
		case 10:
			s10=Integer.parseInt(value);
			break;
		case 11:
			s11=Integer.parseInt(value);
			break;
		case 12:
			s12=Integer.parseInt(value);
			break;
		case 13:
			s13=Integer.parseInt(value);
			break;
		case 14:
			s14=Integer.parseInt(value);
			break;
		case 15:
			s15=Integer.parseInt(value);
			break;
		case 16:
			s16=Integer.parseInt(value);
			break;
		case 17:
			s17=Integer.parseInt(value);
			break;
		case 18:
			s18=Integer.parseInt(value);
			break;
		case 19:
			s19=Integer.parseInt(value);
			break;
		case 20:
			s20=Integer.parseInt(value);
			break;
		case 21:
			s21=Integer.parseInt(value);
			break;
		case 22:
			s22=Integer.parseInt(value);
			break;
		case 23:
			s23=Integer.parseInt(value);
			break;
		case 24:
			s24=Integer.parseInt(value);
			break;
		case 25:
			s25=Integer.parseInt(value);
			break;
		case 26:
			s26=Integer.parseInt(value);
			break;
		case 27:
			s27=Integer.parseInt(value);
			break;
		case 28:
			s28=Integer.parseInt(value);
			break;
		case 29:
			s29=Integer.parseInt(value);
			break;
		case 30:
			s30=Integer.parseInt(value);
			break;
		case 31:
			s31=Integer.parseInt(value);
			break;

		default:
			break;
		}
	}

	/**
	 * 获取标志位
	 * @return
	 */
	public static byte[] getStateByte(){

		byte[] state = new byte[4];
		try {
			//31-24位
			StringBuffer state0 = new StringBuffer();
			state0.append(s31);
			state0.append(s30);
			state0.append(s29);
			state0.append(s28);
			state0.append(s27);
			state0.append(s26);
			state0.append(s25);
			state0.append(s24);
			state[0] = ParseUtil.BitToByte(state0.toString());

			//23-16位
			StringBuffer state1 = new StringBuffer();
			state1.append(s23);
			state1.append(s22);
			state1.append(s21);
			state1.append(s20);
			state1.append(s19);
			state1.append(s18);
			state1.append(s17);
			state1.append(s16);
			state[1] = ParseUtil.BitToByte(state1.toString());

			//15-8位
			StringBuffer state2 = new StringBuffer();
			state2.append(s15);
			state2.append(s14);
			state2.append(s13);
			state2.append(s12);
			state2.append(s11);
			state2.append(s10);
			state2.append(s89);
			state[2] = ParseUtil.BitToByte(state2.toString());

			//7-0位
			StringBuffer state3 = new StringBuffer();
			state3.append(s7);
			state3.append(s6);
			state3.append(s5);
			state3.append(s4);
			state3.append(s3);
			state3.append(s2);
			state3.append(s1);
			state3.append(s0);
			state[3] = ParseUtil.BitToByte(state3.toString());
		} catch (Exception e) {
			e.printStackTrace();
			AppLog.e(ExceptionUtil.getInfo(e), e);
		}
		return state;
	}

}
