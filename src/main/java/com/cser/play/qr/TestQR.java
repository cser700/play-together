package com.cser.play.qr;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONObject;
import com.cser.play.common.utils.QrCodeUtils;
import com.cser.play.login.LoginService;

/**
 * @version: V1.0
 * @author: fendo
 * @className: TestQR
 * @packageName: com.xxx
 * @description: 二维码测试类
 * @data: 2018-04-17 14:23
 **/
public class TestQR {

	// C接口
	public static void getC() {
		try {
			String access_token = LoginService.ME.getAccessToken();
			URL url = new URL("https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode?access_token=" + access_token);
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
			paramJson.put("path", "pages/index/index?companyId=1");
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
			ImageIO.write(inputbig, "png", new File("D:\\qrcode\\" + imgName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getB(){
		try {
			String access_token = LoginService.ME.getAccessToken();
			URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + access_token);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");// 提交模式
			// conn.setConnectTimeout(10000);//连接超时 单位毫秒
			// conn.setReadTimeout(2000);//读取超时 单位毫秒
			// 发送POST请求必须设置如下两行
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			// // 发送请求参数
			// JSONObject paramJson = new JSONObject();
			// paramJson.put("scene", "companyId=1");
			// paramJson.put("page", "pages/index/index");
			// paramJson.put("width", 430);
			// paramJson.put("auto_color", true);
			// // line_color生效
			// JSONObject lineColor = new JSONObject();
			// lineColor.put("r", 0);
			// lineColor.put("g", 0);
			// lineColor.put("b", 0);
			// paramJson.put("line_color", lineColor);

			// 发送请求参数
			JSONObject paramJson = new JSONObject();
			paramJson.put("scene", "companyId=1");
			paramJson.put("page", "pages/index/index");
			paramJson.put("width", 430);
			paramJson.put("auto_color", true);
			/**
			 * line_color生效 paramJson.put("auto_color", false); JSONObject
			 * lineColor = new JSONObject(); lineColor.put("r", 0);
			 * lineColor.put("g", 0); lineColor.put("b", 0);
			 * paramJson.put("line_color", lineColor);
			 */

			printWriter.write(paramJson.toString());
			// flush输出流的缓冲
			printWriter.flush();
			// 开始获取数据
			BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
			OutputStream os = new FileOutputStream(new File("D:\\qrcode\\b\\abc.png"));
			int len;
			byte[] arr = new byte[1024];
			while ((len = bis.read(arr)) != -1) {
				os.write(arr, 0, len);
				os.flush();
			}
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		getC();
	}
}
