package com.cser.play.pay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jdom2.JDOMException;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.utils.PayUtil;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;

/**
 * 
 * @author res
 *
 */
public class PayController extends BaseController {

	private PayService srv = PayService.ME;

	/**
	 * 支付
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 */
	public void pay() throws IOException, JDOMException {
		String jsonStr = getPara();
		String openid = JSONkit.jsonValue(jsonStr, "openid");
		double amount = Double.parseDouble(JSONkit.jsonValue(jsonStr, "amount"));
		String result = srv.pay(openid, amount);
		renderJson(result);
	}

	// /**
	// * 支付结果
	// */
	// public void result() {
	// String jsonStr = getPara();
	// String orderNo = JSONkit.jsonValue(jsonStr, "orderNo");
	// String result = JSONkit.jsonValue(jsonStr, "result");
	// srv.payNotify(orderNo, result);
	// renderText("");
	// }

	/**
	 * 支付结果回调-微信
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public void wxNotify() throws IOException, JDOMException {
		LogKit.info("##################### notify 微信支付回调   ####################");
		InputStream inStream = getRequest().getInputStream();
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		String resultxml = new String(outSteam.toByteArray(), "utf-8");
		Map<String, String> params = PayUtil.doXMLParse(resultxml);
		outSteam.close();
		inStream.close();

		LogKit.info("##### params = " + params);
		if (PayUtil.isTenpaySign(params)) {
			LogKit.info("===============付款失败==============");
			// 支付失败
			renderJson(Ret.fail("return_code", "FAIL").set("return_msg", "return_code不正确"));
		} else {
			LogKit.info("===============付款成功==============");
			// 处理订单状态
			String resultCode = params.get("result_code");
			if (resultCode.equals("SUCCESS")) {
				String orderNo = params.get("out_trade_no");
				String result = "1";
				srv.payNotify(orderNo, result);
				// LogKit.info("### resultCode = " + resultCode);
				// String total_fee = params.get("total_fee");
				// LogKit.info("### total_fee = " + total_fee);
				// double v = Double.valueOf(total_fee) / 100;
				// LogKit.info("### v " + v);
				// LogKit.info("### out_trade_no " + out_trade_no);
				// Date accountTime =
				// PlayDateUtil.stringtoDate(params.get("time_end"),
				// "yyyyMMddHHmmss");
				// LogKit.info("### accountTime " + accountTime);
				// String ordertime = PlayDateUtil.dateToString(new Date(),
				// "yyyy-MM-dd HH:mm:ss");
				// LogKit.info("### ordertime " + ordertime);
				// String totalAmount = String.valueOf(v);
				// LogKit.info("### totalAmount " + totalAmount);
				// String appId = params.get("appid");
				// LogKit.info("### appId " + appId);
				// String tradeNo = params.get("transaction_id");
				// LogKit.info("### tradeNo " + tradeNo);
			}
			// renderJson(Ret.ok("return_msg", "OK").set("return_code",
			// "SUCCESS"));
			renderText("");
		}
	}

}
