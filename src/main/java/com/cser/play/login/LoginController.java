package com.cser.play.login;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

/**
 * 登录
 * 
 * @author res
 *
 */
public class LoginController extends BaseController {
	LoginService srv = LoginService.ME;

	/**
	 * 微信登录
	 */
	public void login() {
		String jsonStr = getPara();
		String jsCode = JSONkit.jsonValue(jsonStr, "code");
		if (StrKit.notBlank(jsCode)) {
			// 查询openid并返回，opendid、status=0
			Ret ret = srv.getOpenId(jsCode);
			renderJson(ret);
		} else {
			renderJson(Ret.fail("msg", "缺少参数，登录失败"));
		}
	}
	

}
