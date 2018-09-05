package com.cser.play.user;

import java.util.List;

import com.cser.play.account.UserAccountService;
import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.kit.MyHttpKit;
import com.cser.play.common.model.UserAccount;
import com.cser.play.common.model.UserInfo;
import com.jfinal.kit.Ret;

/**
 * 用户信息
 * 
 * @author res
 *
 */
public class UserInfoController extends BaseController {

	UserInfoService srv = UserInfoService.ME;

	/**
	 * 查询主键
	 */
	public void next() {
		renderText(srv.nextPrimaryKey() + "");
	}

	/**
	 * 查询用户
	 */
	public void findUser() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		UserInfo user = srv.findUser(userId);
		renderJson(Ret.create("status", user.getStatus()));
	}

	/**
	 * 修改用户信息：姓名，性别，邮箱
	 */
	public void updateUser() {
		UserInfo user = getJSONObject(MyHttpKit.readData(getRequest()), UserInfo.class);
		renderJson(srv.updateUserInfo(user));
	}

	/**
	 * 个人中心
	 */
	public void center() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		UserInfo user = srv.userCenter(userId);
		if (null == user) {
			renderJson(Ret.ok("msg", "未查询到数据"));
		} else {
			renderJson(user);
		}
	}

	/**
	 * 搜索用户
	 */
	public void search() {
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		String condition = JSONkit.jsonValue(jsonStr, "condition");
		List<UserInfo> list = srv.searchUser(companyId, condition);
		if (list.size() > 0) {
			renderJson(list);
		} else {
			renderJson(Ret.ok("msg", "没有查找到用户"));
		}
	}

	/**
	 * 用户列表
	 */
	public void userPage() {
		String jsonStr = getPara();
		String userId = JSONkit.jsonValue(jsonStr, "userId");
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		List<UserInfo> list = srv.userPage(companyId, userId);
		renderJson(list);
	}

	/**
	 * 查询账户余额
	 */
	public void queryAmount() {
		int userId = Integer.parseInt(JSONkit.jsonValue(getPara(), "userId"));
		UserAccount account = UserAccountService.ME.queryBalance(userId);
		renderJson(Ret.create("freeze", account.getFreeze()).set("available", account.getAvailable()));
	}
}
