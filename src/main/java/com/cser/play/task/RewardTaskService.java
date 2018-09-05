package com.cser.play.task;

import java.math.BigDecimal;
import java.util.List;

import com.cser.play.account.UserAccountService;
import com.cser.play.common.enums.DealCategory;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.enums.TaskStatus;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.RewardTask;
import com.cser.play.common.model.RewardTaskAccept;
import com.cser.play.common.model.UserAccount;
import com.cser.play.common.model.UserInfo;
import com.cser.play.deal.DealRecordService;
import com.cser.play.user.UserInfoService;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 
 * @author res
 *
 */
public class RewardTaskService {
	public static RewardTaskService ME = new RewardTaskService();
	private RewardTask dao = new RewardTask();

	/**
	 * 发布任务
	 * 
	 * @param task
	 */
	public Ret publishTask(RewardTask task) {
		if (!StrKit.notNull(task.getTaskUserId(), task.getNumberPeople(), task.getReward(), task.getTaskDesc())) {
			return Ret.fail("msg", "参数缺失，任务发布失败");
		}
		int totalReward = task.getNumberPeople() * task.getReward();
		UserInfo user = UserInfoService.ME.findUser(task.getTaskUserId());
		task.setTaskUserName(user.getNickName());
		UserAccount account = UserAccountService.ME.queryBalance(task.getTaskUserId());
		if (account.getFreeze() < totalReward) {
			return Ret.fail("msg", "余额不足，发布失败");
		}
		// 冻结余额减少
		boolean subFreezeResult = UserAccountService.ME.subFreeze(task.getTaskUserId(), totalReward);
		if (subFreezeResult) {
			DealRecord dr = new DealRecord();
			dr.setUserId(task.getTaskUserId());
			dr.setAcceptUserName(user.getNickName());
			dr.setAmount(BigDecimal.valueOf(totalReward));
			dr.setDealDesc(DealRecord.DEAL_TASK_PAY_FREEZE_DESC);
			dr.setDealType(DealRecord.DEAL_TASK_PAY_FREEZE);
			dr.setStatus(DealStatus.success.getKey());
			dr.setCategory(DealCategory.freeze.getKey());
			DealRecordService.ME.addDealRecord(dr);
		}
		// 任务冻结余额增加
		UserAccountService.ME.addTaskFreeze(task.getTaskUserId(), totalReward);
		task.setTotalReward(totalReward);
		boolean result = task.save();
		if (result) {
			return Ret.ok("msg", "任务发布成功");
		} else {
			return Ret.fail("msg", "任务发布失败");
		}
	}

	/**
	 * 增加参加人数
	 * 
	 * @param pageNum
	 * @return
	 */
	public boolean addAcceptNum(int taskId) {
		SqlPara sqlPara = dao.getSqlPara("task.addAcceptNum", taskId);
		return Db.update(sqlPara) > 0;
	}

	/**
	 * 删除任务
	 * 
	 * @param task
	 * @return
	 */
	@Before(Tx.class)
	public Ret delete(RewardTask task) {
		// 查询是否是发布者本人
		SqlPara sqlPara = dao.getSqlPara("task.findByUserId", task.getTaskId(), task.getTaskUserId());
		RewardTask taskQuery = dao.findFirst(sqlPara);
		if (null == taskQuery) {
			return Ret.fail("msg", "任务不存在，无法删除");
		}
		if (!TaskStatus.publish.getKey().equals(taskQuery.getTaskStatus())) {
			return Ret.fail("msg", "任务已有人参与，无法删除");
		}
		if (task.getTaskUserId() != taskQuery.getTaskUserId()) {
			return Ret.fail("msg", "不是发布者本人，无法删除");
		}
		if (null != taskQuery.getTaskUserId()) {
			SqlPara para = dao.getSqlPara("task.deleteById", taskQuery.getTaskId());
			boolean result = Db.update(para) > 0 ? true : false;
			if (result) {
				// 将任务冻结余额转入冻结余额
				UserAccountService.ME.subTaskFreeze(taskQuery.getTaskUserId(), taskQuery.getTotalReward());
				boolean addFreezeResult = UserAccountService.ME.addFreeze(taskQuery.getTaskUserId(),
						taskQuery.getTotalReward());
				if (addFreezeResult) {
					DealRecord dr = new DealRecord();
					dr.setUserId(taskQuery.getTaskUserId());
					dr.setAmount(BigDecimal.valueOf(taskQuery.getTotalReward()));
					dr.setDealDesc(DealRecord.DEAL_TASK_FREEZE_THAW_DESC);
					dr.setDealType(DealRecord.DEAL_TASK_FREEZE_THAW);
					dr.setStatus(DealStatus.success.getKey());
					dr.setCategory(DealCategory.thaw.getKey());
					DealRecordService.ME.addDealRecord(dr);
				}
				return Ret.ok("msg", "删除成功");
			}
		}
		return Ret.fail("msg", "删除失败");
	}

	/**
	 * 查询任务
	 * 
	 * @param taskId
	 * @return
	 */
	public RewardTask findTask(int taskId) {
		return dao.findById(taskId);
	}

	/**
	 * 确认完成任务
	 * 
	 * @param task
	 */
	@Before(Tx.class)
	public Ret confirmTask(RewardTask task) {
		if (!StrKit.notNull(task.getTaskUserId(), task.getTaskId())) {
			return Ret.fail("msg", "参数缺失，确认失败");
		}
		RewardTask rewardTask = ME.findTask(task.getTaskId());
		if (null == rewardTask) {
			return Ret.fail("msg", "未查询到该任务，确认失败");
		}
		if (task.getTaskUserId() != rewardTask.getTaskUserId()) {
			return Ret.fail("msg", "不是发布者本人，确认失败");
		}
		if (!TaskStatus.processing.getKey().equals(rewardTask.getTaskStatus())) {
			return Ret.fail("msg", "任务未开始，无法确认");
		}
		List<RewardTaskAccept> acceptList = RewardTaskAcceptService.ME.findAcceptList(task.getTaskId());
		// 判断人数是否正确
		if (rewardTask.getNumberPeople() == acceptList.size()) {
			// 扣减发布人任务冻结余额
			boolean result = UserAccountService.ME.subTaskFreeze(rewardTask.getTaskUserId(),
					rewardTask.getTotalReward());

			if (result) {
				// 任务参与者user 数组
				String acceptUserIdArr = "";
				String acceptUserNameArr = "";
				// 将任务奖励转入参与任务者活动余额中
				for (int i = 0; i < acceptList.size(); i++) {
					RewardTaskAccept acceptUser = acceptList.get(i);
					// 增加活动余额
					boolean addAvailableResult = UserAccountService.ME.addAvailable(acceptUser.getAcceptUserId(),
							rewardTask.getReward());
					if (addAvailableResult) {
						// 增加交易记录
						DealRecord record = new DealRecord();
						record.setUserId(acceptUser.getAcceptUserId());
						record.setAcceptUserId(rewardTask.getTaskUserId() + "");
						record.setAcceptUserName(rewardTask.getTaskUserName());
						record.setAmount(BigDecimal.valueOf(rewardTask.getReward()));
						record.setDealType(DealRecord.DEAL_TASK_GET);
						record.setDealDesc(DealRecord.DEAL_TASK_GET_DESC);
						record.setStatus(DealStatus.success.getKey());
						record.setCategory(DealCategory.income.getKey());
						DealRecordService.ME.addDealRecord(record);
					}
					if (i == 0) {
						acceptUserIdArr = acceptUser.getAcceptUserId() + "";
						acceptUserNameArr = acceptUser.getAcceptUserName();
					} else {
						acceptUserIdArr += "," + acceptUser.getAcceptUserId() + "";
						acceptUserNameArr += "," + acceptUser.getAcceptUserName();
					}
				}
				// 增加发布人扣减交易记录
				DealRecord rd = new DealRecord();
				rd.setUserId(rewardTask.getTaskUserId());
				rd.setAcceptUserId(acceptUserIdArr);
				rd.setAcceptUserName(acceptUserNameArr);
				rd.setAmount(BigDecimal.valueOf(rewardTask.getReward()));
				rd.setDealType(DealRecord.DEAL_TASK_PAY);
				rd.setDealDesc(acceptUserNameArr + DealRecord.DEAL_TASK_PAY_DESC);
				rd.setStatus(DealStatus.success.getKey());
				rd.setCategory(DealCategory.expenditure.getKey());
				DealRecordService.ME.addDealRecord(rd);

				// 更新任务状态
				rewardTask.setTaskStatus(TaskStatus.end.getKey());
				rewardTask.update();
				return Ret.ok("msg", "确认成功");
			}
		}
		return Ret.fail("msg", "确定任务失败");
	}
	
	
	/**
	 * 悬赏列表
	 * @param pageNum
	 * @return
	 */
	public Page<RewardTask> taskPage(int pageNum, int companyId) {
		SqlPara sqlPara = dao.getSqlPara("task.taskPage", companyId);
		Page<RewardTask> taskPage = dao.paginate(pageNum, 10, sqlPara);
		return taskPage;
	}


	/**
	 * 悬赏管理-发布列表
	 * 
	 * @param userId
	 * @return
	 */
	public Page<RewardTask> publishList(int userId, int pageNum) {
		SqlPara sqlPara = dao.getSqlPara("task.publishList", userId);
		Page<RewardTask> taskPage = dao.paginate(pageNum, 100, sqlPara);
		return taskPage;
	}

	/**
	 * 悬赏管理-参加列表
	 * 
	 * @param userId
	 * @return
	 */
	public Page<RewardTask> acceptList(int userId, int pageNum) {
		SqlPara sqlPara = dao.getSqlPara("task.acceptList", userId);
		Page<RewardTask> taskPage = dao.paginate(pageNum, 100, sqlPara);
		return taskPage;
	}

}
