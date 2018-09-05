package com.cser.play.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jdom2.JDOMException;

import com.cser.play.account.CompanyAccountService;
import com.cser.play.common.PlayConfig;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.model.CompanyAccount;
import com.cser.play.common.model.PayOrder;
import com.cser.play.common.model.UserInfo;
import com.cser.play.common.utils.HttpUtil;
import com.cser.play.common.utils.PayUtil;
import com.cser.play.common.utils.PlayDateUtil;
import com.cser.play.common.utils.XMLUtil;
import com.cser.play.user.UserInfoService;
import com.google.gson.Gson;
import com.jfinal.kit.Prop;
import com.jfinal.kit.StrKit;

/**
 * 小程序支付
 * 
 * @author res
 *
 */
public class PayService {

	public static final PayService ME = new PayService();

	private static Prop p = PlayConfig.p;
	// 小程序
	private static final String APP_ID = p.get("mini_program_app_id");
	// 商户号
	private static final String MCH_NO = "";
	// 商户32位私钥
	private static final String PRIVATE_KEY = "";

	/**
	 * 
	 * @param openid
	 * @param amount
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	@SuppressWarnings("rawtypes")
	public String pay(String openid, double amount) throws IOException, JDOMException {
		// 得到小程序传过来的价格，注意这里的价格必须为整数，1代表1分，所以传过来的值必须*100；
		int fee = (int) (amount * 100);
		// 订单编号
		String orderNo = PlayDateUtil.getNowDateHHmmss() + System.currentTimeMillis();
		// 订单标题
		String title = "余额充值";
		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
		packageParams.put("appid", APP_ID);
		packageParams.put("mch_id", MCH_NO);
		packageParams.put("nonce_str", orderNo);
		// 支付主体
		packageParams.put("body", title);
		// 编号
		packageParams.put("out_trade_no", orderNo);
		// 价格
		packageParams.put("total_fee", fee);
		// 支付返回地址，不用纠结这个东西，随便写了一个接口，内容什么都没有
		packageParams.put("notify_url", "*");
		// 这个api有，固定的
		packageParams.put("trade_type", "JSAPI");
		packageParams.put("openid", openid);
		// 获取sign, 手动设置的32位密钥
		String sign = PayUtil.createSign("UTF-8", packageParams, PRIVATE_KEY);
		packageParams.put("sign", sign);
		// 转成XML
		String requestXML = PayUtil.getRequestXml(packageParams);
		System.out.println(requestXML);
		// 得到含有prepay_id的XML
		String resXml = HttpUtil.postData("https://api.mch.weixin.qq.com/pay/unifiedorder", requestXML);
		System.out.println(resXml);
		// 解析XML存入Map
		Map map = XMLUtil.doXMLParse(resXml);
		System.out.println(map);
		// 得到prepay_id
		String prepay_id = (String) map.get("prepay_id");
		SortedMap<Object, Object> packageP = new TreeMap<Object, Object>();
		// !注意，这里是appId,上面是appid
		packageP.put("appId", APP_ID);
		// 时间戳
		packageP.put("nonceStr", orderNo);
		// 必须把package写成
		packageP.put("package", "prepay_id=" + prepay_id);
		// paySign加密
		packageP.put("signType", "MD5");
		packageP.put("timeStamp", (System.currentTimeMillis() / 1000) + "");
		// 得到paySign
		String paySign = PayUtil.createSign("UTF-8", packageP, PRIVATE_KEY);
		packageP.put("paySign", paySign);
		// 将packageP数据返回给小程序
		Gson gson = new Gson();
		String result = gson.toJson(packageP);
		System.out.println(result);

		// 支付订单保存
		if (!result.contains("null")) {
			PayOrder order = new PayOrder();
			order.setOrderNo(orderNo);
			order.setOpenId(openid);
			order.setAmount(BigDecimal.valueOf(amount));
			order.setStatus(DealStatus.progress.getKey());
			order.setCreateDate(new Date());
			order.save();
		}
		return result;
	}

	/**
	 * 支付结果回调
	 * 
	 * @param orderNo
	 * @param result
	 */
	public void payNotify(String orderNo, String result) {
		boolean updateResult = PayOrderService.ME.update(orderNo, result);
		// 充值成功
		if (updateResult && DealStatus.success.getKey().equals(result)) {
			PayOrder order = PayOrderService.ME.findByOrderNo(orderNo);
			UserInfo user = null;
			if (null != order) {
				String openId = order.getOpenId();
				if (StrKit.notBlank(openId)) {
					user = UserInfoService.ME.findByOpenId(openId);
				}
			}
			if (null != user) {
				int companyId = user.getCompanyId();
				CompanyAccount companyAccount = CompanyAccountService.ME.findCompanyAccount(companyId);
				// 充值成功，修改企业账户余额
				companyAccount.setAmount(companyAccount.getAmount().add(order.getAmount()));
				companyAccount.update();
			}
		}
	}

}
