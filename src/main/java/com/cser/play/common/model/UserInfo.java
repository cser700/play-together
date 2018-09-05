package com.cser.play.common.model;

import com.cser.play.common.model.base.BaseUserInfo;

/**
 * @author res
 */
@SuppressWarnings("serial")
public class UserInfo extends BaseUserInfo<UserInfo> {

	/**
	 * 获得牛贝
	 */
	private int getAmount;

	/**
	 * 赠出牛贝
	 */
	private int payAmount;

	/**
	 * 冻结牛贝
	 */
	private int freeze;
	
	/**
	 * 公司名称
	 */
	private String companyName;

	public int getGetAmount() {
		return getAmount;
	}

	public void setGetAmount(int getAmount) {
		this.getAmount = getAmount;
	}

	public int getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(int payAmount) {
		this.payAmount = payAmount;
	}

	public int getFreeze() {
		return freeze;
	}

	public void setFreeze(int freeze) {
		this.freeze = freeze;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	
}
