package com.cser.play.common.utils;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
 
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
 
/**
 * 二维码工具类
 * @author res
 *
 */
public class QrCodeUtils {
 
    /**
     * 解析二维码（QRCode）
     * @param image
     * @return
     */
    public static String decodeQrcode(BufferedImage image) throws NotFoundException {
 
        MultiFormatReader formatReader = new MultiFormatReader();
 
        BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
 
        //定义二维码的参数:
        Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");//定义字符集
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        Result result = formatReader.decode(binaryBitmap, hints);//开始解析
 
        return result.getText();
    }
 
    /**
     * 流图片解码
     * @param   input
     * @return  String
     */
    public static String decodeQrcode(InputStream input) throws NotFoundException, IOException {
 
        BufferedImage image = ImageIO.read(input);
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
 
        Map<DecodeHintType,Object> hints = new LinkedHashMap<DecodeHintType,Object>();
        // 解码设置编码方式为：utf-8，
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        //优化精度
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        //复杂模式，开启PURE_BARCODE模式
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }
 
 
}