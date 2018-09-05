package com.cser.play.apply;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.ApplyList;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

public class ApplyController extends BaseController {
	private ApplyService srv = ApplyService.ME;

	/**
	 * 申請列表
	 */
	public void applyPage() {
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		Page<ApplyList> page = srv.applyPage(companyId, pageNum);
		renderJson(page);
	}

	/**
	 * 审核
	 */
	public void acceptUser() {
		String jsonStr = getPara();
		int id = Integer.parseInt(JSONkit.jsonValue(jsonStr, "id"));
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		Ret ret = srv.acceptUser(id, userId);
		renderJson(ret);
	}
}
