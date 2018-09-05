package com.cser.play.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HttpKit;


/**
 * 
 * @author res
 *
 */
public class JSONHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if ("application/json".equals(request.getContentType()) || "text/json".equals(request.getContentType())) {
			// 数据直接从request中提取出来
			String json = HttpKit.readData(request);
			request.setAttribute("json", json);
		}
		// 传递给下一个
		next.handle(target, request, response, isHandled);
	}
}
