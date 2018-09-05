package com.cser.play.common.model;

import java.math.BigDecimal;

import com.cser.play.common.model.base.BaseCompanyInfo;

/**
 * @author res
 */
@SuppressWarnings("serial")
public class CompanyInfo extends BaseCompanyInfo<CompanyInfo> {
	
	/**
	 * 公司余额
	 */
	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
	
}
