package com.cser.play.product;

import java.util.List;

import com.cser.play.common.model.Product;
import com.jfinal.plugin.activerecord.SqlPara;

public class ProductService {
	public static final ProductService ME = new ProductService();
	private Product dao = new Product();
	
	/**
	 * 商品列表
	 * @return
	 */
	public List<Product> productList(){
		SqlPara sqlPara = dao.getSqlPara("product.list");
		return dao.find(sqlPara);
	}
	
	/**
	 * 查找商品
	 * @param productId
	 * @return
	 */
	public Product findProduct(int productId){
		SqlPara sqlPara = dao.getSqlPara("product.findProduct", productId);
		return dao.findFirst(sqlPara);
	}
}
