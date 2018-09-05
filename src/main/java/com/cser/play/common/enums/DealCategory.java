package com.cser.play.common.enums;

/**
 * 
 * @author res
 *
 */
public enum DealCategory {
	/**
	 * 收入
	 */
	income("收入", "1"),
	/**
	 * 支出
	 */
	expenditure("支出", "2"),
	/**
	 * 冻结
	 */
	freeze("冻结", "3"),
	/**
	 * 解冻
	 */
	thaw("解冻", "4");

	private String name;
	private String key;

	private DealCategory(String name, String key) {
		this.name = name;
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
