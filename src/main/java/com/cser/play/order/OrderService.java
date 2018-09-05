package com.cser.play.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cser.play.account.UserAccountService;
import com.cser.play.common.enums.DealCategory;
import com.cser.play.common.enums.DealStatus;
import com.cser.play.common.model.DealRecord;
import com.cser.play.common.model.OrderList;
import com.cser.play.common.model.Orders;
import com.cser.play.common.model.Product;
import com.cser.play.common.model.UserAccount;
import com.cser.play.common.utils.SnowIdWorker;
import com.cser.play.deal.DealRecordService;
import com.cser.play.product.ProductService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

public class OrderService {
	public static final OrderService ME = new OrderService();
	private Orders dao = new Orders();
	private OrderListService orderListSrv = OrderListService.ME;

	/**
	 * 购物车添加商品
	 * 
	 * @param userId
	 * @param productId
	 *            产品ID
	 * @param quantity
	 *            产品数量
	 * @return
	 * @throws Exception
	 */
	public Ret addOrder(int userId, int productId) {
		Orders isExits = findOrder(userId);
		String orderId = "";
		// 判断是否存在未支付订单
		if (null == isExits) {
			Orders order = new Orders();
			try {
				orderId = String.valueOf(SnowIdWorker.getInstanceSnowflake().nextId());
				order.setId(String.valueOf(orderId));
				order.setUserId(userId);
				order.save();
			} catch (Exception e) {
				LogKit.error(e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			orderId = isExits.getId();
		}
		OrderList orderList = orderListSrv.findOrderList(userId, productId);
		// 判断是否存在订单明细记录
		if (null != orderList) {
			int quantity = orderList.getProductQuantity();
			orderList.setProductQuantity(quantity + 1);
			boolean result = orderList.update();
			if (result) {
				return Ret.ok("msg", "添加成功");
			}
		} else {
			Product pro = ProductService.ME.findProduct(productId);
			OrderList newOrderList = new OrderList();
			if (null != pro) {
				newOrderList.setOrderId(orderId);
				newOrderList.setProductId(pro.getId());
				newOrderList.setProductName(pro.getName());
				newOrderList.setProductQuantity(1);
				newOrderList.setProductPrice(pro.getPrice());
				boolean result = newOrderList.save();
				if (result) {
					return Ret.ok("msg", "添加成功");
				}
			}
		}
		return Ret.fail("msg", "添加失败");
	}

	/**
	 * 删除商品明细
	 * 
	 * @param userId
	 * @param productId
	 * @return
	 */
	public Ret deleteProduct(int userId, int productId) {
		OrderList orderList = orderListSrv.findOrderList(userId, productId);
		List<OrderList> list = orderListSrv.findList(orderList.getOrderId());
		int quantity = orderList.getProductQuantity();
		if (quantity > 1) {
			orderList.setProductQuantity(quantity - 1);
			boolean result = orderList.update();
			if (result) {
				return Ret.ok("msg", "删除成功");
			}
		} else {
			boolean result = orderList.delete();
			if (result) {
				if (list.size() == 1) {
					String orderId = list.get(0).getOrderId();
					delete(orderId);
				}
				return Ret.ok("msg", "删除成功");
			}
		}
		return Ret.fail("msg", "删除失败");
	}

	/**
	 * 查找订单 by userId
	 * 
	 * @param userId
	 * @return
	 */
	public Orders findOrder(int userId) {
		SqlPara sqlPara = dao.getSqlPara("orders.findOrder", userId);
		return dao.findFirst(sqlPara);
	}

	/**
	 * 查找订单 by orderId
	 * 
	 * @param orderId
	 * @return
	 */
	public Orders findOrderById(String orderId) {
		return dao.findById(orderId);
	}

	/**
	 * 删除订单
	 * 
	 * @param orderId
	 * @return
	 */
	public boolean delete(String orderId) {
		return dao.deleteById(orderId);
	}

	/**
	 * 清空购物车
	 * 
	 * @param orderId
	 * @return
	 */
	public Ret deleteAll(String orderId) {
		orderListSrv.deleteAll(orderId);
		boolean result = dao.deleteById(orderId);
		if (result) {
			return Ret.ok("msg", "删除成功");
		}
		return Ret.fail("msg", "删除失败");
	}

	/**
	 * 支付订单
	 * 
	 * @param orderId
	 * @return
	 */
	public Ret pay(String orderId, int userId) {
		List<OrderList> list = orderListSrv.findList(orderId);
		BigDecimal totalPrice = BigDecimal.valueOf(0);
		for (int i = 0; i < list.size(); i++) {
			OrderList order = list.get(i);
			int quantity = order.getProductQuantity();
			if (quantity > 1) {
				totalPrice = totalPrice.add(order.getProductPrice().multiply(BigDecimal.valueOf(quantity)));
			} else {
				totalPrice = totalPrice.add(order.getProductPrice());
			}
		}
		// 判断账户余额
		UserAccount account = UserAccountService.ME.queryBalance(userId);
		BigDecimal balance = account.getAvailable();
		if (-1 == account.getAvailable().compareTo(totalPrice)) {
			return Ret.fail("msg", "账户余额不足，支付失败");
		}
		// 更新账户余额
		account.setAvailable(balance.subtract(totalPrice));
		account.update();
		// 更新订单明细状态
		orderListSrv.updateStatus(orderId);
		// 更新订单状态
		Orders order = findOrderById(orderId);
		order.setCreateDate(new Date());
		order.setStatus("1");
		boolean result = order.update();
		if (result) {
			DealRecord record = new DealRecord();
			record.setUserId(userId);
			record.setAmount(totalPrice);
			record.setDealType(DealRecord.DEAL_PAY);
			record.setDealDesc(DealRecord.DEAL_PAY_DESC);
			record.setStatus(DealStatus.success.getKey());
			record.setCategory(DealCategory.expenditure.getKey());
			DealRecordService.ME.addDealRecord(record);
			return Ret.ok("msg", "支付成功");
		}
		return Ret.fail("msg", "支付失败");
	}

	/**
	 * 查询已支付订单
	 * 
	 * @param orderId
	 * @return
	 */
	public List<Object> myOrderList(int userId) {
		List<OrderList> list = orderListSrv.findMyOrders(userId);
		List<Object> orderList = new ArrayList<>();
		List<Object> detailList = new ArrayList<>();
		Map<String, Object> orderMap = new HashMap<String, Object>();
		Map<String, Object> detailMap = new HashMap<String, Object>();

		OrderList before = new OrderList();
		OrderList next = new OrderList();
		for (int i = 0; i < list.size(); i++) {
			if ("{}".equals(before.toString())) {
				before = list.get(i);
				orderMap.put("orderId", before.get("orderId"));
				orderMap.put("totalPrice", before.get("totalPrice"));
				orderMap.put("createDate", before.get("createDate"));
				orderList.add(orderMap);
			} else {
				next = list.get(i);
				if (before.get("orderId").equals(next.get("orderId"))) {
					continue;
				} else {
					before = next;
					orderMap = new HashMap<String, Object>();
					orderMap.put("orderId", before.get("orderId"));
					orderMap.put("totalPrice", before.get("totalPrice"));
					orderMap.put("createDate", before.get("createDate"));
					orderList.add(orderMap);
				}
			}
		}
		// 将list中列表数据，添加至oderList中的order
		for (int i = 0; i < orderList.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				@SuppressWarnings("unchecked")
				Map<String, Object> order = ((Map<String, Object>) orderList.get(i));
				OrderList detail = list.get(j);
				if (detail.get("orderId").equals(order.get("orderId"))) {
					detailMap = new HashMap<String, Object>();
					detailMap.put("productName", detail.get("productName"));
					detailMap.put("productPrice", detail.get("productPrice"));
					detailMap.put("productQuantity", detail.get("productQuantity"));
					detailList.add(detailMap);
					order.put("order", detailList);
				} else {
					detailList = new ArrayList<>();
					continue;
				}
			}
		}
		return orderList;
	}

	/**
	 * 查询未支付商品列表
	 * 
	 * @param userId
	 * @return
	 */
	public List<OrderList> unpaidOrder(int userId) {
		return orderListSrv.unpaidOrder(userId);
	}

}
