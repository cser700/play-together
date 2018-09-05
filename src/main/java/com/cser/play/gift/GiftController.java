package com.cser.play.gift;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.Gift;
import com.cser.play.common.model.GiftComment;
import com.cser.play.message.NoticeService;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * 赠送
 * 
 * @author res
 *
 */
public class GiftController extends BaseController {
	GiftService srv = GiftService.ME;
	GiftLikeService likeSrv = GiftLikeService.ME;
	GiftCommentService commentSrv = GiftCommentService.ME;

	/**
	 * 赠送
	 */
	public void gift() {
		String jsonStr = getPara();
		String formId = JSONkit.jsonValue(jsonStr, "formId");
		System.out.println("################## formId = " + formId);
		Gift gift = getJSONObject(jsonStr, Gift.class);
		if (!StrKit.notNull(gift.getUserId(), gift.getAcceptUserId(), gift.getAmount())) {
			renderJson(Ret.fail("msg", "缺少参数"));
			return;
		}
		if (gift.getUserId() == gift.getAcceptUserId()) {
			renderJson(Ret.fail("msg", "送贝不能送给自己"));
			return;
		}
		if (StrKit.notBlank(formId)) {
			NoticeService.ME.save(gift.getUserId(), formId);
		}
		Ret ret = srv.gift(gift);
		renderJson(ret);
	}

	/**
	 * 赠送列表
	 */
	public void giftPage() {
		String jsonStr = getPara();
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		Page<Gift> page = srv.giftList(pageNum, companyId, userId);
		renderJson(page);
	}

	/**
	 * 点赞
	 */
	public void like() {
		String jsonStr = getPara();
		int giftId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "giftId"));
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		Ret ret = likeSrv.like(userId, giftId);
		renderJson(ret);
	}

	/**
	 * 点赞详情
	 */
	public void detail() {
		String jsonStr = getPara();
		int giftId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "giftId"));
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		Ret ret = likeSrv.detail(userId, giftId);
		renderJson(ret);
	}

	/**
	 * 评论
	 */
	public void comment() {
		GiftComment comment = getJSONObject(getPara(), GiftComment.class);
		if (!StrKit.notBlank(comment.getComment())) {
			renderJson(Ret.fail("msg", "评论不能为空"));
		}
		Ret ret = commentSrv.comment(comment);
		renderJson(ret);
	}

	/**
	 * 删除评论
	 */
	public void delete() {
		int id = Integer.parseInt(JSONkit.jsonValue(getPara(), "id"));
		Ret ret = commentSrv.delete(id);
		renderJson(ret);
	}

}
