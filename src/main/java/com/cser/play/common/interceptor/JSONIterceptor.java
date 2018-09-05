package com.cser.play.common.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.HttpKit;

/**
 * 
 * @author res
 *
 */
public class JSONIterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		HttpServletRequest request = inv.getController().getRequest();
		if ("application/json".equals(request.getContentType()) || "text/json".equals(request.getContentType())) {
			// 数据直接从request中提取出来
			String json = HttpKit.readData(request);
			request.setAttribute("json", json);
		}
		inv.invoke();
	}

}
