package com.cser.play.company;

import java.math.BigDecimal;

import com.cser.play.account.CompanyAccountService;
import com.cser.play.account.UserAccountService;
import com.cser.play.common.enums.DealCategory;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.model.ApplyList;
import com.cser.play.common.model.CompanyAccount;
import com.cser.play.common.model.CompanyInfo;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.UserInfo;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

public class CompanyService {
	public static CompanyService ME = new CompanyService();
	private CompanyInfo companyDao = new CompanyInfo();
	private UserInfo userDao = new UserInfo().dao();
	private ApplyList applyDao = new ApplyList().dao();

	private static final int DEFAULT_AMOUNT = 0;

	/**
	 * 注册公司
	 * 
	 * @param company
	 */
	public boolean regCompany(CompanyInfo company) {
		if (null != isExist(company.getCompanyName())) {
			return false;
		}
		boolean result = company.save();
		if (result) {
			// 初始化公司账号
			CompanyInfo cInfo = isExist(company.getCompanyName());
			CompanyAccount account = new CompanyAccount();
			int companyId = cInfo.getCompanyId();
			BigDecimal amount = new BigDecimal(DEFAULT_AMOUNT);
			account.setCompanyId(companyId);
			account.setAmount(amount);
			account.save();
			return true;
		}
		return false;
	}

	/**
	 * 根据公司名称查找
	 * @param companyName
	 * @return
	 */
	public CompanyInfo isExist(String companyName) {
		SqlPara sqlPara = companyDao.getSqlPara("company.findByName", companyName);
		return companyDao.findFirst(sqlPara);
	}

	/**
	 * 公司管理
	 * 
	 * @param adminUserId
	 * @param companyId
	 * @return
	 */
	public Ret manage(int adminUserId, int companyId) {
		CompanyInfo company = companyDao.findById(companyId);
		if (null == company) {
			return Ret.fail("msg", "公司不存在");
		}
		if (adminUserId != company.getAdminUserId()) {
			return Ret.fail("msg", "您不是管理员，没有权限");
		}
		SqlPara sqlPara = companyDao.getSqlPara("company.manage", companyId);
		CompanyInfo info = companyDao.findFirst(sqlPara);
		return Ret.create("company", info);
	}

	/**
	 * 申请加入公司
	 * 
	 * @param userId
	 * @param companyId
	 * @return
	 */
	public Ret apply(int userId, int companyId) {
		if (StrKit.notNull(userId, companyId)) {
			UserInfo user = userDao.findById(userId);
			if (null != user) {
				ApplyList apply = new ApplyList();
				apply.setUserId(userId);
				apply.setCompanyId(companyId);
				boolean result = apply.save();
				if (result) {
					return Ret.ok("msg", "成功");
				}
			}
		}
		return Ret.fail("msg", "加入失败");
	}

	/**
	 * 申请加入公司列表
	 * 
	 * @param companyId
	 * @param pageNum
	 * @return
	 */
	public Page<ApplyList> applyList(int companyId, int pageNum) {
		SqlPara sqlPara = applyDao.getSqlPara("", companyId, pageNum);
		return applyDao.paginate(pageNum, 10, sqlPara);
	}

	/**
	 * 批量员工加入审核
	 * 
	 * @param userArr
	 * @param companyId
	 * @param adminUserId
	 * @return
	 */
	public Ret reviewUser(String userArr, int companyId, int adminUserId) {
		CompanyInfo company = companyDao.findById(companyId);
		if (null != company && StrKit.notNull(company.getAdminUserId())) {
			if (company.getAdminUserId() != adminUserId) {
				Ret.fail("msg", "没有权限，请联系管理员");
			}
		} else {
			Ret.fail("msg", "参数错误，请联系管理员");
		}
		if (StrKit.notBlank(userArr)) {
			String users = "(" + userArr + ")";
			SqlPara sqlPara = userDao.getSqlPara("user.updateByUserIds", companyId, users);
			boolean result = Db.update(sqlPara) > 0;
			if (result) {
				Ret.ok("msg", "加入成功");
			}
		}
		return Ret.fail("msg", "加入失败");
	}

	/**
	 * 分配牛贝
	 * 
	 * @param userArr
	 * @param companyId
	 * @param adminUserId
	 * @param amount
	 *            每个人分配数量
	 * @return
	 */
	public Ret assign(String userArr, int companyId, int adminUserId, int amount) {
		CompanyInfo company = companyDao.findById(companyId);
		if (null != company && StrKit.notNull(company.getAdminUserId(), userArr, amount)) {
			if (company.getAdminUserId() != adminUserId) {
				Ret.fail("msg", "没有权限，请联系管理员");
			}
		} else {
			Ret.fail("msg", "参数错误，请联系管理员");
		}
		UserInfo adminUser = UserInfoService.ME.findUser(adminUserId);
		String[] users = userArr.split(",");
		int totalAmount = users.length * amount;

		CompanyAccount companyAccount = CompanyAccountService.ME.findCompanyAccount(companyId);
		// 判断公司余额
		if (-1 == companyAccount.getAmount().compareTo(BigDecimal.valueOf(totalAmount))) {
			return Ret.fail("msg", "公司账户余额不足，请充值");
		}

		// 分发
		boolean result = UserAccountService.ME.assign(userArr, amount, companyId);
		String userNameArr = "";
		if (result) {
			for (int i = 0; i < users.length; i++) {
				// 增加获得交易记录
				DealRecord dr = new DealRecord();
				dr.setUserId(Integer.parseInt(users[i]));
				dr.setAmount(BigDecimal.valueOf(amount));
				dr.setDealType(DealRecord.DEAL_GET_COMPANY_SHARE);
				dr.setDealDesc(DealRecord.DEAL_GET_COMPANY_SHARE_DESC);
				dr.setAcceptUserId(adminUserId + "");
				dr.setAcceptUserName(adminUser.getNickName());
				dr.setStatus(DealStatus.success.getKey());
				dr.setCategory(DealCategory.income.getKey());
				dr.save();
				UserInfo user = UserInfoService.ME.findUser(Integer.parseInt(users[i]));
				if (i == 0) {
					userNameArr = user.getNickName();
				} else {
					userNameArr += "," + user.getNickName();
				}
			}
			// 增加分配交易记录
			DealRecord deal = new DealRecord();
			deal.setUserId(adminUserId);
			deal.setAmount(BigDecimal.valueOf(totalAmount));
			deal.setDealType(DealRecord.DEAL_COMPANY_SHARE);
			deal.setDealDesc(DealRecord.DEAL_COMPANY_SHARE_DESC.replace("{0}", users.length + "").replace("{1}",
					totalAmount + ""));
			deal.setAcceptUserId(userArr);
			deal.setAcceptUserName(userNameArr);
			deal.setStatus(DealStatus.success.getKey());
			deal.setCategory(DealCategory.expenditure.getKey());
			deal.save();

			// 修改公司账号余额
			CompanyAccountService.ME.updateAmount(companyId, totalAmount);

			return Ret.ok("msg", "分配成功");
		}
		return Ret.fail("msg", "分配失败");
	}

	/**
	 * 根据公司名称查找公司
	 * @param companyName
	 */
	public CompanyInfo querCompanyByName(String companyName) {
		SqlPara sqlPara = companyDao.getSqlPara("company.findByName", companyName);
		return companyDao.findFirst(sqlPara);
	}
	
	/**
	 * 根据公司ID查找公司
	 * @param companyName
	 */
	public CompanyInfo querCompanyById(int companyId) {
		return companyDao.findById(companyId);
	}


}
