package com.cser.play.gift;

import java.util.List;

import com.cser.play.common.model.Gift;
import com.cser.play.common.model.GiftLike;
import com.cser.play.common.model.UserInfo;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

public class GiftLikeService {
	public static final GiftLikeService ME = new GiftLikeService();

	private GiftLike dao = new GiftLike();

	/**
	 * 点赞
	 * 
	 * @param userId
	 * @param giftId
	 * @return
	 */
	public Ret like(int userId, int giftId) {
		Gift gift = GiftService.ME.findById(giftId);
		if (null == gift) {
			return Ret.fail("msg", "点赞内容不存在");
		}
		GiftLike giftLike = findByUserId(userId, giftId);
		if (giftLike != null) {
			boolean delResult = giftLike.delete();
			if (delResult) {
				return Ret.ok("msg", "取消点赞成功").set("flag", "0");
			}
		}
		UserInfo user = UserInfoService.ME.findUser(userId);
		GiftLike like = new GiftLike();
		like.setGiftId(giftId);
		like.setUserId(userId);
		like.setNickName(user.getNickName());
		boolean result = like.save();
		if (result) {
			List<GiftLike> list = giftLikeList(giftId);
			return Ret.ok("msg", "成功点赞").set("list", list).set("flag", "1");
		}
		return Ret.fail("msg", "点赞失败");
	}

	/**
	 * 点赞列表
	 * 
	 * @param giftId
	 * @return
	 */
	public List<GiftLike> giftLikeList(int giftId) {
		SqlPara sqlPara = dao.getSqlPara("giftLike.giftLikeList", giftId);
		return dao.find(sqlPara);
	}

	/**
	 * 根据userId查找点赞
	 * 
	 * @param userId
	 * @return
	 */
	public GiftLike findByUserId(int userId, int giftId) {
		SqlPara sqlPara = dao.getSqlPara("giftLike.findGiftLike", userId, giftId);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 点赞详情
	 * 
	 * @param userId
	 * @param giftId
	 * @return
	 */
	public Ret detail(int userId, int giftId) {
		List<GiftLike> list = giftLikeList(giftId);
		boolean whether = false;
		for (GiftLike like : list) {
			if ((int) like.get("userId") == userId) {
				whether = true;
			}
			whether = (int) like.get("userId") == userId ? true : false;
		}
		return Ret.create("list", list).set("whether", whether);
	}

}
