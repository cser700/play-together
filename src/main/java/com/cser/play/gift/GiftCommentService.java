package com.cser.play.gift;

import java.util.List;

import com.cser.play.common.model.GiftComment;
import com.cser.play.common.model.UserInfo;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;

public class GiftCommentService {
	public static final GiftCommentService ME = new GiftCommentService();

	private GiftComment dao = new GiftComment();

	/**
	 * 评论
	 * 
	 * @param giftId
	 * @param userId
	 * @return
	 */
	public Ret comment(GiftComment gc) {
		UserInfo user = UserInfoService.ME.findUser(gc.getUserId());
		GiftComment comment = new GiftComment();
		comment.setGiftId(gc.getGiftId());
		comment.setUserId(gc.getUserId());
		comment.setNickName(user.getNickName());
		comment.setComment(gc.getComment());
		if (null != gc.getReplyUserId()) {
			comment.setReplyUserId(gc.getReplyUserId());
			UserInfo replayUser = UserInfoService.ME.findUser(gc.getReplyUserId());
			comment.setReplyNickName(replayUser != null ? replayUser.getNickName() : "");
		}
		boolean result = comment.save();
		if (result) {
			List<GiftComment> list = commentList(gc.getGiftId());
			return Ret.ok("msg", "评论成功").set("list", list);
		} else {
			return Ret.fail("msg", "评论失败");
		}
	}

	/**
	 * 评论列表
	 * 
	 * @param giftId
	 * @return
	 */
	public List<GiftComment> commentList(int giftId) {
		SqlPara sqlPara = dao.getSqlPara("giftComment.findCommentList", giftId);
		return dao.find(sqlPara);
	}

	/**
	 * 删除评论
	 * @param commentId
	 * @return
	 */
	public Ret delete(int commentId) {
		boolean result = Db.delete("delete from gift_comment where id = ?", commentId) > 0 ? true : false;
		if (result) {
			return Ret.ok("msg", "删除成功");
		} else {
			return Ret.fail("msg", "删除失败");
		}
	}

}
