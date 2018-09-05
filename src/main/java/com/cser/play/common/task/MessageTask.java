/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.cser.play.common.task;

import com.cser.play.message.NoticeService;
import com.jfinal.plugin.cron4j.ITask;

/**
 * 定时推送消息
 *
 * 目前暂定为每天9点30分，见配置文件
 * cron 表达式为：30 09 * * *
 */
public class MessageTask implements ITask {

	public void run() {
		todo();
	}

	public void stop() {
		todo();
	}

	private void todo() {
		NoticeService.ME.timingSend();
		
	}
}
