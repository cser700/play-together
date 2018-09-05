package com.cser.play.sign;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cser.play.account.UserAccountService;
import com.cser.play.common.enums.DealCategory;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.SignIn;
import com.cser.play.common.model.SignInInfo;
import com.cser.play.common.model.SignSupplement;
import com.cser.play.common.model.UserAccount;
import com.cser.play.common.utils.PlayDateUtil;
import com.cser.play.deal.DealRecordService;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 签到
 * 
 * @author res
 *
 */
public class SignInService {
	public static final SignInService ME = new SignInService();
	private SignIn dao = new SignIn();

	/**
	 * 是否签到
	 * 
	 * @param userId
	 * @param date
	 * @return
	 */
	public boolean isSignIn(String userId, String date) {
		SqlPara sqlPara = dao.getSqlPara("sign.findById", userId, date);
		return dao.findFirst(sqlPara) != null;
	}

	/**
	 * 获取当月签到情况
	 * 
	 * @param userId
	 * @param currMonth
	 *            当月
	 * @return
	 */
	public int[] findSignList(String userId, String currMonth) {
		Kv para = Kv.by("userId", userId).set("signDate", currMonth);
		SqlPara sqlPara = dao.getSqlPara("sign.findSignList", para);
		List<SignIn> list = dao.find(sqlPara);
		int[] arr = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String date = list.get(i).getSignInDate().toString().substring(8, 10);
			arr[i] = Integer.parseInt(date);
		}
		return arr;
	}

	/**
	 * 签到
	 */
	public Ret sign(Integer userId, String supplement) {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String nowDate = PlayDateUtil.dateToStr(new Date());
			String beginTime = nowDate + " 05:00:00";
			String endTime = nowDate + " 10:01:00";
			String supplementEndTime = nowDate + " 16:00:00";
			Date endDate = format.parse(endTime);
			Date beginDate = format.parse(beginTime);
			Date supplementDate = format.parse(supplementEndTime);
			// 补签
			if (StrKit.notBlank(supplement)) {
				boolean isExits = SignSupplementService.ME.querySupplement(userId);
				if (isExits) {
					return Ret.fail("msg", "每月只能补签一次");
				}
				// 补卡只能在上午12点之前
				if (System.currentTimeMillis() > supplementDate.getTime()) {
					return Ret.fail("msg", "请在12点之前补签");
				}
			} else {
				// 打卡只能在上午5-10之间
				if (System.currentTimeMillis() > endDate.getTime()
						|| System.currentTimeMillis() < beginDate.getTime()) {
					return Ret.fail("msg", "请在 5点~10点之间签到");
				}
			}

			// 随机获得签到奖励
			// int reward = MathRandomUtil.random();
			// 计算用户应得奖励
			Map<String,Integer> map = getReward(userId);
			int reward = map.get("reward") == null ? 0 : map.get("reward");
			int basis = map.get("basis");
			int totalReward = basis + reward;
			boolean isSign = ME.isSignIn(userId + "", nowDate);
			if (!isSign) {
				SignIn signIn = new SignIn();
				signIn.setUserId(userId);
				signIn.setSignInDate(new Date());
				boolean result = signIn.save();
				// 签到成功，增加牛贝
				if (result) {
					boolean rst = UserAccountService.ME.addFreeze(userId, totalReward);
					if (rst) {
						// 增加记录
						DealRecord record = new DealRecord();
						record.setUserId(userId);
						record.setAmount(BigDecimal.valueOf(totalReward));
						record.setDealType(DealRecord.DEAL_SIGN);
						record.setDealDesc(DealRecord.DEAL_SIGN_DESC);
						record.setStatus(DealStatus.success.getKey());
						record.setCategory(DealCategory.income.getKey());
						DealRecordService.ME.addDealRecord(record);
					}
					// 更新签到记录表
					SignInInfo info = SignInInfoService.ME.query(userId);
					int count = info == null ? 0 : info.getSignCount();
					if (null == info) {
						SignInInfo signInfo = new SignInInfo();
						signInfo.setUserId(userId);
						signInfo.setSignCount(1);
						signInfo.save();
					} else {
						info.setSignCount(count + 1);
						info.update();
					}
					if (StrKit.notBlank(supplement)) {
						SignSupplement sign = new SignSupplement();
						sign.setUserId(userId);
						sign.save();
						return Ret.ok("msg", "补签成功").set("reward", reward).set("basis", basis);
					}
				}
				return Ret.ok("msg", "签到成功").set("reward", reward).set("basis", basis);
			}
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return Ret.fail("msg", "不能重复签到");
	}

	/**
	 * 当月签到列表
	 */
	public Ret signList(Integer userId) {
		String currMonth = PlayDateUtil.dateToStr(new Date()).substring(0, 7);
		int[] signList = ME.findSignList(userId + "", currMonth);
		if (null != signList && signList.length > 0) {
			return Ret.create("signList", signList);
		}
		return Ret.fail("msg", "本月没有签到数据");
	}

	/**
	 * 计算签到应得奖励
	 */
	public Map<String, Integer> getReward(int userId) {
		Map<String, Integer> result = new HashMap<>();
		// 额外奖励
		int reward = 0;
		// 基础奖励
		int basis = 0;
		SignInInfo info = SignInInfoService.ME.query(userId);
		if (null != info) {
			// 连续签到第2天（+5矿），第7天（+20矿），第21天（+50矿），第60天（+50矿），第90天（+200矿）
			int count = info.getSignCount() + 1;
			if (count == 0) {
				result.put("basis", 1);
				return result;
			} else if (count == 2) {
				reward = 5;
			} else if (count == 7) {
				reward = 20;
			} else if (count == 21 || count == 60) {
				reward = 50;
			} else if (count == 90) {
				reward = 200;
			}
			// 每7天基础挖矿奖励+1，基础挖矿奖励=5时停止累加并一直保持下去
			if (count <= 7) {
				basis = 1;
			} else if (count > 7 && count <= 14) {
				basis = 2;
			} else if (count > 14 && count <= 21) {
				basis = 3;
			} else if (count > 21 && count <= 28) {
				basis = 4;
			} else if (count > 28) {
				basis = 5;
			}
		} else {
			result.put("basis", 1);
			return result;
		}
		result.put("basis", basis);
		result.put("reward", reward);
		return result;
	}

	/**
	 * 签到信息
	 * 
	 * @param userId
	 * @return
	 */
	public Ret info(int userId) {
		UserAccount account = UserAccountService.ME.queryBalance(userId);
		boolean isExits = SignSupplementService.ME.querySupplement(userId);
		SignInInfo info = SignInInfoService.ME.query(userId);
		// 矿石余额
		int balance = account.getFreeze();
		// 补签次数
		int supplementCount = isExits == true ? 0 : 1;
		// 连续签到次数
		int signCount = info == null ? 0 : info.getSignCount();
		return Ret.create("balance", balance).set("supplementCount", supplementCount).set("signCount", signCount);
	}
	
	/**
	 * 查询当天签到情况
	 * @return
	 */
	public List<SignIn> queryInfo(){
		String currentDate = PlayDateUtil.getStringDateShort();
		SqlPara sqlPara = dao.getSqlPara("sign.queryInfo", currentDate);
		return dao.find(sqlPara);
	}
}
