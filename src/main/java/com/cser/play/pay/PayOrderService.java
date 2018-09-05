package com.cser.play.pay;

import java.util.Date;

import com.cser.play.common.model.PayOrder;
import com.cser.play.common.utils.PlayDateUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;

public class PayOrderService {

	public static final PayOrderService ME = new PayOrderService();

	private PayOrder dao = new PayOrder();

	/**
	 * 保存支付订单
	 * 
	 * @param order
	 */
	public void add(PayOrder order) {
		if (StrKit.notBlank(order.getOrderNo())) {
			order.save();
		}
	}

	/**
	 * 修改支付状态
	 * 
	 * @param orderNo
	 * @param result
	 */
	public boolean update(String orderNo, String result) {
		String currentDate = PlayDateUtil.dateToStrLong(new Date());
		return Db.update("update pay_order set status = ?, update_date = ? where order_no = ?", result, currentDate,
				orderNo) > 0;
	}

	/**
	 * 根据订单号查询订单
	 * 
	 * @param orderNo
	 * @return
	 */
	public PayOrder findByOrderNo(String orderNo) {
		SqlPara sqlPara = dao.getSqlPara("pay.findByOrderNo", orderNo);
		return dao.findFirst(sqlPara);
	}
}
