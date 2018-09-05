package com.cser.play.task;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.RewardTask;
import com.cser.play.common.model.RewardTaskAccept;
import com.cser.play.message.NoticeService;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * 悬赏任务
 * 
 * @author res
 *
 */
public class TaskController extends BaseController {
	RewardTaskService srv = RewardTaskService.ME;
	RewardTaskAcceptService accSrv = RewardTaskAcceptService.ME;

	/**
	 * 发布任务
	 */
	public void publish() {
		String jsonStr = getPara();
		RewardTask task = getJSONObject(jsonStr, RewardTask.class);
		String formId = JSONkit.jsonValue(jsonStr, "formId");
		if (StrKit.notBlank(formId)) {
			NoticeService.ME.save(task.getTaskUserId(), formId);
		}
		Ret ret = srv.publishTask(task);
		renderJson(ret);
	}

	/**
	 * 参加任务
	 */
	public void accept() {
		RewardTaskAccept acceptTask = getJSONObject(getPara(), RewardTaskAccept.class);
		Ret ret = accSrv.acceptTask(acceptTask);
		renderJson(ret);
	}

	/**
	 * 确认完成任务
	 */
	public void confirm() {
		RewardTask task = getJSONObject(getPara(), RewardTask.class);
		Ret ret = srv.confirmTask(task);
		renderJson(ret);
	}

	/**
	 * 删除任务
	 */
	public void delete() {
		RewardTask task = getJSONObject(getPara(), RewardTask.class);
		Ret ret = srv.delete(task);
		renderJson(ret);
	}

	/**
	 * 悬赏列表
	 */
	public void taskPage() {
		String jsonStr = getPara();
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int companyId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "companyId"));
		Page<RewardTask> page = srv.taskPage(pageNum, companyId);
		renderJson(page);
	}

	/**
	 * 悬赏管理-发布列表
	 */
	public void publishList() {
		String jsonStr = getPara();
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		Page<RewardTask> page = srv.publishList(userId, pageNum);
		renderJson(page);
	}

	/**
	 * 悬赏管理-参加列表
	 */
	public void acceptList() {
		String jsonStr = getPara();
		int pageNum = Integer.parseInt(JSONkit.jsonValue(jsonStr, "pageNum"));
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		Page<RewardTask> page = srv.acceptList(userId, pageNum);
		renderJson(page);
	}
}
