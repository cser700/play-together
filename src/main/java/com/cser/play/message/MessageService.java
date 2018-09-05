package com.cser.play.message;

import com.cser.play.common.wx.MessageUtil;

public class MessageService {
	public static final MessageService ME = new MessageService();
	
	private static final String TEXT = "快要上班啦，记得去签到领取牛贝哦!";
	
	public void sendMessage(){
		MessageUtil.getInstance().initMessage(TEXT);
	}
}
