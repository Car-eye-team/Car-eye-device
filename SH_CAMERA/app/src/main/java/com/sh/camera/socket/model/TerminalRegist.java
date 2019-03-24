/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */

package com.sh.camera.socket.model;

/**    
 *     
 * 项目名称808    
 * 类名称：TerminalRegist    
 * 类描述：终端注册    
 * 创建人：Administrator    
 * 创建时间：2016年9月28日 下午3:22:16    
 * 修改人：Administrator    
 * 修改时间：2016年9月28日 下午3:22:16    
 * 修改备注：    
 * @version 1.0  
 *     
 */
public class TerminalRegist {
	
	/**应答流水号*/
	private int reseq;
	
	/**结果  0：成功；1：车辆已被注册；2：数据库中无该车辆；3：终端已被注册；4：数据库中无该终端*/
	private int result;

	public int getReseq() {
		return reseq;
	}

	public void setReseq(int reseq) {
		this.reseq = reseq;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
	
}
