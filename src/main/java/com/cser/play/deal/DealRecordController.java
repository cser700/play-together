package com.cser.play.deal;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.DealRecord;
import com.jfinal.plugin.activerecord.Page;

/**
 * 交易记录
 * 
 * @author res
 *
 */
public class DealRecordController extends BaseController {

	DealRecordService srv = DealRecordService.ME;

	/**
	 * 周榜
	 */
	public void weekList() {
		String jsonStr = getPara();
		String today = JSONkit.jsonValue(jsonStr, "today");
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		renderJson(srv.getWeekList(today, pageNum, companyId));
	}

	/**
	 * 月榜
	 */
	public void monthList() {
		String jsonStr = getPara();
		String today = JSONkit.jsonValue(jsonStr, "today");
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		renderJson(srv.getMonthList(today, pageNum, companyId));
	}

	/**
	 * 年榜
	 */
	public void yearList() {
		String jsonStr = getPara();
		String today = JSONkit.jsonValue(jsonStr, "today");
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		renderJson(srv.getYearList(today, pageNum, companyId));
	}

	/**
	 * 交易记录-收入
	 */
	public void incomeList() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		Page<DealRecord> page = srv.incomeList(userId, pageNum);
		renderJson(page);
	}

	/**
	 * 交易记录-支出
	 */
	public void expenditureList() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		Page<DealRecord> page = srv.expenditureList(userId, pageNum);
		renderJson(page);
	}

	/**
	 * 交易记录-所有
	 */
	public void dealAllList() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		int targetUserId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "targetUserId"));
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		Page<DealRecord> page = srv.dealAllList(userId, targetUserId, pageNum);
		renderJson(page);
	}

}
