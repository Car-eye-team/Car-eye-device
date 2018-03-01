/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.model;

/**
 *     
 * 项目名称：808    
 * 类名称：DSCommData    
 * 类描述：    
 * 创建人：Administrator    
 * 创建时间：2016年9月28日 下午2:52:33    
 * 修改人：Administrator    
 * 修改时间：2016年9月28日 下午2:52:33    
 * 修改备注：    
 * @version 1.0  
 *
 */
public class DSCommData {
	
	private int id;

	/**消息体流水号*/
	/**按发送顺序从0开始循环累加*/
	private int seq;

	/**应答流水号*/
	private int reseq;

	/**消息ID*/
	private int msgid;
	
	/**结果 0 成功 1 失败 2 消息有误 3 不支持*/
	private int result;

	private String datahex;
	
	private String createtime;
	
	/**消息总包数*/
	private int pkgCount;
	
	/**消息包序号*/
	private int pkgNum;
	
	/**终端设备号*/
	private String terminal;
	
	private byte[] data;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getReseq() {
		return reseq;
	}

	public void setReseq(int reseq) {
		this.reseq = reseq;
	}

	public int getMsgid() {
		return msgid;
	}

	public void setMsgid(int msgid) {
		this.msgid = msgid;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getDatahex() {
		return datahex;
	}

	public void setDatahex(String datahex) {
		this.datahex = datahex;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public int getPkgCount() {
		return pkgCount;
	}

	public void setPkgCount(int pkgCount) {
		this.pkgCount = pkgCount;
	}

	public int getPkgNum() {
		return pkgNum;
	}

	public void setPkgNum(int pkgNum) {
		this.pkgNum = pkgNum;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	
}
