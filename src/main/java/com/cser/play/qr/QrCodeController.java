package com.cser.play.qr;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.jfinal.kit.Ret;

/**
 * 
 * @author res
 *
 */
public class QrCodeController extends BaseController{
	
	private QrCodeService srv = QrCodeService.ME;
	
	/**
	 * 查詢企业二维码
	 */
	public void getCode(){
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		Ret ret = srv.getQrCode(companyId);
		renderJson(ret);
	}
}
