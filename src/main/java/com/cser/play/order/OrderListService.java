package com.cser.play.order;

import java.util.List;

import com.cser.play.common.model.OrderList;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;

public class OrderListService {
	public static final OrderListService ME = new OrderListService();
	private OrderList dao = new OrderList();

	/**
	 * 查找订单明细
	 * 
	 * @param userId
	 * @param productId
	 * @return
	 */
	public OrderList findOrderList(int userId, int productId) {
		SqlPara sqlPara = dao.getSqlPara("orderList.findOrderList", userId, productId);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 订单明细列表
	 * 
	 * @param orderId
	 * @return
	 */
	public List<OrderList> findList(String orderId) {
		SqlPara sqlPara = dao.getSqlPara("orderList.findByOrderId", orderId);
		return dao.find(sqlPara);
	}

	/**
	 * 支付成功，修改订单明细状态
	 * 
	 * @param orderId
	 * @return
	 */
	public boolean updateStatus(String orderId) {
		SqlPara sqlPara = dao.getSqlPara("orderList.updateStatus", orderId);
		return Db.update(sqlPara) > 0;
	}

	/**
	 * 查询已支付订单列表
	 * 
	 * @param orderId
	 * @return
	 */
	public List<OrderList> findMyOrders(int userId) {
		SqlPara sqlPara = dao.getSqlPara("orderList.findMyOrders", userId);
		return dao.find(sqlPara);
	}

	/**
	 * 查询未支付订单列表
	 * 
	 * @param userId
	 * @return
	 */
	public List<OrderList> unpaidOrder(int userId) {
		SqlPara sqlPara = dao.getSqlPara("orderList.unpaidOrder", userId);
		return dao.find(sqlPara);
	}

	/**
	 * 删除订单列表
	 * 
	 * @param orderId
	 * @return
	 */
	public boolean deleteAll(String orderId) {
		SqlPara sqlPara = dao.getSqlPara("orderList.delete", orderId);
		return Db.update(sqlPara) > 0;
	}

}
