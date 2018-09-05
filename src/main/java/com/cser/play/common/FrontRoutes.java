/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.cser.play.common;

import com.cser.play.apply.ApplyController;
import com.cser.play.common.upload.UploadController;
import com.cser.play.company.CompanyController;
import com.cser.play.deal.DealRecordController;
import com.cser.play.gift.GiftController;
import com.cser.play.login.LoginController;
import com.cser.play.message.NoticeController;
import com.cser.play.order.OrderController;
import com.cser.play.pay.PayController;
import com.cser.play.product.ProductController;
import com.cser.play.qr.QrCodeController;
import com.cser.play.reg.RegController;
import com.cser.play.sign.SignInController;
import com.cser.play.suggest.SuggestController;
import com.cser.play.task.TaskController;
import com.cser.play.user.UserInfoController;
import com.jfinal.config.Routes;

/**
 * 前台路由
 * 
 * @author res
 *
 */
public class FrontRoutes extends Routes {

	@Override
	public void config() {
		add("/user", UserInfoController.class);
		add("/sign", SignInController.class);
		add("/gift", GiftController.class);
		add("/task", TaskController.class);
		add("/deal", DealRecordController.class);
		add("/company", CompanyController.class);
		add("/login", LoginController.class);
		add("/reg", RegController.class);
		add("/upload", UploadController.class);
		add("/qrcode", QrCodeController.class);
		add("/apply", ApplyController.class);
		add("/product", ProductController.class);
		add("/order", OrderController.class);
		add("/notice", NoticeController.class);
		add("/pay", PayController.class);
		add("/suggest",SuggestController.class);
	}
}
