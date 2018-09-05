package com.cser.play.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGift<M extends BaseGift<M>> extends Model<M> implements IBean {

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

	public void setAcceptUserId(java.lang.Integer acceptUserId) {
		set("accept_user_id", acceptUserId);
	}
	
	public java.lang.Integer getAcceptUserId() {
		return getInt("accept_user_id");
	}

	public void setAmount(java.lang.Integer amount) {
		set("amount", amount);
	}
	
	public java.lang.Integer getAmount() {
		return getInt("amount");
	}

	public void setDesc(java.lang.String desc) {
		set("desc", desc);
	}
	
	public java.lang.String getDesc() {
		return getStr("desc");
	}

	public void setImageUrl(java.lang.String imageUrl) {
		set("image_url", imageUrl);
	}
	
	public java.lang.String getImageUrl() {
		return getStr("image_url");
	}

	public void setCreateDate(java.util.Date createDate) {
		set("create_date", createDate);
	}
	
	public java.util.Date getCreateDate() {
		return get("create_date");
	}

	public void setLikeList(java.lang.String likeList) {
		set("likeList", likeList);
	}
	
	public java.lang.String getLikeList() {
		return getStr("likeList");
	}

	public void setCommentList(java.lang.String commentList) {
		set("commentList", commentList);
	}
	
	public java.lang.String getCommentList() {
		return getStr("commentList");
	}

	public void setFlag(java.lang.String flag) {
		set("flag", flag);
	}
	
	public java.lang.String getFlag() {
		return getStr("flag");
	}

}