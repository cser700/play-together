package com.cser.play.common.model;

/**
 * 
 * @author res
 *
 */
public class SmsModel {
	private String code = CODE_SENDFAILD_DONEFAILD;
	private String msg = MSG_SENDFAILD_DONEFAILD;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return (code.equals(CODE_SENDSUCCESS) || code == CODE_SENDSUCCESS);
	}

	public boolean invalidPhoneNumber() {
		return code.equals(CODE_SENDFAILD_INVALIEMOBILENUM);
	}

	public static final String CODE_SENDSUCCESS = "100";
	public static final String MSG_SENDSUCCESS = "发送成功";

	public static final String CODE_SENDFAILD_VERIFYFAILD = "101";
	public static final String MSG_SENDFAILD_VERIFYFAILD = "验证失败";

	public static final String CODE_SENDFAILD_NOTENOUGH = "102";
	public static final String MSG_SENDFAILD_NOTENOUGH = "短信不足";

	public static final String CODE_SENDFAILD_DONEFAILD = "103";
	public static final String MSG_SENDFAILD_DONEFAILD = "操作失败";

	public static final String CODE_SENDFAILD_INVALIDECHAR = "104";
	public static final String MSG_SENDFAILD_INVALIDECHAR = "非法字符";

	public static final String CODE_SENDFAILD_TOOLONG = "105";
	public static final String MSG_SENDFAILD_TOOLONG = "内容过多";

	public static final String CODE_SENDFAILD_TOOMANYMOILENUM = "106";
	public static final String MSG_SENDFAILD_TOOMANYMOILENUM = "号码过多";

	public static final String CODE_SENDFAILD_FREQUENCYOVERHEAD = "107";
	public static final String MSG_SENDFAILD_FREQUENCYOVERHEAD = "频率过快";

	public static final String CODE_SENDFAILD_EMPTYCONTEXT = "108";
	public static final String MSG_SENDFAILD_EMPTYCONTEXT = "号码内容空";

	public static final String CODE_SENDFAILD_ACCOUNTFREEZE = "109";
	public static final String MSG_SENDFAILD_ACCOUNTFREEZE = "账号冻结";

	public static final String CODE_SENDFAILD_FORBIDSAMEMSG = "110";
	public static final String MSG_SENDFAILD_FORBIDSAMEMSG = "禁止频繁单条发送";

	public static final String CODE_SENDFAILD_INVALIEMOBILENUM = "111";
	public static final String MSG_SENDFAILD_INVALIEMOBILENUM = "号码错误";

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{").append("\"").append("code").append("\":\"").append(code).append("\",").append("\"")
				.append("msg").append("\":\"").append(msg).append("\"").append("\"}");
		return builder.toString();
	}
}
