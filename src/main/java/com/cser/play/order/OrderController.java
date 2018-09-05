package com.cser.play.order;

import java.util.List;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.kit.JSONkit;
import com.cser.play.common.model.OrderList;
import com.jfinal.kit.Ret;

public class OrderController extends BaseController {
	private OrderService srv = OrderService.ME;

	/**
	 * 添加商品
	 */
	public void add() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		int productId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "productId"));
		Ret ret = srv.addOrder(userId, productId);
		renderJson(ret);
	}

	/**
	 * 刪除商品
	 */
	public void delete() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		int productId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "productId"));
		Ret ret = srv.deleteProduct(userId, productId);
		renderJson(ret);
	}

	/**
	 * 支付
	 */
	public void pay() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		String orderId = JSONkit.jsonValue(jsonStr, "orderId");
		Ret ret = srv.pay(orderId, userId);
		renderJson(ret);
	}

	/**
	 * 已支付订单
	 */
	public void myOrder() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		List<Object> list = srv.myOrderList(userId);
		renderJson(list);
	}

	/**
	 * 查询未支付订单列表
	 */
	public void unpaid() {
		String jsonStr = getPara();
		int userId = Integer.parseInt(JSONkit.jsonValue(jsonStr, "userId"));
		List<OrderList> list = srv.unpaidOrder(userId);
		renderJson(list);
	}

	/**
	 * 清空购物车
	 */
	public void deleteAll() {
		String jsonStr = getPara();
		String orderId = JSONkit.jsonValue(jsonStr, "orderId");
		Ret ret = srv.deleteAll(orderId);
		renderJson(ret);
	}

}
