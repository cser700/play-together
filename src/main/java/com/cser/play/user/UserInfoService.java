package com.cser.play.user;

import java.math.BigDecimal;
import java.util.List;

import com.cser.play.account.UserAccountService;
import com.cser.play.apply.ApplyService;
import com.cser.play.common.enums.DealCategory;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.kit.BeanKit;
import com.cser.play.common.model.ApplyList;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.UserAccount;
import com.cser.play.common.model.UserInfo;
import com.cser.play.deal.DealRecordService;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 用户信息
 * 
 * @author res
 *
 */
public class UserInfoService {
	public static final UserInfoService ME = new UserInfoService();
	private UserInfo dao = new UserInfo();

	/**
	 * 注册企业用户
	 * 
	 * @return
	 */
	public boolean regCompany(UserInfo userInfo) {
		return userInfo.update();
	}

	/**
	 * 注册普通用户
	 * 
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	public Ret regUser(UserInfo userInfo) {
		String openid = userInfo.getOpenid();
		int companyId = !StrKit.notNull(userInfo.getCompanyId()) ? 0 : userInfo.getCompanyId();
		UserInfo oldUser = ME.findByOpenId(openid);
		if (null == oldUser) {
			return Ret.fail("msg", "用户不存在");
		}
		boolean isExtis = ApplyService.ME.isExits(oldUser.getUserId(), userInfo.getCompanyId());
		if (isExtis) {
			return Ret.fail("msg", "正在审核中，请耐心等候...");
		}
		try {
			// 复制属性
			BeanKit.copyPropertiesInclude(userInfo, oldUser, new String[] { "nickName", "avatarUrl", "city", "country",
					"gender", "province", "language", "position", "realName", "mobile" });
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		}
		oldUser.setStatus("0");
		boolean result = oldUser.update();
		if (!result) {
			return Ret.ok("msg", "注冊失败");
		}
		UserInfo resultUser = findByOpenId(openid);
		// 初始化新用户账户
		UserAccountService.ME.initUserAccount(resultUser.getUserId());
		// companyId有值，增加申请记录
		if (companyId > 0) {
			ApplyList apply = new ApplyList();
			apply.setCompanyId(companyId);
			apply.setUserId(resultUser.getUserId());
			boolean applyResult = apply.save();
			if (applyResult) {
				// 修改用户信息为
				resultUser.setStatus("1");
				resultUser.update();
			}
		}
		return Ret.ok("msg", "已提交审核，请耐心稍等...").set("userInfo", resultUser);
	}
	
	/**
	 * 注册游客
	 * 
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	public Ret regTourist(UserInfo userInfo) {
		String openid = userInfo.getOpenid();
		UserInfo oldUser = ME.findByOpenId(openid);
		if (null == oldUser) {
			return Ret.fail("msg", "用户不存在");
		}
		try {
			// 复制属性
			BeanKit.copyPropertiesInclude(userInfo, oldUser, new String[] { "nickName", "avatarUrl", "city", "country",
					"gender", "province", "language", "position", "realName", "mobile" });
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		}
		oldUser.setStatus("4");
		// 游客默认加入“游客公司”
		oldUser.setCompanyId(20);
		boolean result = oldUser.update();
		if (!result) {
			return Ret.ok("msg", "注冊失败");
		}
		UserInfo resultUser = findByOpenId(openid);
		// 初始化新用户账户
		UserAccountService.ME.initTouristUser(resultUser.getUserId());
		return Ret.ok("msg", "游客登录成功").set("userInfo", resultUser);
	}

	/**
	 * 获得user_info下一个自增主键
	 */
	public Integer nextPrimaryKey() {
		String sql = Db.getSql("user.nextPrimaryKey");
		return Db.queryInt(sql);
	}

	/**
	 * 赠送牛贝
	 * 
	 * @param giftAmount
	 * @param userId
	 * @param acceptUserId
	 * @param desc
	 * @return
	 */
	@Before(Tx.class)
	public Ret gift(Integer amount, Integer userId, Integer acceptUserId, String desc) {
		try {
			UserAccount account = UserAccountService.ME.queryBalance(userId);
			if (amount > account.getFreeze()) {
				return Ret.fail("msg", "余额不足，无法赠送");
			} else {
				if (null != userId && null != acceptUserId && 0 > amount) {
					// 赠送人冻结余额-1
					boolean subResult = UserAccountService.ME.subFreeze(userId, amount);
					if (subResult) {
						// 添加赠送交易记录
						DealRecord dr = new DealRecord();
						dr.setUserId(userId);
						dr.setAcceptUserId(acceptUserId + "");
						dr.setAmount(BigDecimal.valueOf(amount));
						dr.setDealDesc(desc);
						dr.setDealType(DealRecord.DEAL_REWARD_GET);
						dr.setStatus(DealStatus.success.getKey());
						dr.setCategory(DealCategory.income.getKey());
						DealRecordService.ME.addDealRecord(dr);
					}
					// 接收人可用余额+1
					boolean addResult = UserAccountService.ME.addAvailable(acceptUserId,
							Integer.parseInt(UserAccountService.SIGN_REWARD));
					if (addResult) {
						// 获赠交易记录
						DealRecord dr = new DealRecord();
						dr.setUserId(acceptUserId);
						dr.setAcceptUserId(userId + "");
						dr.setAmount(BigDecimal.valueOf(amount));
						dr.setDealDesc(DealRecord.DEAL_REWARD_PAY_DESC);
						dr.setDealType(DealRecord.DEAL_REWARD_PAY);
						dr.setStatus(DealStatus.success.getKey());
						dr.setCategory(DealCategory.expenditure.getKey());
						DealRecordService.ME.addDealRecord(dr);
					}
					return Ret.ok("msg", "赠送成功");
				} else {
					return Ret.fail("msg", "参数不能为空，赠送失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Ret.fail("msg", "赠送失败");
	}

	/**
	 * 根据 userId 查询用户
	 * 
	 * @param userId
	 * @return
	 */
	public UserInfo findUser(int userId) {
		return dao.findById(userId);
	}

	/**
	 * 根据 mobile 查询用户
	 * 
	 * @param mobile
	 * @return
	 */
	public UserInfo findByMobile(String mobile) {
		SqlPara sqlPara = dao.getSqlPara("user.findByMobile", mobile);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 根据 openId 查询用户
	 * 
	 * @param openId
	 * @return
	 */
	public UserInfo findByOpenId(String openId) {
		SqlPara sqlPara = dao.getSqlPara("user.findByOpenId", openId);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 修改用户资料
	 * 
	 * @param user
	 * @return
	 */
	public Ret updateUserInfo(UserInfo user) {
		boolean result = user.update();
		if (result) {
			return Ret.ok("msg", "修改成功");
		}
		return Ret.fail("msg", "修改失败");
	}

	/**
	 * 个人中心
	 * 
	 * @param userId
	 * @param openId
	 * @return
	 */
	public UserInfo userCenter(int userId) {
		SqlPara sqlPara = dao.getSqlPara("user.userCenter", userId);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 搜索用户
	 * 
	 * @param condition
	 * @param companyId
	 * @return
	 */
	public List<UserInfo> searchUser(int companyId, String condition) {
		SqlPara sqlPara = dao.getSqlPara("user.searchUser", companyId, condition);
		return dao.find(sqlPara);
	}

	/**
	 * 用户列表
	 * 
	 * @param pageNumber
	 * @return
	 */
	public List<UserInfo> userPage(int companyId, String userId) {
		Kv data = Kv.by("userId", userId).set("companyId", companyId);
		SqlPara sqlPara = dao.getSqlPara("user.userPage", data);
		return dao.find(sqlPara);
		/*
		 * // 此处中文排序需要用 paginateByFullSql 特殊处理, 框架问题 String from =
		 * "from user_info where company_id = ?"; String totalRowSql =
		 * "select count(*) " + from; String findSql =
		 * "select user_id userId, nick_name nickName, avatar_url avatarUrl " +
		 * from +
		 * " order by convert(nick_name USING gbk)COLLATE gbk_chinese_ci ASC";
		 * return dao.paginateByFullSql(pageNumber, 10, totalRowSql, findSql,
		 * companyId);
		 */
	}

	/**
	 * 根据 unionid 查询用户
	 * @param unionId
	 * @return
	 */
	public UserInfo findUserByUnionId(String unionId) {
		SqlPara sqlPara = dao.getSqlPara("findUserByUnionId", unionId);
		return dao.findFirst(sqlPara);	
	}

}
