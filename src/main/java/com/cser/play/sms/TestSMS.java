package com.cser.play.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestSMS {
	private static final String addr = "http://api.sms.cn/sms/";
	private static final String userId = "cc561659";

	/*
	 * 如uid是：test，登录密码是：123123 pwd=md5(123123test),即
	 * pwd=b9887c5ebb23ebb294acab183ecf0769
	 * 
	 */
	private static final String pwd = "048ce9357cc2152c86d5c9a2aac292cd";

	private static final String encode = "utf8";

	public static void send(String msgContent, String mobile) throws Exception {

		// 组建请求
		String straddr = addr + "?ac=send&uid=" + userId + "&pwd=" + pwd + "&mobile=" + mobile + "&encode=" + encode
				+ "&content=" + msgContent;

		StringBuffer sb = new StringBuffer(straddr);
		System.out.println("URL:" + sb);

		// 发送请求
		URL url = new URL(sb.toString());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		// 返回结果
		String inputline = in.readLine();
		System.out.println("Response:" + inputline);

	}

	public static void main(String[] args) {
		try {
			send("【】您的验证码是：{**}，请在10分钟内完成验证，请勿将该验证码泄露给他人（包括工作人员）", "18616802191");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建验证码
	 * 
	 * @param phone
	 * @return
	 */
	public String createRandomByPhone(String phone) {
		Long random = (long) ((Math.random() * 9 + 1) * 100000);
		return "" + random;
	}

	/**
	 * 替换验证码
	 * 
	 * @param verifyNumer
	 * @return
	 */
	public static String createVerifyNumber(String verifyNumer) {
		return String.format("【虾比】您的验证码是：%s，请在10分钟内完成验证，请勿将该验证码泄露给他人（包括工作人员）", verifyNumer);
	}

}
