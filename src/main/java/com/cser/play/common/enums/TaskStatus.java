package com.cser.play.common.enums;

/**
 * 
 * @author res
 *
 */
public enum TaskStatus {
	/**
	 * 发布无人参加
	 */
	publish("发布无人参加", "0"), 
	/**
	 * 完成
	 */
	end("完成", "1"), 
	/**
	 * 参加人数未满
	 */
	someone("参加人数未满", "2"), 
	/**
	 * 进行中
	 */
	processing("进行中", "3"), 
	/**
	 * 任务删除
	 */
	delete("任务删除", "4");

	private String name;
	private String key;

	private TaskStatus(String name, String key) {
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
