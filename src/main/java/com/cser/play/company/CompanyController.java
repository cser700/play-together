package com.cser.play.company;

import java.util.List;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.ApplyList;
import com.cser.play.common.model.CompanyInfo;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.UserInfo;
import com.cser.play.deal.DealRecordService;
import com.cser.play.message.NoticeService;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

public class CompanyController extends BaseController {

	private CompanyService srv = CompanyService.ME;
	private UserInfoService userSrv = UserInfoService.ME;
	private DealRecordService dealSrv = DealRecordService.ME;

	/**
	 * 注册公司
	 */
	public void reg() {
		CompanyInfo company = getJSONObject(getPara(), CompanyInfo.class);
		boolean result = srv.regCompany(company);
		if (result) {
			renderJson(Ret.ok("msg","注册成功"));
		} else {
			renderJson(Ret.fail("msg","注册失败"));
		}
	}
	
	/**
	 * 公司管理
	 */
	public void manage(){
		String jsonStr = getPara();
		int adminUserId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "adminUserId"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		Ret ret = srv.manage(adminUserId, companyId);
		renderJson(ret);
	}


	/**
	 * 员工申请
	 */
	public void invite() {
		ApplyList apply = getJSONObject(getPara(), ApplyList.class);
		Ret ret = srv.apply(apply.getUserId(), apply.getCompanyId());
		renderJson(ret);
	}

	/**
	 * 员工申请加入公司列表
	 */
	public void applyList() {
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		Page<ApplyList> page = srv.applyList(companyId, pageNum);
		renderJson(page);
	}

	/**
	 * 分发牛贝
	 */
	public void assign() {
		String jsonStr = getPara();
		String userArr = JSONkit.jsonValue(jsonStr, "userArr");
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		int adminUserId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "adminUserId"));
		int amount = Integer.parseInt(JSONkit.jsonValue(jsonStr, "amount"));
		
		String formId = JSONkit.jsonValue(jsonStr, "formId");
		if (StrKit.notBlank(formId)) {
			NoticeService.ME.save(adminUserId, formId);
		}
		
		Ret ret = srv.assign(userArr, companyId, adminUserId, amount);
		renderJson(ret);
	}

	/**
	 * 员工申请加入审核
	 */
	public void review() {
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		int adminUserId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "adminUserId"));
		// 用户ID数组
		String userArr = JSONkit.jsonValue(jsonStr, "userArr");
		Ret ret = srv.reviewUser(userArr, companyId, adminUserId);
		renderJson(ret);
	}
	
	/**
	 * 分发牛贝-用户列表
	 */
	public void findUserByCompanyId(){
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		List<UserInfo> list = userSrv.userPage(companyId, null);
		renderJson(list);
	}
	
	/**
	 * 公司分配记录
	 */
	public void assignPage(){
		String jsonStr = getPara();
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		int adminUserId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "adminUserId"));		
		Page<DealRecord> page = dealSrv.assignPage(pageNum, adminUserId, companyId);
		renderJson(page);
	}
	
	/**
	 * 查找公司
	 */
	public void query(){
		String jsonStr = getPara();
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		CompanyInfo company = srv.querCompanyById(companyId);
		renderJson(company);
	}
}
