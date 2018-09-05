package com.cser.play.common.wx;

import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cser.play.common.PlayConfig;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.utils.MiniUtils;
import com.github.kevinsawicki.http.HttpRequest;
import com.jfinal.kit.Prop;

/**
 * 公众号-发送消息
 * 
 * @author res
 *
 */
public class MessageUtil {

	private static Prop p = PlayConfig.p;
	
	// 服务号appid,secret
	private static final String APP_ID = p.get("mini_program_app_id");
	
	private static final String APP_SECRET = p.get("mini_program_app_secret");

	// 获取公众号-已关注所有用户列表接口地址
	private static final String ALL_USER_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=";

	// 获取公众号-查询单个用户详细信息接口地址
	private static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token={0}&openid={1}";
	
	// 小程序-发送模版消息
	private static final String SEND_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";

	/**
	 * 内部类 实例化当前对象
	 *
	 */
	private static class Instance {
		private static final MessageUtil mu = new MessageUtil();
	}

	/**
	 * 暴露在外部的方法
	 * 
	 * @return
	 */
	public static MessageUtil getInstance() {
		return Instance.mu;
	}

	/**
	 * 小程序-群发文本消息
	 * 
	 * @param openId
	 * @param text
	 * @return
	 */
	private boolean sendMessage(String[] openId, String text) {
		try {
			String resp = "";// 响应
			String reqUrl = SEND_MSG_URL + getAssessToken();
			try {
				// 构造httpRequest设置
				HttpClient client = new HttpClient();
				PostMethod request = new PostMethod(reqUrl);
				// 添加request headers
				request.addRequestHeader("Content-type", "application/json");
				request.addRequestHeader("Accept", "application/json");

				JSONObject param = new JSONObject();
				param.put("touser", openId);
				param.put("msgtype", "text");
				JSONObject content = new JSONObject();
				content.put("content", text);
				param.put("text", content);

				String json = param.toString();
				request.setRequestEntity(new ByteArrayRequestEntity(json.getBytes("UTF-8")));

				client.executeMethod(request);
				resp = request.getResponseBodyAsString();
				System.out.println(resp);
			} catch (Exception e) {
				System.out.println("发送POST请求出现异常！" + e);
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获得assess_token
	 * 
	 * @return
	 */
	public static String getAssessToken() {
		String assess_token = MiniUtils.getAccessToken(APP_ID, APP_SECRET);
		return assess_token;
	}

	/**
	 * 查询公众号所有用户列表 通过
	 * 
	 * @param assessToken
	 * @return
	 */
	public static String getAllUserList(String assessToken) {
		String response = HttpRequest.get(ALL_USER_URL + assessToken).body();
		return response;
	}

	/**
	 * 根据openid,查询userInfo,返回unionid
	 * 
	 * @param openid
	 * @param assessToken
	 * @return
	 */
	public static String getUserInfo(String openid, String assessToken) {
		String url = USER_INFO_URL.replace("{0}", assessToken).replace("{1}", openid);
		String response = HttpRequest.get(url).body();
		String unionid = JSONkit.jsonValue(response, "unionid");
		return unionid;
	}

	
	public void initMessage(String text) {
		String result = getAllUserList(getAssessToken());
		JSONArray arr = JSONObject.parseObject(JSONkit.jsonValue(result, "data")).getJSONArray("openid");
		String str = JSONObject.toJSONString(arr);
		List<String> list = JSONObject.parseArray(str, String.class);
		String[] openIds = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			openIds[i] = list.get(i);
		}
		MessageUtil.getInstance().sendMessage(openIds, text);
	}

}
