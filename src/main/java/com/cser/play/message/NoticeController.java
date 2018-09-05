package com.cser.play.message;

import com.cser.play.common.controller.BaseController;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

public class NoticeController extends BaseController {
	
	private NoticeService srv = NoticeService.ME;
	
	public void msg(){
		Cache cache= Redis.use("redis");
		cache.setex("test2", 600, "hhhhh");
		System.out.println("  redis : "+cache.get("test2"));
		renderJson(cache.get("test2").toString());
	}
	
	public void test(){
		String formid = "7cac63dec1975eaf939d45be47a3c45f";
		String openid = "oZjAb0QhlijRb7MtzEaVVfLgSg9o";
		srv.sendMessage(openid, formid);
		renderText("111");
	}
	
}
