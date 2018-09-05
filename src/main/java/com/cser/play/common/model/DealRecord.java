package com.cser.play.common.model;

import com.cser.play.common.model.base.BaseDealRecord;

/**
 * @author res
 */
@SuppressWarnings("serial")
public class DealRecord extends BaseDealRecord<DealRecord> {
	/**
	 * 获赠
	 */
	public static final String DEAL_REWARD_GET = "1";
	public static final String DEAL_REWARD_GET_DESC = "获得赠送";
	
	/**
	 * 完成任务奖励
	 */
	public static final String DEAL_TASK_GET = "2";			
	public static final String DEAL_TASK_GET_DESC = "完成任务奖励";
	
	/**
	 * 签到
	 */
	public static final String DEAL_SIGN = "3";	
	public static final String DEAL_SIGN_DESC = "签到奖励";	
	
	/**
	 * 公司分配
	 */
	public static final String DEAL_COMPANY_SHARE = "4";	
	public static final String DEAL_COMPANY_SHARE_DESC = "定期分发给{0}名员工共{1}个牛贝";	
	/**
	 * 公司充值
	 */
	public static final String DEAL_RECHARGE = "5";			
	/**
	 * 赠送他人
	 */
	public static final String DEAL_REWARD_PAY = "6";
	public static final String DEAL_REWARD_PAY_DESC = "赠送他人";
	
	/**
	 * 任务奖励冻结
	 */
	public static final String DEAL_TASK_PAY_FREEZE = "7";	
	public static final String DEAL_TASK_PAY_FREEZE_DESC = "发布任务冻结余额";	
	
	/**
	 * 支付任务赏金
	 */
	public static final String DEAL_TASK_PAY = "8";	
	public static final String DEAL_TASK_PAY_DESC = "完成你发布的悬赏";	
	
	/**
	 * 任务冻结余额解冻
	 */
	public static final String DEAL_TASK_FREEZE_THAW = "9";
	public static final String DEAL_TASK_FREEZE_THAW_DESC = "任务冻结余额解冻";
	/**
	 * 获得公司分配
	 */
	public static final String DEAL_GET_COMPANY_SHARE = "10";	
	public static final String DEAL_GET_COMPANY_SHARE_DESC = "获得公司分配";	
	/**
	 * 兑换商品
	 */
	public static final String DEAL_PAY = "11";	
	public static final String DEAL_PAY_DESC = "兑换商品";	
	
	
	private int number;


	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}
	
	
	
}
