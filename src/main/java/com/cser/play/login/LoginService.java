package com.cser.play.login;

import com.alibaba.fastjson.JSONObject;
import com.cser.play.account.UserAccountService;
import com.cser.play.common.PlayConfig;
import com.cser.play.common.model.Session;
import com.cser.play.common.model.UserInfo;
import com.cser.play.common.utils.MiniUtils;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 登录业务
 * 
 * @author res
 *
 */
public class LoginService {
	public static final LoginService ME = new LoginService();
	private UserInfo userDao = new UserInfo();

	private static Prop p = PlayConfig.p;

	private static final String APP_ID = p.get("mini_program_app_id");
	private static final String APP_SECRET = p.get("mini_program_app_secret");
	private static final String LOGIN_USER = "loginUser";

	/**
	 * 
	 * @param jsCode
	 *            小程序调用wx.login返回的code
	 * @param encryptedData
	 *            包括敏感数据在内的完整用户信息的加密数据
	 * @param iv
	 *            加密算法的初始向量
	 * @return
	 */

	public Ret wxLogin(String jsCode, String encryptedData, String iv) {
		// 请求微信登录api, 返回session_key, openId
		JSONObject responseJson = MiniUtils.getSessionKeyOropenid(jsCode);

		String sessionKey = responseJson.getString("session_key");
		String openId = responseJson.getString("openid");
		// 判断如果是首次登录
		UserInfo userInfo = UserInfoService.ME.findByOpenId(openId);

		if (!StrKit.notBlank(encryptedData, iv) && null == userInfo) {
			// 查询openid并返回，opendid、status=0
			return Ret.ok("msg", "登录成功").set("openid", openId).set("sessionKey", sessionKey).set("status", "0");
		}

		// userInfo数据解析
		if (!StrKit.notBlank(encryptedData, iv)) {
			JSONObject userJson = MiniUtils.getUserInfo(encryptedData, sessionKey, iv);
			String openid = userJson.getString("openid");
			String unionid = userJson.getString("unionid");
			String nickName = userJson.getString("nickName");
			String gender = userJson.getString("gender");
			String country = userJson.getString("country");
			String province = userJson.getString("province");
			String city = userJson.getString("city");
			String avatarUrl = userJson.getString("avatarUrl");
			String language = userJson.getString("language");

			// 查询不到数据,则是首次登录，则将用户信息存入数据库
			if (null == userInfo) {
				UserInfo user = new UserInfo();
				user.setOpenid(openid);
				user.setUnionid(unionid);
				user.setNickName(nickName);
				user.setGender(gender);
				user.setCountry(country);
				user.setProvince(province);
				user.setCity(city);
				user.setAvatarUrl(avatarUrl);
				user.setLanguage(language);
				user.save();
			}
		}

		UserInfo finalUser = UserInfoService.ME.findByOpenId(openId);
		return Ret.ok("msg", "登录成功").set("openId", openId).set("sessionKey", sessionKey).set("userInfo", finalUser);
	}

	/**
	 * 查询openid
	 * 
	 * @param code
	 * @return
	 */
	public Ret getOpenId(String code) {
		JSONObject responseJson = MiniUtils.getSessionKeyOropenid(code);
		if (responseJson.toString().contains("errcode")) {
			return Ret.fail().set("error", responseJson);
		}
		String openid = responseJson.getString("openid");
		String unionid = responseJson.getString("unionid");
		if (StrKit.notBlank(openid)) {
			int companyId = 0;
			UserInfo userInfo = UserInfoService.ME.findByOpenId(openid);
			if (null != userInfo) {
				if (StrKit.notNull(userInfo.getCompanyId())) {
					companyId = userInfo.getCompanyId();
				}
			}
			if (null == userInfo) {
				UserInfo user = new UserInfo();
				user.setOpenid(openid);
				user.setUnionid(unionid);
				user.setStatus("0");
				user.save();
				// 初始化账户 userAccount
				UserInfo resultUser = UserInfoService.ME.findByOpenId(openid);
				UserAccountService.ME.initUserAccount(resultUser.getUserId());
				return Ret.ok("msg", "登录成功").set("openid", openid).set("status", user.getStatus())
						.set("userId", user.getUserId()).set("companyId", companyId);
			} else {
				return Ret.ok("msg", "登录成功").set("openid", openid).set("status", userInfo.getStatus())
						.set("userId", userInfo.getUserId()).set("companyId", companyId)
						.set("nickName", userInfo.get("nickName"));
			}
		}
		return null;
	}

	/**
	 * 通过 sessionId 获取登录用户信息 sessoin表结构：session(id, userId, expireTime)
	 *
	 * 1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录，
	 * 如果没过期则先放缓存一份，然后再返回
	 */
	public UserInfo loginWithSessionId(String sessionId, String loginIp) {
		Session session = Session.dao.findById(sessionId);
		// session 不存在
		if (session == null) {
			return null;
		}
		// session 已过期
		if (session.isExpired()) {
			// 被动式删除过期数据，此外还需要定时线程来主动清除过期数据 TODO
			session.delete();
			return null;
		}
		UserInfo loginUser = userDao.findById(session.getUserId());
		// 找到 loginUser
		if (loginUser != null) {
			// 保存一份 sessionId 到 loginUser 备用
			loginUser.put("sessionId", sessionId);
			CacheKit.put(LOGIN_USER, sessionId, loginUser);
			return loginUser;
		}
		return null;
	}

	/**
	 * 获得 access_token
	 * 
	 * @return
	 */
	public String getAccessToken() {
		return MiniUtils.getAccessToken(APP_ID, APP_SECRET);
	}

}
