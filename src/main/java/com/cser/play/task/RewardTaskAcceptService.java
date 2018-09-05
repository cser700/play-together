package com.cser.play.task;

import java.util.List;

import com.cser.play.common.enums.TaskStatus;
import com.cser.play.common.model.RewardTask;
import com.cser.play.common.model.RewardTaskAccept;
import com.cser.play.common.model.UserInfo;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 接受任务
 * @author res
 *
 */
public class RewardTaskAcceptService {
	public static RewardTaskAcceptService ME = new RewardTaskAcceptService();
	private RewardTaskAccept dao = new RewardTaskAccept();

	/**
	 * 参加任务
	 * 
	 * @param taskId
	 * @param acceptUserId
	 */
	public Ret acceptTask(RewardTaskAccept accept) {
		UserInfo user = UserInfoService.ME.findUser(accept.getAcceptUserId());
		RewardTask task = RewardTaskService.ME.findTask(accept.getTaskId());
		if (null == task) {
			return Ret.fail("msg", "任务不存在");
		}
		if (null == user) {
			return Ret.fail("msg", "用户不存在");
		}
		if (isPublisher(accept)) {
			return Ret.fail("msg", "不能参加自己发布的任务");
		}
		if (isExist(accept)) {
			return Ret.fail("msg", "不能重复参加");
		}
		if (isMax(accept)) {
			return Ret.fail("msg", "参加人数已达到上限");
		}
		RewardTaskAccept acceptTask = new RewardTaskAccept();
		acceptTask.setTaskId(accept.getTaskId());
		acceptTask.setAcceptUserId(accept.getAcceptUserId());
		acceptTask.setAcceptUserName(user.getNickName());
		acceptTask.save();
		// 查询参加人数
		List<RewardTaskAccept> list = findAcceptList(accept.getTaskId());
		if (list.size() == task.getNumberPeople()) {
			task.setTaskStatus(TaskStatus.processing.getKey());
		}
		if(list.size() < task.getNumberPeople()){
			task.setTaskStatus(TaskStatus.someone.getKey());
		}
		// 增加参加人数
		task.setAcceptNum(task.getAcceptNum()+1);
		task.update();
		return Ret.ok("msg", "成功参加");
	}
	
	/**
	 * 判断是否已经参与
	 * @param accept
	 * @return
	 */
	public boolean isExist(RewardTaskAccept accept) {
		SqlPara sqlPara = dao.getSqlPara("taskAccept.queryIsExist", accept.getTaskId(), accept.getAcceptUserId());
		return dao.findFirst(sqlPara) != null;
	}

	/**
	 * 判断人数是否达到上限
	 * @param accept
	 * @return
	 */
	public boolean isMax(RewardTaskAccept accept) {
		RewardTask task = RewardTaskService.ME.findTask(accept.getTaskId());
		List<RewardTaskAccept> list = findAcceptList(accept.getTaskId());
		// 已参加人数
		int acceptNum = list.size();
		return task.getNumberPeople() > acceptNum ? false : true;
	}

	/**
	 * 判断是否是任务发布人
	 * @param accept
	 * @return
	 */
	public boolean isPublisher(RewardTaskAccept accept) {
		RewardTask task = RewardTaskService.ME.findTask(accept.getTaskId());
		return task.getTaskUserId() == accept.getAcceptUserId() ? true : false;
	}

	/**
	 * 查询任务参加人列表
	 * 
	 * @param accept
	 * @return
	 */
	public List<RewardTaskAccept> findAcceptList(int taskId) {
		SqlPara sqlPara = dao.getSqlPara("taskAccept.acceptList", taskId);
		return dao.find(sqlPara);
	}

}
