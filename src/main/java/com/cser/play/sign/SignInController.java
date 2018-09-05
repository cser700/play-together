package com.cser.play.sign;

import java.util.Date;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.SignIn;
import com.cser.play.common.utils.PlayDateUtil;
import com.cser.play.message.NoticeService;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

/**
 * 签到
 * 
 * @author res
 *
 */
public class SignInController extends BaseController {

	SignInService srv = SignInService.ME;

	/**
	 * 用户签到
	 */
	public void sign() {
		String jsonStr = getPara();
		SignIn sign = getJSONObject(jsonStr, SignIn.class);
		String formId = JSONkit.jsonValue(jsonStr, "formId");
		if (StrKit.notBlank(formId)) {
			NoticeService.ME.save(sign.getUserId(), formId);
		}
		Ret ret = srv.sign(sign.getUserId(), "");
		renderJson(ret);
	}

	/**
	 * 补签
	 */
	public void supplement() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		String formId = JSONkit.jsonValue(jsonStr, "formId");
		if (StrKit.notBlank(formId)) {
			NoticeService.ME.save(userId, formId);
		}
		String supplement = "supplement";
		Ret ret = srv.sign(userId, supplement);
		renderJson(ret);
	}

	/**
	 * 获取当月签到列表
	 */
	public void signList() {
		SignIn sign = getJSONObject(getPara(), SignIn.class);
		Ret ret = srv.signList(sign.getUserId());
		renderJson(ret);
	}

	/**
	 * 是否签到
	 */
	public void whether() {
		SignIn sign = getJSONObject(getPara(), SignIn.class);
		String nowDate = PlayDateUtil.dateToStr(new Date());
		boolean result = srv.isSignIn(sign.getUserId() + "", nowDate);
		renderJson(result);
	}

	/**
	 * 用户签到信息
	 */
	public void info() {
		int userId = Integer.parseInt(JSONkit.jsonValue(getPara(), "userId"));
		Ret ret = srv.info(userId);
		renderJson(ret);
	}

}
