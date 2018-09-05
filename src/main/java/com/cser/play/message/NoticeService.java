package com.cser.play.message;

import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import com.alibaba.fastjson.JSONObject;
import com.cser.play.common.PlayConfig;
import com.cser.play.common.model.Notice;
import com.cser.play.common.model.SignIn;
import com.cser.play.common.utils.MiniUtils;
import com.cser.play.sign.SignInService;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Prop;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;

public class NoticeService {
	public static final NoticeService ME = new NoticeService();
	private Notice dao = new Notice();

	private static Prop p = PlayConfig.p;

	// 小程序
	private static final String APP_ID = p.get("mini_program_app_id");
	private static final String APP_SECRET = p.get("mini_program_app_secret");

	// 签到模版消息template_id
	private static final String SIGN_TEMPLATE_ID = "***";

	// 发送模版消息URL
	private static final String URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";

	/**
	 * 保存fromId
	 * 
	 * @param openid
	 * @param formId
	 */
	public void save(int userId, String formId) {
		String openid = UserInfoService.ME.findUser(userId).getOpenid();
		// 过期时间设置为7天后
		long expiredTimes = 7 * 24 * 60 * 60 * 1000;
		long expired = new Date().getTime() + expiredTimes;
		Notice nc = new Notice();
		nc.setUserId(userId);
		nc.setOpenid(openid);
		nc.setFormid(formId);
		nc.setExpired(expired);
		nc.save();
	}

	/**
	 * 签到定时提醒-群发服务通知
	 */
	public void timingSend() {
		long expired = new Date().getTime();
		
		delete(expired);
		SqlPara sqlPara = dao.getSqlPara("notice.noticeList", expired);
		// 清除过期formid
		List<Notice> list = dao.find(sqlPara);
		List<SignIn> signList = SignInService.ME.queryInfo();
		String sign = "";
		for (int i = 0; i < signList.size(); i++) {
			sign += signList.get(i).getUserId() + "-";
		}
		try {
			for (int i = 0; i < list.size(); i++) {
				Notice ne = list.get(i);
				if (sign.contains(ne.getUserId() + "")) {
					continue;
				}
				boolean sendResult = sendMessage(ne.getOpenid(), ne.getFormid());
				if (sendResult) {
					ne.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogKit.error(" ########## notice timgng error ##########", e);
		}
		LogKit.info("########## notice timing task end ##########");
	}

	/**
	 * 小程序-发送模版消息-签到提醒
	 * 
	 * @param openId
	 * @param text
	 * @return
	 */
	public boolean sendMessage(String openId, String formid) {
		try {
			String resp = "";// 响应
			String reqUrl = URL + MiniUtils.getAccessToken(APP_ID, APP_SECRET);
			// 构造httpRequest设置
			HttpClient client = new HttpClient();
			PostMethod request = new PostMethod(reqUrl);
			// 添加request headers
			request.addRequestHeader("Content-type", "application/json");
			request.addRequestHeader("Accept", "application/json");

			JSONObject keyword1 = new JSONObject();
			keyword1.put("value", "挖矿啦！");
			keyword1.put("color", "#000000");

			JSONObject keyword2 = new JSONObject();
			keyword2.put("value", "叮！该挖矿啦~锲而不舍,金石可镂。");
			keyword2.put("color", "#000000");

			JSONObject data = new JSONObject();
			data.put("keyword1", keyword1);
			data.put("keyword2", keyword2);

			JSONObject params = new JSONObject();
			params.put("touser", openId);
			params.put("template_id", SIGN_TEMPLATE_ID);
			params.put("form_id", formid);
			params.put("page", "pages/validate/validate");
			params.put("data", data);

			String json = params.toString();
			request.setRequestEntity(new ByteArrayRequestEntity(json.getBytes("UTF-8")));

			client.executeMethod(request);
			resp = request.getResponseBodyAsString();
			System.out.println(resp);
		} catch (Exception e) {
			LogKit.error("发送POST请求出现异常！" + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除过期formid
	 * 
	 * @param expired
	 */
	public void delete(long expired) {
		Db.delete("delete from notice where expired < ?", expired);
	}

}
