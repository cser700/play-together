package com.cser.play.reg;

import java.util.regex.Pattern;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.BeanKit;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.kit.MyHttpKit;
import com.cser.play.common.model.CompanyInfo;
import com.cser.play.common.model.SmsCode;
import com.cser.play.common.model.UserInfo;
import com.cser.play.company.CompanyService;
import com.cser.play.message.NoticeService;
import com.cser.play.sms.SMSService;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

public class RegController extends BaseController {
	RegService srv = RegService.ME;
	SMSService smsSrv = SMSService.ME;
	CompanyService companySrv = CompanyService.ME;
	UserInfoService userSrv = UserInfoService.ME;

	/**
	 * 注册企业用户
	 */
	public void regCompany() {
		String jsonStr = getPara();
		UserInfo user = getJSONObject(jsonStr, UserInfo.class);
		String companyName = JSONkit.jsonValue(jsonStr, "companyName");
		UserInfo loginUser = userSrv.findByOpenId(user.getOpenid());
		UserInfo resultUser = null;
		CompanyInfo resultCompany = null;
		
		// 注册公司
		if (StrKit.notBlank(companyName)) {
			CompanyInfo company = new CompanyInfo();
			company.setCompanyName(companyName);
			company.setAdminUserId(loginUser.getUserId());
			boolean companyResult = companySrv.regCompany(company);
			if (!companyResult) {
				renderJson(Ret.fail("msg", "注册失败"));
				return;
			}
			resultCompany = companySrv.querCompanyByName(companyName);
			loginUser.setCompanyId(resultCompany.getCompanyId());
		}

		// 注册用户
		if (!StrKit.notBlank(user.getOpenid(), user.getPosition(), user.getMobile(), user.getRealName(), companyName,
				user.getNickName(), user.getAvatarUrl())) {
			renderJson(Ret.fail("msg", "缺失参数，注册失败"));
			return;
		}
		try {
			// 复制属性
			BeanKit.copyPropertiesInclude(user, loginUser, new String[] { "nickName", "avatarUrl", "city", "country",
					"gender", "province", "language", "position", "realName", "mobile" });
			loginUser.setAdmin("1");
			loginUser.setStatus("2");
			boolean result = userSrv.regCompany(loginUser);
			if (!result) {
				renderJson(Ret.fail("msg", "注册失败"));
				return;
			}
			resultUser = userSrv.findByOpenId(user.getOpenid());
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			e.printStackTrace();
		}
		String formId = JSONkit.jsonValue(jsonStr, "formId");
		if (StrKit.notBlank(formId)) {
			NoticeService.ME.save(resultUser.getUserId(), formId);
		}
		renderJson(Ret.ok("msg", "注册成功").set("userInfo", resultUser));

	}

	/**
	 * 注册普通用户
	 */
	public void regUser() {
		UserInfo user = getJSONObject(getPara(), UserInfo.class);
		if (user.getCompanyId() > 0 && StrKit.notNull(user.getStatus())) {
			renderJson(Ret.fail("msg", "管理员正在审核中...请稍等"));
			return;
		}
		if (!StrKit.notBlank(user.getAvatarUrl(), user.getNickName(), user.getOpenid())) {
			renderJson(Ret.fail("msg", "缺失参数，注册失败"));
			return;
		}
		Ret ret = userSrv.regUser(user);
		renderJson(ret);
	}
	
	/**
	 * 注册游客
	 */
	public void regTourist() {
		UserInfo user = getJSONObject(getPara(), UserInfo.class);
		if (!StrKit.notBlank(user.getAvatarUrl(), user.getNickName(), user.getOpenid())) {
			renderJson(Ret.fail("msg", "缺失参数，注册失败"));
			return;
		}
		Ret ret = userSrv.regTourist(user);
		renderJson(ret);
	}

	/**
	 * 发送验证码
	 */
	public void sendSms() {
		SmsCode sms = getJSONObject(MyHttpKit.readData(getRequest()), SmsCode.class);
		Ret ret = smsSrv.sendSmsCode(sms.getMobile());
		renderJson(ret);
	}

	/**
	 * 校验手机号码是否存在
	 */
	public void valiMobile() {
		String mobile = JSONkit.jsonValue(getPara(), "mobile");
		if (!StrKit.notBlank(mobile)) {
			renderJson(Ret.fail("msg", "手机号码不能为空"));
			return;
		}
		String mobileRegex = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
		boolean regResult = Pattern.matches(mobileRegex, mobile);
		if (!regResult) {
			renderJson(Ret.fail("msg", "请输入正确的手机格式"));
			return;
		}
		UserInfo user = userSrv.findByMobile(mobile);
		if (null != user) {
			renderJson(Ret.fail("msg", "手机号码已存在"));
		} else {
			renderJson(Ret.ok("msg", "可以注册"));
		}
	}

	/**
	 * 校验公司名称是否存在
	 */
	public void valiCompanyName() {
		String companyName = JSONkit.jsonValue(getPara(), "companyName");
		if (!StrKit.notBlank(companyName)) {
			renderJson(Ret.fail("msg", "公司不能为空"));
			return;
		}
		CompanyInfo company = companySrv.isExist(companyName);
		if (null != company) {
			renderJson(Ret.fail("msg", "公司名称已存在"));
		} else {
			renderJson(Ret.ok("msg", "可以注册"));
		}
	}

	/**
	 * 校验验证码
	 */
	public void valiCode() {
		SmsCode sms = getJSONObject(MyHttpKit.readData(getRequest()), SmsCode.class);
		Ret ret = smsSrv.valiSmsCode(sms.getMobile(), sms.getCode() + "");
		renderJson(ret);
	}
	
	/**
	 * 根据openid 判断用户是否存在
	 */
	public void isExistsUser() {
		String openid = JSONkit.jsonValue(getPara(), "openid");		
		UserInfo loginUser = userSrv.findByOpenId(openid);
		if (null == loginUser) {
			renderJson(false);
			return;
		} 
		boolean result;
		if (StrKit.notBlank(loginUser.get("nickName").toString())){
			result = true;
		} else {
			result = false;
		}
		renderJson(result);
	}

}
