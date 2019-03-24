/* car-eye车辆管理平台 
 * car-eye车辆管理公共平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-admin
 * Copyright car-eye 车辆管理平台  2017 
 */

package com.sh.camera.socket.model;

/**    
 *     
 * 项目名称：dsparse    
 * 类名称：TaxiInfoItems    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016-10-13 下午03:02:48    
 * 修改人：Administrator    
 * 修改时间：2016-10-13 下午03:02:48    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class VideoInfo {
	
	/**逻辑通道号*/
	private int logicChannel;
	
	/**码流类型*/
	private int streamType;
	

	/**0：主存储器或灾备存储器 1：主存储器 2：灾备存储器*/
	private int memoryType;
	

	/**开始时间*/
	private String startTime;
	
	/**结束时间*/
	private String endTime;
	
	
	/**0：音视频 1：音频 2：视频 3视频或音视频*/
	private int mediaType;
	
	private int size;

	
	private int a0;
	private int a1;
	private int a2;
	private int a3;
	private int a4;
	private int a5;
	private int a6;
	private int a7;
	private int a8;
	private int a9;
	private int a10;
	private int a11;
	private int a12;
	private int a13;
	private int a14;
	private int a15;
	private int a16;
	private int a17;
	private int a18;
	private int a19;
	private int a20;
	private int a21;
	private int a22;
	private int a23;
	private int a24;
	private int a25;
	private int a26;
	private int a27;
	private int a28;
	private int a29;
	private int a30;
	private int a31;
	private int a32;
	private int a33;
	private int a34;
	private int a35;
	private int a36;
	private int a37;
	private int a38;
	private int a39;
	private int a40;
	private int a41;
	private int a42;
	private int a43;
	private int a44;
	private int a45;
	private int a46;
	private int a47;
	private int a48;
	private int a49;
	private int a50;
	private int a51;
	private int a52;
	private int a53;
	private int a54;
	private int a55;
	private int a56;
	private int a57;
	private int a58;
	private int a59;
	private int a60;
	private int a61;
	private int a62;
	private int a63;
	
	/**总数*/
	private int total;
	
	/**流水号*/
	private int seqNumber;
	
	/**密码长度*/
	private int passLength;
	
	/**密码*/
	private String passWord;
	
	/**用户长度*/
	private int userLength;
	
	/**用户*/
	private String username;
	
	/**文件路径*/
	private String poot;
	
	/**文件路径长度*/
	private int pootLength;
	
	/**存储位置*/
	private int recordPosition;
	
	/**执行任务条件*/
	private int taskOp;
	
	/**上传控制*/
	private int control;
	
	/**方向*/
	private int direction;
	
	/**速度*/
	private int speed;
	
	/**音频编码格式*/
	private int audioCodec;
	
	/**输入音频通道数*/
	private int channels;
	
	/**输入音频采样率*/
	private int samplerate;
	
	/**音频采样位数*/
	private int sampleBits;
	
	/**音频帧长度*/
	private int sampleLength;
	
	/**是否支持音频输出*/
	private int enableflag;
	
	/**视频编码格式*/
	private int vediocodec;
	
	/**终端支持最大音频通道数量*/
	private int audiovhannels;
	
	/**终端支持的最大视频通道数量*/
	private int vediovhannnels;
	
	/**上车人数*/
	private int aboard;
	
	/**下车人数*/
	private int leave;
	
	public int getLogicChannel() {
		return logicChannel;
	}
	public void setLogicChannel(int logicChannel) {
		this.logicChannel = logicChannel;
	}
	public int getStreamType() {
		return streamType;
	}
	public void setStreamType(int streamType) {
		this.streamType = streamType;
	}
	public int getMemoryType() {
		return memoryType;
	}
	public void setMemoryType(int memoryType) {
		this.memoryType = memoryType;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getMediaType() {
		return mediaType;
	}
	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getA0() {
		return a0;
	}
	public void setA0(int a0) {
		this.a0 = a0;
	}
	public int getA1() {
		return a1;
	}
	public void setA1(int a1) {
		this.a1 = a1;
	}
	public int getA2() {
		return a2;
	}
	public void setA2(int a2) {
		this.a2 = a2;
	}
	public int getA3() {
		return a3;
	}
	public void setA3(int a3) {
		this.a3 = a3;
	}
	public int getA4() {
		return a4;
	}
	public void setA4(int a4) {
		this.a4 = a4;
	}
	public int getA5() {
		return a5;
	}
	public void setA5(int a5) {
		this.a5 = a5;
	}
	public int getA6() {
		return a6;
	}
	public void setA6(int a6) {
		this.a6 = a6;
	}
	public int getA7() {
		return a7;
	}
	public void setA7(int a7) {
		this.a7 = a7;
	}
	public int getA8() {
		return a8;
	}
	public void setA8(int a8) {
		this.a8 = a8;
	}
	public int getA9() {
		return a9;
	}
	public void setA9(int a9) {
		this.a9 = a9;
	}
	public int getA10() {
		return a10;
	}
	public void setA10(int a10) {
		this.a10 = a10;
	}
	public int getA11() {
		return a11;
	}
	public void setA11(int a11) {
		this.a11 = a11;
	}
	public int getA12() {
		return a12;
	}
	public void setA12(int a12) {
		this.a12 = a12;
	}
	public int getA13() {
		return a13;
	}
	public void setA13(int a13) {
		this.a13 = a13;
	}
	public int getA14() {
		return a14;
	}
	public void setA14(int a14) {
		this.a14 = a14;
	}
	public int getA15() {
		return a15;
	}
	public void setA15(int a15) {
		this.a15 = a15;
	}
	public int getA16() {
		return a16;
	}
	public void setA16(int a16) {
		this.a16 = a16;
	}
	public int getA17() {
		return a17;
	}
	public void setA17(int a17) {
		this.a17 = a17;
	}
	public int getA18() {
		return a18;
	}
	public void setA18(int a18) {
		this.a18 = a18;
	}
	public int getA19() {
		return a19;
	}
	public void setA19(int a19) {
		this.a19 = a19;
	}
	public int getA20() {
		return a20;
	}
	public void setA20(int a20) {
		this.a20 = a20;
	}
	public int getA21() {
		return a21;
	}
	public void setA21(int a21) {
		this.a21 = a21;
	}
	public int getA22() {
		return a22;
	}
	public void setA22(int a22) {
		this.a22 = a22;
	}
	public int getA23() {
		return a23;
	}
	public void setA23(int a23) {
		this.a23 = a23;
	}
	public int getA24() {
		return a24;
	}
	public void setA24(int a24) {
		this.a24 = a24;
	}
	public int getA25() {
		return a25;
	}
	public void setA25(int a25) {
		this.a25 = a25;
	}
	public int getA26() {
		return a26;
	}
	public void setA26(int a26) {
		this.a26 = a26;
	}
	public int getA27() {
		return a27;
	}
	public void setA27(int a27) {
		this.a27 = a27;
	}
	public int getA28() {
		return a28;
	}
	public void setA28(int a28) {
		this.a28 = a28;
	}
	public int getA29() {
		return a29;
	}
	public void setA29(int a29) {
		this.a29 = a29;
	}
	public int getA30() {
		return a30;
	}
	public void setA30(int a30) {
		this.a30 = a30;
	}
	public int getA31() {
		return a31;
	}
	public void setA31(int a31) {
		this.a31 = a31;
	}
	public int getA32() {
		return a32;
	}
	public void setA32(int a32) {
		this.a32 = a32;
	}
	public int getA33() {
		return a33;
	}
	public void setA33(int a33) {
		this.a33 = a33;
	}
	public int getA34() {
		return a34;
	}
	public void setA34(int a34) {
		this.a34 = a34;
	}
	public int getA35() {
		return a35;
	}
	public void setA35(int a35) {
		this.a35 = a35;
	}
	public int getA36() {
		return a36;
	}
	public void setA36(int a36) {
		this.a36 = a36;
	}
	public int getA37() {
		return a37;
	}
	public void setA37(int a37) {
		this.a37 = a37;
	}
	public int getA38() {
		return a38;
	}
	public void setA38(int a38) {
		this.a38 = a38;
	}
	public int getA39() {
		return a39;
	}
	public void setA39(int a39) {
		this.a39 = a39;
	}
	public int getA40() {
		return a40;
	}
	public void setA40(int a40) {
		this.a40 = a40;
	}
	public int getA41() {
		return a41;
	}
	public void setA41(int a41) {
		this.a41 = a41;
	}
	public int getA42() {
		return a42;
	}
	public void setA42(int a42) {
		this.a42 = a42;
	}
	public int getA43() {
		return a43;
	}
	public void setA43(int a43) {
		this.a43 = a43;
	}
	public int getA44() {
		return a44;
	}
	public void setA44(int a44) {
		this.a44 = a44;
	}
	public int getA45() {
		return a45;
	}
	public void setA45(int a45) {
		this.a45 = a45;
	}
	public int getA46() {
		return a46;
	}
	public void setA46(int a46) {
		this.a46 = a46;
	}
	public int getA47() {
		return a47;
	}
	public void setA47(int a47) {
		this.a47 = a47;
	}
	public int getA48() {
		return a48;
	}
	public void setA48(int a48) {
		this.a48 = a48;
	}
	public int getA49() {
		return a49;
	}
	public void setA49(int a49) {
		this.a49 = a49;
	}
	public int getA50() {
		return a50;
	}
	public void setA50(int a50) {
		this.a50 = a50;
	}
	public int getA51() {
		return a51;
	}
	public void setA51(int a51) {
		this.a51 = a51;
	}
	public int getA52() {
		return a52;
	}
	public void setA52(int a52) {
		this.a52 = a52;
	}
	public int getA53() {
		return a53;
	}
	public void setA53(int a53) {
		this.a53 = a53;
	}
	public int getA54() {
		return a54;
	}
	public void setA54(int a54) {
		this.a54 = a54;
	}
	public int getA55() {
		return a55;
	}
	public void setA55(int a55) {
		this.a55 = a55;
	}
	public int getA56() {
		return a56;
	}
	public void setA56(int a56) {
		this.a56 = a56;
	}
	public int getA57() {
		return a57;
	}
	public void setA57(int a57) {
		this.a57 = a57;
	}
	public int getA58() {
		return a58;
	}
	public void setA58(int a58) {
		this.a58 = a58;
	}
	public int getA59() {
		return a59;
	}
	public void setA59(int a59) {
		this.a59 = a59;
	}
	public int getA60() {
		return a60;
	}
	public void setA60(int a60) {
		this.a60 = a60;
	}
	public int getA61() {
		return a61;
	}
	public void setA61(int a61) {
		this.a61 = a61;
	}
	public int getA62() {
		return a62;
	}
	public void setA62(int a62) {
		this.a62 = a62;
	}
	public int getA63() {
		return a63;
	}
	public void setA63(int a63) {
		this.a63 = a63;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSeqNumber() {
		return seqNumber;
	}
	public void setSeqNumber(int seqNumber) {
		this.seqNumber = seqNumber;
	}
	public int getPassLength() {
		return passLength;
	}
	public void setPassLength(int passLength) {
		this.passLength = passLength;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public int getUserLength() {
		return userLength;
	}
	public void setUserLength(int userLength) {
		this.userLength = userLength;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPoot() {
		return poot;
	}
	public void setPoot(String poot) {
		this.poot = poot;
	}
	public int getPootLength() {
		return pootLength;
	}
	public void setPootLength(int pootLength) {
		this.pootLength = pootLength;
	}
	public int getRecordPosition() {
		return recordPosition;
	}
	public void setRecordPosition(int recordPosition) {
		this.recordPosition = recordPosition;
	}
	public int getTaskOp() {
		return taskOp;
	}
	public void setTaskOp(int taskOp) {
		this.taskOp = taskOp;
	}
	public int getControl() {
		return control;
	}
	public void setControl(int control) {
		this.control = control;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getAudioCodec() {
		return audioCodec;
	}
	public void setAudioCodec(int audioCodec) {
		this.audioCodec = audioCodec;
	}
	public int getChannels() {
		return channels;
	}
	public void setChannels(int channels) {
		this.channels = channels;
	}
	public int getSamplerate() {
		return samplerate;
	}
	public void setSamplerate(int samplerate) {
		this.samplerate = samplerate;
	}
	public int getSampleBits() {
		return sampleBits;
	}
	public void setSampleBits(int sampleBits) {
		this.sampleBits = sampleBits;
	}
	public int getSampleLength() {
		return sampleLength;
	}
	public void setSampleLength(int sampleLength) {
		this.sampleLength = sampleLength;
	}
	public int getEnableflag() {
		return enableflag;
	}
	public void setEnableflag(int enableflag) {
		this.enableflag = enableflag;
	}
	public int getVediocodec() {
		return vediocodec;
	}
	public void setVediocodec(int vediocodec) {
		this.vediocodec = vediocodec;
	}
	public int getAudiovhannels() {
		return audiovhannels;
	}
	public void setAudiovhannels(int audiovhannels) {
		this.audiovhannels = audiovhannels;
	}
	public int getVediovhannnels() {
		return vediovhannnels;
	}
	public void setVediovhannnels(int vediovhannnels) {
		this.vediovhannnels = vediovhannnels;
	}
	public int getAboard() {
		return aboard;
	}
	public void setAboard(int aboard) {
		this.aboard = aboard;
	}
	public int getLeave() {
		return leave;
	}
	public void setLeave(int leave) {
		this.leave = leave;
	}
	
}
