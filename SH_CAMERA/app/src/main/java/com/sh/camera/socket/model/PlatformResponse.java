/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.model;

/**    
 *     
 * 项目名称：808    
 * 类名称：PlatformResponse    
 * 类描述：平台通用应答    
 * 创建人：Administrator    
 * 创建时间：2016年9月28日 上午11:13:37    
 * 修改人：Administrator    
 * 修改时间：2016年9月28日 上午11:13:37    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class PlatformResponse {
	
	/**应答流水号*/
	private int reseq;
	
	/**应答ID*/
	private int remsgid;
	
	/**结果  0：成功/确认；1：失败；2：消息有误；3：不支持 4:报警处理确认*/
	private int result;

	public int getReseq() {
		return reseq;
	}

	public void setReseq(int reseq) {
		this.reseq = reseq;
	}

	public int getRemsgid() {
		return remsgid;
	}

	public void setRemsgid(int remsgid) {
		this.remsgid = remsgid;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
	
}
