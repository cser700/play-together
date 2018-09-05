package com.cser.play.reg;

import com.cser.play.sms.SMSService;
import com.jfinal.kit.Ret;

/**
 * 企业注册
 * @author res
 *
 */
public class RegService {
	public static RegService ME = new RegService();
	
	
	
	public void regCompany(){
		
	}
	
	
	/**
	 * 发送验证码
	 * @param phone
	 * @return
	 */
	public Ret sendSmsCode(String phone){
		return SMSService.ME.sendSmsCode(phone);
	}
	
	/**
	 * 校验验证码
	 */
	public Ret valiSmsCode(String phone, String smsCode){
		return SMSService.ME.valiSmsCode(phone, smsCode);
	}
	
	
}
