/*  car eye 车辆管理平台 
 * 企业网站:www.shenghong-technology.com
 * 车眼管理平台   www.car-eye.cn
 * 车眼开源网址:https://github.com/Car-eye-admin
 * Copyright
 */


package com.sh.camera.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zr
 * 工具类
 */
public class Tools {
	private static final String TAG = "Tools";

	/**
	 * 获取指定长度"\0"结尾的数组转字符串
	 * @param buf
	 * @param startIndex
	 * @param length
	 * @return
	 */
	public static byte[] byteToSubstringToByte(byte data[],int startIndex, int length){

		try {
			byte[] strbyte = null;
			if(data != null && data.length > 0){

				String dataHex = parseByte2HexStr(byteTobyte(data, startIndex, length));
				//用“00”结尾截取十六进制字符串
				String[] strarr = replaceStr(dataHex,"00","##").split("##");
				//	   //用“00”结尾截取十六进制字符串
				//	   String[] strarr = parseByte2HexStr(byteTobyte(data, startIndex, length)).split("00");
				//得到字符串二进制
				if(strarr != null && strarr.length > 0){
					strbyte =  parseHexStr2Byte(strarr[0]);
				}
			}
			return strbyte;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 偶数位截取数据
	 * @param data		需要切割的数据
	 * @param regex		切割字符串
	 * @return			十六进制字符串
	 */
	public static String[] splitForEven(byte[] data, String regex){

		try {
			String dataHex = parseByte2HexStr(data);
			StringBuilder agterText = new StringBuilder();
			int startIndex = 0;
			int regexIndex = 0;
			while((regexIndex = dataHex.indexOf(regex, regexIndex+4)) != -1){
				if(regexIndex % 2 == 0){
					agterText.append(dataHex.substring(startIndex, regexIndex)+"####");
					startIndex = regexIndex + 4;
				}
				if(dataHex.length() > 512){
					break;
				}
			}
			agterText.append(dataHex.substring(startIndex, dataHex.length()));
			String[] dataList = agterText.toString().split("####");
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将整数转换成二进制字节（先低字节后高字节）
	 * @param num
	 * @return
	 */
	public static byte[] int2Bytes(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/**
	 * 获取整数转换成二进制字节
	 * @return
	 */
	public static String int2BytesStr(int iSource, int iArrayLen){
		String str = parseByte2HexStr(int2Bytes(iSource,iArrayLen));
		return str;
	}

	/**
	 * 获取指定长度字节数
	 * @param src
	 * @param startIndex
	 * @param length
	 * @return
	 */
	public static byte[] byteTobyte(byte[] src, int startIndex, int length)
	{
		byte[] des = new byte[length];
		int i = 0; 
		for (int j = startIndex; i < length; ++j) {
			des[i] = src[j];
			++i;
		}
		return des;
	}


	public static byte[] longToByteOne(long num){
		byte[] b = new byte[1];
		b[0] = (byte)(int)(num >>> 0);
		return b;
	}

	/**将二进制转换成16进制 
	 * @param buf 
	 * @return 
	 */  
	public static String parseByte2HexStr(byte buf[]) {  
		StringBuffer sb = new StringBuffer();  
		for (int i = 0; i < buf.length; i++) {  
			String hex = Integer.toHexString(buf[i] & 0xFF);  
			if (hex.length() == 1) {  
				hex = '0' + hex;  
			}  
			sb.append(hex.toUpperCase());  
		}  
		return sb.toString();  
	} 

	/**将16进制转换为二进制 
	 * @param hexStr 
	 * @return 
	 */  
	public static byte[] parseHexStr2Byte(String hexStr) {  

		if (hexStr.length() < 1)  

			return null;  

		byte[] result = new byte[hexStr.length()/2];  

		for (int i = 0;i< hexStr.length()/2; i++) {  

			int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  

			int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  

			result[i] = (byte) (high * 16 + low);  

		}  

		return result;  
	}  

	/**
	 * 字符串转换成ascii的16进制
	 * @param value
	 * @return
	 */
	public static String stringToAsciiHexString(String value){
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			sbu.append(Integer.toHexString((int)chars[i]));   
		}
		return sbu.toString();
	}

	/**
	 * 字符串转换成ascii的2进制
	 * @param value
	 * @return
	 */
	/*public static byte[] stringToByte(String value){
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			sbu.append(Integer.toHexString((int)chars[i]));   
		}
		return Tools.parseHexStr2Byte(sbu.toString());
	}*/
	
	public static byte[] stringToByte(String str){
		try {
			return (str+"\0").getBytes("GBK");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** 
	 * Bit转Byte 
	 */  
	public static byte BitToByte(String byteStr) {  
		int re, len;  
		if (null == byteStr) {  
			return 0;  
		}  
		len = byteStr.length();  
		if (len != 4 && len != 8) {  
			return 0;  
		}  
		if (len == 8) {// 8 bit处理  
			if (byteStr.charAt(0) == '0') {// 正数  
				re = Integer.parseInt(byteStr, 2);  
			} else {// 负数  
				re = Integer.parseInt(byteStr, 2) - 256;  
			}  
		} else {//4 bit处理  
			re = Integer.parseInt(byteStr, 2);  
		}  
		return (byte) re;  
	} 

	/**编码类型*/
	public static final int ENCODER_TYPE = 0;
	/**解码类型*/
	public static final int DECODER_TYPE = 1;

	/**
	 * 55aa转义与逆转义
	 * @param data	数据源
	 * @param type	类型	
	 * 					0：转义	55AA ——> 55AB01,55AB ——> 55AB02;	
	 * 					1：逆转义	55AB01 ——>55AA ,55AB02 ——> 55AB;	
	 * @return
	 */
	public static byte[] trope55aa(byte[] data, int type){
		String dataStr = parseByte2HexStr(data);
		dataStr = dataStr.substring(4, dataStr.length()-4);
		if(type == ENCODER_TYPE){
			dataStr = dataStr.replaceAll("55AB", "55AB02");
			dataStr = dataStr.replaceAll("55AA", "55AB01");
		}else if(type == DECODER_TYPE){
			dataStr = dataStr.replaceAll("55AB01", "55AA");
			dataStr = dataStr.replaceAll("55AB02", "55AB");
		}
		return parseHexStr2Byte("55AA"+dataStr+"55AA");
	}

	/**
	 * 1)	报文的发送端需要将待发送报文的数据内容中（从版本号至校验位，包括版本号和校验位）出现的1002H转义为0x10100202，将出现的1003H转义为0x10100303。
	 * @param str
	 * @return
	 */
	public static byte[] tropeByte(String str,int type){
		byte[] result = new byte[str.length()/2];
		if(type == 1){
			if(str.indexOf("1002")>=0){
				str = str.replaceAll("1002", "10100202");
			}

			if(str.indexOf("1003")>=0){
				str = str.replaceAll("1003", "10100203");
			}
		}
		//		if(str.indexOf("10")>=0){
		//			str = str.replaceAll("10", "1010");
		//		}
		str = replaceStr(str,"10", "1010");
		result = Tools.parseHexStr2Byte(str);

		return result;
	}

	/**
	 * 为保证数据传输的透明，需对信息字段中出现的标志位进行转义处理，定义如下
	 *  7EH 《————》 7DH+02H；
	 *  7DH 《————》 7DH+01H；
	 * @param str
	 * @param type
	 * @return
	 */
	public static byte[] tropeYXByte(String str,int type){

		byte[] result = new byte[str.length()/2];

		if(type == 1){
			str = replaceStr(str,"7D", "7D01");
			str = replaceStr(str,"7E", "7D02");
			//			if(str.indexOf("7D")>=0){
			//				str = str.replaceAll("7D", "7D01");
			//			}
			//
			//			if(str.indexOf("7E")>=0){
			//				str = str.replaceAll("7E", "7D02");
			//			}
		}
		str = replaceStr(str,"10", "1010");
		result = Tools.parseHexStr2Byte(str);

		return result;
	}

	/**
	 * 转义操作
	 * @param str
	 * @param thq
	 * @param thh
	 * @return
	 */
	public static String replaceStr(String str,String thq,String thh){

		StringBuffer des = new StringBuffer();				
		String s = "";
		str = str+" ";
		for(int i=0;i< str.length();i++){

			if(i%2==0){
				if(s.equals(thq)){
					s = thh;
				}

				des.append(s);
				s = "";
				s = s+str.charAt(i);
			}else{
				s = s+str.charAt(i);
			}
		}
		return des.toString();
	}

	/**
	 * 对DTU协议进行逆转义(1010转10)
	 * @param bytes
	 * @return
	 */
	public static byte[] dtuTransferredMeaning(byte[] bytes){

		StringBuffer des = new StringBuffer();
		String str = "";
		for (int j = 0; j < bytes.length; j++) {

			String hex = Integer.toHexString(bytes[j] & 0xFF).toUpperCase();  
			if (hex.length() == 1) {  
				hex = '0' + hex;  
			}

			if(hex.equals("10")){
				str = str+hex;
				if(str.indexOf("1010")>=0){
					str = str.replaceAll("1010", "10");
					des.append(str);
					str = "";
				}

			}else{
				if(str.indexOf("1010")>=0){
					str = str.replaceAll("1010", "10");
				}
				des.append(str);
				str = "";
				des.append(hex);
			}
		}
		return Tools.parseHexStr2Byte(des.toString());
	}

	/**
	 * 计价器与智能顶灯通信协议逆转 
	 * 2)	报文的接收端在找到报文头（1002H）后，接收数据内容（从版本号至校验位，包括版本号和校验位），
	 *    在接收过程中，需要将遇到的0x10100202逆转义为0x1002，将将遇到的0x10100303逆转义为0x1003
	 * @param bytes
	 * @return
	 */
	public static byte[] taximeterToplightReversal(byte[] bytes){
		String str = parseByte2HexStr(bytes);
		str = str.replaceAll("10100202", "1002").replaceAll("10100303", "1003");
		return parseHexStr2Byte(str);

	}

	/**
	 * GK-120D与GK-110G接口协议
	 * 其中帧以7EH作为起始/结束标志，为保证数据传输的透明，需对信息字段中出现的标志位进行转义处理，定义如下
	 *  7DH+02H 《————》 7EH；
	 *  7DH+01H 《————》7DH 
	 * @param bytes
	 * @return
	 */
	public static byte[] yxReversal(byte[] bytes){
		String str = parseByte2HexStr(bytes).toUpperCase();
		str = str.replaceAll("7D02", "7E").replaceAll("7D01", "7D");
		return parseHexStr2Byte(str);
	}

	/**
	 * 高地位反相排序
	 * @param bytes
	 * @return
	 */
	public static byte[]sortToByte(byte[] bytes){
		byte[] des = new byte[bytes.length];
		int i = 0; 
		for (int j = bytes.length-1; j >=0; j--) {
			des[i] = bytes[j];
			i++;
		}
		return des;
	}

	/**
	 * 异或运算和
	 * @param bytes
	 * @return
	 */
	public static byte[] byteOrbyte(byte[] bytes){
		byte[] orbyte = new byte[1];
		byte value = bytes[0];
		for (int i = 1; i < bytes.length; i++) {
			value = (byte) (value^bytes[i]);
		}
		orbyte[0] = value;
		return orbyte;
	}

	/**
	 *String的字符串转换成unicode的String
	 */
	public static String stringToUnicode(String strText) {
		char c;
		String strRet = "";
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128) {
				strRet += strHex;
			} else {
				// 低位在前面补00
				strRet += "00" + strHex;
			}
		}
		return strRet;
	}
	/**
	 *unicode的String转换成String的字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	/**
	 * 得到经纬度的度、分（分+小数点后四位）
	 * @param latlng 
	 * @param type 1 返回度 2 返回分 3 返回小数点后（1至2）两位 4 返回小数点（3至4）两位 
	 * @return 十六进制字符串
	 */
	public static String getLatLng(double latlng,int type){

		try {
			int reLatlng = 0;
			double min = (latlng-(int)latlng)*60;
			String minStr = String.valueOf(min).substring(String.valueOf(min).indexOf(".")+1, String.valueOf(min).length());
			int size = minStr.length();
			if(minStr.length()<4){
				for (int i = 0; i < (4-size); i++) {
					minStr +="0";
				}
			}
			switch (type) {
			case 1:
				reLatlng = (int)latlng;
				break;
			case 2:
				reLatlng = (int) ((latlng-(int)latlng)*60);
				break;
			case 3:
				reLatlng = Integer.parseInt(minStr.substring(0, 2));
				break;
			case 4:
				reLatlng = Integer.parseInt(minStr.substring(2, 4));
				break;
			default:
				break;
			}
			return Tools.parseByte2HexStr(Tools.longToByteOne((reLatlng)));
		} catch (Exception e) {
			e.printStackTrace();
			return "00";
		}
	}

	/**
	 * 解码经纬度
	 * @param latlngbyte
	 * @return
	 */
	public static Double decoderLatlng(byte[] latlngbyte){
		try {
			int no=0;
			byte[] ad = Tools.byteTobyte(latlngbyte, no, 1);
			int adi = Integer.parseInt(Tools.parseByte2HexStr(ad),16);
			no+=1;
			byte[] ac = Tools.byteTobyte(latlngbyte, no, 1);
			no+=1;
			byte[] ac1 = Tools.byteTobyte(latlngbyte, no, 1);
			no+=1;
			byte[] ac2 = Tools.byteTobyte(latlngbyte, no, 1);
			no+=1;
			StringBuffer acBuffer = new StringBuffer();
			acBuffer.append(Integer.parseInt(Tools.parseByte2HexStr(ac),16));
			acBuffer.append(Integer.parseInt(Tools.parseByte2HexStr(ac1),16));
			acBuffer.append(Integer.parseInt(Tools.parseByte2HexStr(ac2),16));
			Double acDouble = Double.parseDouble(acBuffer.toString())/10000;
			Double latlng = adi + acDouble/60;
			DecimalFormat df = new DecimalFormat("#.000000");
			return Double.parseDouble(df.format(latlng));
		} catch (Exception e) {
			e.printStackTrace();
			return 0.000000;
		}
	}

	/**
	 * 得到16进制 年 月 日 时 分 秒
	 * @param formart
	 * @return 十六进制字符串
	 */
	public static String getDateTime(String formart){
		String str = "00";
		try {
			String systemdate = new SimpleDateFormat(formart).format(Calendar.getInstance().getTime());
			str = parseByte2HexStr(longToByteOne(Long.parseLong(systemdate)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;

	}

	/**
	 * 将二进制转换成字符串
	 * @param src
	 * @param startIndex
	 * @param length
	 * @return
	 */
	public static String byteToString(byte[] src, int startIndex, int length){
		if(src == null){
			return "";
		}
		byte[] des = new byte[length];
		int i = 0; 
		for (int j = startIndex; i < length; ++j) {
			des[i] = src[j];
			++i;
		}
		String str = null;
		try {
			str= new String(des,"gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return str.trim();
	}

	/**
	 * 数字字符串转ASCII码字符串
	 * 
	 * @param String
	 *            字符串
	 * @return ASCII字符串
	 */
	public static String StringToAsciiString(String content) {
		String result = "";
		int max = content.length();
		for (int i = 0; i < max; i++) {
			char c = content.charAt(i);
			String b = Integer.toHexString(c);
			result = result + b;
		}
		return result;
	}

	/**
	 * 字节转换成位
	 * @param bytes
	 * @return
	 */
	public static String byteTobit(byte[] bytes){
		String str = "";
		for (int j = 0; j < bytes.length; j++) {
			for(int i = 7; i >= 0; --i){
				str +=(bytes[j] & (1 << i)) == 0 ? '0' : '1';
			}
		}
		return str;
	}

	/**
	 * 字节转换成位，封装成int数组
	 * @param bytes
	 * @return
	 */
	public static int[] byteTobitForIntArr(byte[] bytes){
		String str = byteTobit(bytes);
		int[] bitArr = new int[str.length()];
		for(int i = 0; i < str.length(); i++){
			bitArr[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
		}
		return bitArr;
	}

	/**
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcd2Str(byte[] bytes){
		StringBuffer temp=new StringBuffer(bytes.length*2);

		for(int i=0;i<bytes.length;i++){
			temp.append((byte)((bytes[i]& 0xf0)>>>4));
			temp.append((byte)(bytes[i]& 0x0f));
		}
		return temp.toString().substring(0,1).equalsIgnoreCase("0")?temp.toString().substring(1):temp.toString();
	}

	/** *//**
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length()/2; p++) {
			if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			}else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	/**
	 * 转换成指定长度的字符串 少的部分以空格填充
	 * @param s
	 * @param fieldLength
	 * @return
	 */
	public static String appendSpaceRight(String s, int fieldLength){
		if (s == null) {
			s = "";
		}
		String ret = s;
		int stringLength = s.length();
		if (stringLength < fieldLength) {
			for (int i = stringLength; i < fieldLength; ++i) {
				ret = ret + " ";
			}
		}
		else if (stringLength > fieldLength) {
			ret = ret.substring(0, fieldLength);
		}
		return ret;
	}

	/**
	 * 转换成指定长度的字符串 少的部分以0填充
	 * @param s
	 * @param fieldLength
	 * @return
	 */
	public static String appendRight(String s, int fieldLength){
		if (s == null) {
			s = "";
		}
		String ret = s;
		int stringLength = s.getBytes().length;
		if (stringLength < fieldLength) {
			for (int i = stringLength; i < fieldLength; ++i) {
				ret = ret + "0";
			}
		}
		else if (stringLength > fieldLength) {
			ret = ret.substring(0, fieldLength);
		}
		return ret;
	}

	/**
	 * 返回指定长度的字节数组
	 * @param str
	 * @param len
	 * @return
	 */
	public static byte[] getByteToByte(String str,int len){
		byte[] body = new byte[len];
		int dstPos = 0;
		//省域ID
		System.arraycopy(str.getBytes(), 0, body, dstPos, str.getBytes().length);
		return body;
	}


	// 将byte数组转换成InputStream  
	public static InputStream byteTOInputStream(byte[] in) throws Exception {  

		ByteArrayInputStream is = new ByteArrayInputStream(in);  

		return is;  

	}  

	/**
	 * 字符串转换成十六进制字符串
	 */

	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();

		StringBuilder sb = new StringBuilder("");

		byte[] bs = str.getBytes();

		int bit;

		for (int i = 0; i < bs.length; i++) {

			bit = (bs[i] & 0x0f0) >> 4;

		sb.append(chars[bit]);

		bit = bs[i] & 0x0f;

		sb.append(chars[bit]);

		}

		return sb.toString();

	}

	/**

	 * 十六进制转换字符串

	 */

	public static String hexStr2Str(String hexStr) {

		String str = "0123456789ABCDEF";

		char[] hexs = hexStr.toCharArray();

		byte[] bytes = new byte[hexStr.length() / 2];

		int n;

		for (int i = 0; i < bytes.length; i++) {

			n = str.indexOf(hexs[2 * i]) * 16;

			n += str.indexOf(hexs[2 * i + 1]);

			bytes[i] = (byte) (n & 0xff);

		}

		return new String(bytes);

	} 

	public static char ascii2Char(int ASCII) {  
		return (char) ASCII;  
	}  

	public static int char2ASCII(char c) {  
		return (int) c;  
	}  

	/**
	 * 将 ascii转换成字符串
	 * @param ASCIIs
	 * @return
	 */
	public static String ascii2String(String ASCIIs) {  
		String[] ASCIIss = ASCIIs.split(" ");  
		StringBuffer sb = new StringBuffer();  
		for (int i = 0; i < ASCIIss.length; i++) {  
			sb.append((char) ascii2Char(Integer.parseInt(ASCIIss[i])));  
		}  
		return sb.toString();  
	}

	/**
	 * byte——>String
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src){  
		StringBuilder stringBuilder = new StringBuilder("");  
		if (src == null || src.length <= 0) {  
			return null;  
		}  
		for (int i = 0; i < src.length; i++) {  
			int v = src[i] & 0xFF;  
			String hv = Integer.toHexString(v);  
			if (hv.length() < 2) {  
				stringBuilder.append(0);  
			}  
			stringBuilder.append(hv);  
		}  
		return stringBuilder.toString();  
	} 

	/**
	 * 域名转换成IP
	 * @param domain
	 * @return
	 */
	public static String domainToip(String domain){
		String ip = "";
		try {  
			InetAddress inetHost = InetAddress.getByName(domain);  
			ip = inetHost.getHostAddress();
			System.out.println("ip=" + ip);  
		} catch(UnknownHostException e) {  
			e.printStackTrace();
		} 
		return ip;
	}

	public static boolean isIp(String IP){//判断是否是一个IP  
		boolean b = false;  
		IP = clearSpace(IP);  
		if(IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")){  
			String s[] = IP.split("\\.");  
			if(Integer.parseInt(s[0])<255)  
				if(Integer.parseInt(s[1])<255)  
					if(Integer.parseInt(s[2])<255)  
						if(Integer.parseInt(s[3])<255)  
							b = true;  
		}  
		return b;  
	} 

	public static String clearSpace(String IP){//去掉IP字符串前后所有的空格  
		while(IP.startsWith(" ")){  
			IP= IP.substring(1,IP.length()).trim();  
		}  
		while(IP.endsWith(" ")){  
			IP= IP.substring(0,IP.length()-1).trim();  
		}  
		return IP;  
	}  

	/**
	 * 字符串编码转换的实现方法
	 * @param str  待转换编码的字符串
	 * @param newCharset 目标编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String changeCharset(String str, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			//用默认字符编码解码字符串。
			byte[] bs = str.getBytes();
			//用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}

	/** 从字符串中获取数字*/
	public static String filtrateNumber(String text){
		if(text != null && !text.equals("")){
			String number = "";
			String regEx="[^0-9]";   
			Pattern p = Pattern.compile(regEx);   
			Matcher m = p.matcher(text);   
			number = m.replaceAll("").trim();
			return number;
		}else{
			return null;
		}
	}
	
	/**
	 * 整数转换成小数/1000
	 * @param parm
	 * @return
	 */
	public static String intChangeString(int parm,int number){

		String  s = "0";
		try {
			String xs = "0.0";
			if(number == 10){
				xs = "0.0";
			}else if(number == 100){
				xs = "0.00";
			}else if(number == 1000){
				xs = "0.000";
			}else if(number == 10000){
				xs = "0.0000";
			}else if(number == 100000){
				xs = "0.00000";
			}
			float num = (float)parm/number;  
			DecimalFormat df = new DecimalFormat(xs);//格式化小数  
			s = df.format(num);
			s = String.valueOf(Double.parseDouble(s));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return  s;
	}
	
	/**
	 * 字符串转
	 * @param strTextUnicode 编码 高地位反向
	 * @return
	 */
	public static String stringToUnicodeGd(String strText) {
		char c;
		String strRet = "";
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128) {
				strRet += parseByte2HexStr(sortToByte(parseHexStr2Byte(strHex)));
			} else {
				// 低位在前面补00
				strRet += parseByte2HexStr(sortToByte(parseHexStr2Byte("00" + strHex)));
			}
		}
		return strRet;
	}
	
	/**
	 * 字符串转换成指定长度的字符串
	 * @param value
	 * @param len
	 * @return
	 */
	public static String stringToString(String value,int len){
		if(value == null){
			value = "";
		}
		int length = value.length();
		if(length > len){
			value = value.substring(0, len);
		}else{
			for (int i = 0; i < (len-length); i++) {
				value = value+"0";
			}
		}
		return value;

	}

	
}
