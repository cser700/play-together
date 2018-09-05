package com.cser.play.product;

import java.util.List;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.model.Product;

public class ProductController extends BaseController{

	private ProductService srv = new ProductService();
	
	public void list(){
		List<Product> list = srv.productList();
		renderJson(list);
	}
}
