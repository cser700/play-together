package com.cser.play.qr;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONObject;
import com.cser.play.common.PlayConfig;
import com.cser.play.common.model.CompanyInfo;
import com.cser.play.common.utils.QrCodeUtils;
import com.cser.play.company.CompanyService;
import com.cser.play.login.LoginService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

public class QrCodeService {

	// 微信小程序码 C 接口
	private static final String WX_C_CODE_URL = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=";

	public static final String UPLOAD_PATH = PlayConfig.UPLOAD_PATH;
	
	public static final String BASE_URL = PlayConfig.p.get("baseUrl");

	public static final QrCodeService ME = new QrCodeService();

	/**
	 * B接口生成小程序码
	 */
	public Ret getQrCode(int companyId) {
		CompanyInfo company = CompanyService.ME.querCompanyById(companyId);
		if (!StrKit.notNull(company)) {
			return Ret.fail("msg", "公司不存在");
		}
		if (StrKit.notBlank(company.getQrCode())) {
			return Ret.create("qrcode", company.getQrCode());
		}
		// 生成二維碼
		String qrcode = getC(companyId);
		company.setQrCode(qrcode);
		company.update();
		return Ret.create("qrcode", qrcode);
	}

	// 生成小程序二维码，C接口，限制：10万条
	public String getC(int companyId) {
		String qrcodeUrl = "";
		try {
			String access_token = LoginService.ME.getAccessToken();
			URL url = new URL(WX_C_CODE_URL + access_token);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");// 提交模式
			// conn.setConnectTimeout(10000);//连接超时 单位毫秒
			// conn.setReadTimeout(2000);//读取超时 单位毫秒
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// 发送请求参数
			JSONObject paramJson = new JSONObject();
			paramJson.put("path", "pages/QrCode/QrCode?companyId=" + companyId);
			paramJson.put("width", 430);
			printWriter.write(paramJson.toString());
			// flush输出流的缓冲
			printWriter.flush();
			// 开始获取数据
			BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());

			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			// buff用于存放循环读取的临时数据
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = bis.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			ByteArrayInputStream inputStream = new ByteArrayInputStream(swapStream.toByteArray());
			BufferedImage image = ImageIO.read(inputStream);
			/** 裁剪原图 目前访问微信 微信返回的是 470*535 像素 170620 */
			BufferedImage subImage = image.getSubimage(0, 0, image.getWidth(), (int) (image.getHeight() * 0.85));

			System.out.println(QrCodeUtils.decodeQrcode(subImage));

			BufferedImage inputbig = new BufferedImage(256, 256, BufferedImage.TYPE_INT_BGR);
			Graphics2D g = (Graphics2D) inputbig.getGraphics();
			g.drawImage(subImage, 0, 0, 256, 256, null); // 画图
			g.dispose();
			inputbig.flush();
			String uuid = UUID.randomUUID().toString();
			String imgName = uuid + ".png";
			qrcodeUrl = UPLOAD_PATH + "/" + imgName;
			ImageIO.write(inputbig, "png", new File(qrcodeUrl));
			qrcodeUrl = BASE_URL + "/images/" + imgName;
			return qrcodeUrl;
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		}
		return qrcodeUrl;
	}

}
