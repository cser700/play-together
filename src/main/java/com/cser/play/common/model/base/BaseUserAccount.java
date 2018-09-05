package com.cser.play.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserAccount<M extends BaseUserAccount<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}
	
	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	public void setAvailable(java.math.BigDecimal available) {
		set("available", available);
	}
	
	public java.math.BigDecimal getAvailable() {
		return get("available");
	}

	public void setFreeze(java.lang.Integer freeze) {
		set("freeze", freeze);
	}
	
	public java.lang.Integer getFreeze() {
		return getInt("freeze");
	}

	public void setFreezeTask(java.lang.Integer freezeTask) {
		set("freeze_task", freezeTask);
	}
	
	public java.lang.Integer getFreezeTask() {
		return getInt("freeze_task");
	}

}
