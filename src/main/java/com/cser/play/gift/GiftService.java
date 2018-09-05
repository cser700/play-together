package com.cser.play.gift;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cser.play.account.UserAccountService;
import com.cser.play.common.enums.DealCategory;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.Gift;
import com.cser.play.common.model.GiftComment;
import com.cser.play.common.model.GiftLike;
import com.cser.play.common.model.UserAccount;
import com.cser.play.common.model.UserInfo;
import com.cser.play.deal.DealRecordService;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 赠送
 * 
 * @author res
 *
 */
public class GiftService {
	public static final GiftService ME = new GiftService();
	private final Gift dao = new Gift();

	/**
	 * 添加赠送记录
	 * 
	 * @param gift
	 */
	public Ret gift(Gift gift) {
		UserAccount account = UserAccountService.ME.queryBalance(gift.getUserId());
		if (gift.getAmount() > account.getFreeze()) {
			return Ret.fail("msg", "余额不足，无法赠送");
		}
		boolean result = gift.save();
		UserInfo acceotUser = UserInfoService.ME.findUser(gift.getAcceptUserId());
		try {
			if (result) {
				// 赠送人冻结余额-1
				boolean subResult = UserAccountService.ME.subFreeze(gift.getUserId(), gift.getAmount());
				if (subResult) {
					// 添加赠送交易记录
					DealRecord dr = new DealRecord();
					dr.setUserId(gift.getUserId());
					dr.setAcceptUserId(gift.getAcceptUserId() + "");
					dr.setAcceptUserName(acceotUser.getNickName());
					dr.setAmount(BigDecimal.valueOf(gift.getAmount()));
					dr.setDealDesc(gift.getDesc());
					dr.setDealType(DealRecord.DEAL_REWARD_PAY);
					dr.setStatus(DealStatus.success.getKey());
					dr.setCategory(DealCategory.expenditure.getKey());
					DealRecordService.ME.addDealRecord(dr);
				}
				// 接收人可用余额+1
				boolean addResult = UserAccountService.ME.addAvailable(gift.getAcceptUserId(), gift.getAmount());
				if (addResult) {
					// 获赠交易记录
					DealRecord dr = new DealRecord();
					dr.setUserId(gift.getAcceptUserId());
					dr.setAcceptUserId(gift.getUserId() + "");
					dr.setAmount(BigDecimal.valueOf(gift.getAmount()));
					dr.setDealDesc(gift.getDesc());
					dr.setDealType(DealRecord.DEAL_REWARD_GET);
					dr.setStatus(DealStatus.success.getKey());
					dr.setCategory(DealCategory.income.getKey());
					DealRecordService.ME.addDealRecord(dr);
				}
				return Ret.ok("msg", "赠送成功");
			}
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 赠送列表
	 * 
	 * @param pageNum
	 * @return
	 */
	public Page<Gift> giftList(int pageNum, int companyId, int userId) {
		int size = pageNum * 10;
		SqlPara sqlPara = dao.getSqlPara("gift.giftPage", companyId);
		Page<Gift> giftPage = dao.paginate( 1, size, sqlPara);
		List<Gift> list = giftPage.getList();
		// 查询点赞数据
		for (int i = 0; i < list.size(); i++) {
			String flag = "0";
			Gift gift = list.get(i);
			// 增加点赞信息 
			List<GiftLike> likeList = GiftLikeService.ME.giftLikeList(gift.getId());
			Map<String, Object> likeMap = new HashMap<>();
			likeMap.put("likeList", likeList);
			gift._setAttrs(likeMap);
			
			// 判断用户是否点过赞
			for (int j = 0; j < likeList.size(); j++) {
				GiftLike like = likeList.get(j);
				if ((int)like.get("userId") == userId) {
					flag = "1";
				}
			}
			Map<String, Object> flagMap = new HashMap<>();
			flagMap.put("flag", flag);
			gift._setAttrs(flagMap);
			
			// 增加评论信息
			List<GiftComment> commentList = GiftCommentService.ME.commentList(gift.getId());
			Map<String, Object> commentMap = new HashMap<>();
			commentMap.put("commentList", commentList);
			gift._setAttrs(commentMap);
		}
		return giftPage;
	}

	/**
	 * 根据id查找赞赏内容
	 * 
	 * @param giftId
	 * @return
	 */
	public Gift findById(int giftId) {
		return dao.findById(giftId);
	}
}
