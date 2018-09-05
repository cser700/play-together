package com.cser.play.common.enums;

/**
 * 
 * @author res
 *
 */
public enum DealStatus {
	/**
	 * 进行中
	 */
	progress("进行中", "0"), 
	/**
	 * 成功
	 */
	success("成功", "1"), 
	/**
	 * 失败
	 */
	fail("失败", "2");

	private String name;
	private String key;

	private DealStatus(String name, String key) {
		this.key = key;
		this.name = name;
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
