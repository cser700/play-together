package com.cser.play.account;

import com.cser.play.common.model.CompanyAccount;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;

public class CompanyAccountService {
	public static CompanyAccountService ME = new CompanyAccountService();
	private CompanyAccount dao = new CompanyAccount();

	/**
	 * 修改公司账户余额
	 * 
	 * @param comapnyId
	 * @param amount
	 */
	public boolean updateAmount(int companyId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("companyAccount.updateAmount", amount, companyId);
		return Db.update(sqlPara) > 0;
	}
	
	/**
	 * 根据compangId查询公司账户
	 * @param companyId
	 * @return
	 */
	public CompanyAccount findCompanyAccount(int companyId){
		SqlPara sqlPara = dao.getSqlPara("companyAccount.findCompanyAccount", companyId);
		return dao.findFirst(sqlPara);
	}

}
