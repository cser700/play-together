package com.cser.play.common.controller;

import com.cser.play.common.kit.MyHttpKit;
import com.jfinal.core.Controller;
import com.jfinal.json.FastJson;

/**
 * 
 * @author res
 *
 */
public class BaseController extends Controller{

	@Override
	public String getPara() {
		if ("application/json".equals(getRequest().getContentType()) || "text/json".equals(getRequest().getContentType())) {
			// 数据直接从request中提取出来
			return MyHttpKit.readData(getRequest());
		}
		return super.getPara();
	}
	
	public <T>T getJSONObject(String jsonStr, Class<T> t){
		return FastJson.getJson().parse(jsonStr, t);
	}
}
