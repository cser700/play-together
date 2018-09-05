package com.cser.play.account;

import com.cser.play.common.model.UserAccount;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 
 * @author res
 *
 */
public class UserAccountService {
	public static final UserAccountService ME = new UserAccountService();
	private UserAccount dao = new UserAccount();
	public static final String SIGN_REWARD = PropKit.use("play_config_dev.txt").get("sign_reward");

	/**
	 * 初始化用户账户
	 */
	public boolean initUserAccount(Integer userId) {
		UserAccount isExtis= findAccount(userId);
		if (null != isExtis) {
			return false;
		}
		UserAccount account = new UserAccount();
		if (StrKit.notNull(userId)) {
			account.setUserId(userId);
			return account.save();
		}
		return false;
	}
	
	/**
	 * 初始化游客,默认赠送100个牛贝
	 */
	public boolean initTouristUser(Integer userId) {
		UserAccount isExtis= findAccount(userId);
		if (null != isExtis) {
			return false;
		}
		UserAccount account = new UserAccount();
		if (StrKit.notNull(userId)) {
			account.setUserId(userId);
			account.setFreeze(100);
			return account.save();
		}
		return false;
	}

	/**
	 * 查询余额
	 * 
	 * @param userId
	 * @return
	 */
	public UserAccount queryBalance(int userId) {
		SqlPara sqlPara = dao.getSqlPara("account.queryBalance", userId);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 冻结余额增加
	 * 
	 * @param userId
	 * @return
	 */
	public boolean addFreeze(Integer userId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("account.addFreeze", amount, userId);
		return Db.update(sqlPara) > 0;
	}

	/**
	 * 冻结余额减少
	 * 
	 * @param userId
	 * @return
	 */
	public boolean subFreeze(int userId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("account.aubFreeze", amount, userId);
		return Db.update(sqlPara) > 0 ? true : false;
	}

	/**
	 * 可用余额增加
	 * 
	 * @param userId
	 * @return
	 */
	public boolean addAvailable(int userId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("account.addAvailable", amount, userId);
		return Db.update(sqlPara) > 0 ? true : false;
	}

	/**
	 * 可用余额减少
	 * 
	 * @param userId
	 * @return
	 */
	public boolean subAvailable(int userId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("account.subAvailable", amount, userId);
		return Db.update(sqlPara) > 0 ? true : false;
	}

	/**
	 * 任务冻结余额增加
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public boolean addTaskFreeze(int userId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("account.addTaskFreeze", amount, userId);
		return Db.update(sqlPara) > 0 ? true : false;
	}

	/**
	 * 任务冻结余额减少
	 * 
	 * @param userId
	 * @param amount
	 * @return
	 */
	public boolean subTaskFreeze(int userId, int amount) {
		SqlPara sqlPara = dao.getSqlPara("account.subTaskFreeze", amount, userId);
		return Db.update(sqlPara) > 0 ? true : false;
	}

	/**
	 * 分配牛贝
	 * 
	 * @param userArr
	 * @param amount
	 * @param companyId
	 * @return
	 */
	public boolean assign(String userArr, int amount, int companyId) {
		String[] strArr = userArr.split(",");
		int[] users = new int[strArr.length];
		for (int i = 0; i < strArr.length; i++) {
			users[i] = Integer.parseInt(strArr[i]);
		}
		Kv cond = Kv.by("userIds", users).set("amount", amount).set("companyId", companyId);
		SqlPara sqlPara = Db.getSqlPara("account.assign", cond);
		return Db.update(sqlPara) > 0;
	}
	
	/**
	 * 查找账户
	 * @param userId
	 * @return
	 */
	public UserAccount findAccount(int userId){
		SqlPara sqlPara = dao.getSqlPara("account.findAccount", userId);
		return dao.findFirst(sqlPara);
	}
	
}
