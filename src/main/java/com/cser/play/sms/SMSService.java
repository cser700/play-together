package com.cser.play.sms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.cser.play.common.model.SmsCode;
import com.cser.play.common.model.SmsModel;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 短信发送
 * 
 * @author res
 *
 */
public class SMSService {

	public static SMSService ME = new SMSService();
	private static SmsCode dao = new SmsCode();

	private static final String ADDRESS = "http://api.sms.cn/sms/";
	private static final String USER_ID = "cc561659";
	private static final String MD5_PWD = "048ce9357cc2152c86d5c9a2aac292cd";

	// 过期时间，单位：秒
	private static final int EXPIRE_TIME = 600;

	public PostMethod commonSend(String phone, String context) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(ADDRESS);
		// 在头文件中设置转码
		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
		NameValuePair[] data = { new NameValuePair("uid", USER_ID), new NameValuePair("pwd", MD5_PWD),
				new NameValuePair("mobile", phone), new NameValuePair("content", context) };
		post.setRequestBody(data);
		client.executeMethod(post);
		return post;
	}

	/**
	 * 发送
	 * 
	 * @param phone
	 * @param context
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public SmsModel send(String phone, String context) throws HttpException, IOException {
		SmsModel smsStatus = new SmsModel();
		PostMethod post = commonSend(phone, context);
		int statusCode = post.getStatusCode();
		String response = null;
		if (statusCode == 200) {
			// 短信发送的返回为纯文本
			response = new String(post.getResponseBodyAsString().getBytes("utf-8"));
			String responseString = new String(response.getBytes("iso-8859-1"), "UTF-8");
			Map<String, String> resMap = parseResponseText(responseString);
			String stat = resMap.get("stat");
			String statMsg = resMap.get("message");
			smsStatus.setCode(stat);
			smsStatus.setMsg(statMsg);
		}
		return smsStatus;
	}

	/**
	 * Title: parseResponseText Description: 将行如sms;and statea 101;and message
	 * eq 验证失败的返回结果，转换为Map
	 * 
	 * @param cxt
	 * @return Map
	 */
	private Map<String, String> parseResponseText(String cxt) {
		Map<String, String> resMap = new HashMap<>();
		String[] splited = cxt.split("&");
		for (String s : splited) {
			if (!s.contains("=")) {
				continue;
			}
			String[] keyvalue = s.split("=");
			if (keyvalue.length == 2) {
				resMap.put(keyvalue[0], keyvalue[1]);
			}
		}
		return resMap;
	}

	/**
	 * Title: sendVerfyNumber Description:
	 * 对于用户注册时，验证码错误，只需要将与用户相关的结果返回，比如成功，或者号码是错误。其他的错误码不需要返回给用户
	 * 
	 * @param phone
	 * @param smscontext
	 * @return SmsStatusDTO
	 */

	public Ret sendSmsCode(String phone) {
		String smsCode = createRandom(phone);
		String content = createSmsContent(smsCode);
		try {
			send(phone, content);
			return Ret.ok("msg", "验证码发送成功").set("smsCode",smsCode);
		} catch (IOException e) {
			e.getMessage();
			return Ret.fail("msg", "验证码发送失败");
		}
	}

	/**
	 * 创建验证码
	 * 
	 * @param phone
	 * @return
	 */
	private String createRandom(String phone) {
		// 使用 long et 为了避免 int 数值溢出，造成保存到数据库中的数值错误
		long et = EXPIRE_TIME;
		long expireAt = System.currentTimeMillis() + (et * 1000);
		int random = (int) ((Math.random() * 9 + 1) * 1000);
		SmsCode sms = new SmsCode();
		sms.setMobile(phone);
		sms.setExpireTime(expireAt);
		sms.setCode(random);
		sms.save();
		return "" + random;
	}

	/**
	 * 判断验证码是否失效
	 * 
	 * @param expireTime
	 * @return
	 */
	public boolean valiExpireTime(long expireTime) {
		long nowTime = System.currentTimeMillis();
		if (nowTime > expireTime) {
			return false;
		}
		return true;
	}

	/**
	 * 内容，替换验证码
	 * 
	 * @param verifyNumer
	 * @return
	 */
	private String createSmsContent(String smsCode) {
		return String.format("【】您的验证码是：%s，请在10分钟内完成验证，请勿将该验证码泄露给他人（包括工作人员）", smsCode);
	}

	/**
	 * 校验验证码
	 * 
	 * @param phone
	 * @param smsCode
	 * @return
	 */
	public Ret valiSmsCode(String phone, String smsCode) {
		SqlPara sqlPara = dao.getSqlPara("sms.queryCode", phone, smsCode);
		SmsCode code = dao.findFirst(sqlPara);
		if (null == code) {
			return Ret.fail("msg", "验证码不存在");
		}
		if (!valiExpireTime(code.getExpireTime())) {
			return Ret.fail("msg", "验证码已失效");
		}
		return Ret.ok("msg", "验证成功");
	}
}
