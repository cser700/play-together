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

package com.cser.play.common.kit;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import com.jfinal.kit.StrKit;

/**
 * @author res
 * ImageKit 图片高保真缩放与裁剪，不依赖于任何第三方库
 */
public class ImageKit {

	private final static String[] IMG_EXTS = new String[] { "jpg", "jpeg", "png", "bmp" };

	public static String getExtName(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index != -1 && (index + 1) < fileName.length()) {
			return fileName.substring(index + 1);
		} else {
			return null;
		}
	}

	/**
	 * 通过文件扩展名，判断是否为支持的图像文件，支持则返回 true，否则返回 false
	 */
	public static boolean isImageExtName(String fileName) {
		if (StrKit.isBlank(fileName)) {
			return false;
		}
		fileName = fileName.trim().toLowerCase();
		String ext = getExtName(fileName);
		if (ext != null) {
			for (String s : IMG_EXTS) {
				if (s.equals(ext)) {
					return true;
				}
			}
		}
		return false;
	}

	public static final boolean notImageExtName(String fileName) {
		return !isImageExtName(fileName);
	}

	public static BufferedImage loadImageFile(String sourceImageFileName) {
		if (notImageExtName(sourceImageFileName)) {
			throw new IllegalArgumentException("只支持如下几种类型的图像文件：jpg、jpeg、png、bmp");
		}

		File sourceImageFile = new File(sourceImageFileName);
		if (!sourceImageFile.exists() || !sourceImageFile.isFile()) {
			throw new IllegalArgumentException("文件不存在");
		}

		try {
			return ImageIO.read(sourceImageFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 以 maxWidth 为界对图像进行缩放，高保真保存 1：当图像 width > maxWidth 时，将宽度变为
	 * maxWidth，高度等比例进行缩放，高保真保存 2:当图像 width <= maxWidth 时，宽高保持不变，只进行高保真保存
	 */
	public static void zoom(int maxWidth, File srcFile, String saveFile) {
		float quality = 0.8f;

		try {
			BufferedImage srcImage = ImageIO.read(srcFile);
			int srcWidth = srcImage.getWidth();
			int srcHeight = srcImage.getHeight();

			// 当宽度在 maxWidth 范围之内，不改变图像宽高，只进行图像高保真保存
			if (srcWidth <= maxWidth) {
				/**
				 * 如果图像不进行缩放的话， resize 就没有必要了， 经过测试是否有 resize
				 * 这一步，生成的结果图像完全一样，一个字节都不差 所以删掉 resize，可以提升性能，少一步操作
				 */
				// BufferedImage ret = resize(srcImage, srcWidth, srcHeight);
				// saveWithQuality(ret, quality, saveFile);
				saveWithQuality(srcImage, quality, saveFile);
			}
			// 当宽度超出 maxWidth 范围，将宽度变为 maxWidth，而高度按比例变化
			else {
				// 计算缩放比率
				float scalingRatio = (float) maxWidth / (float) srcWidth; 
				// 计算缩放后的高度
				float maxHeight = ((float) srcHeight * scalingRatio); 
				BufferedImage ret = resize(srcImage, maxWidth, (int) maxHeight);
				saveWithQuality(ret, quality, saveFile);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对图片进行剪裁，只保存选中的区域
	 * 
	 * @param sourceImageFile
	 *            原图
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 */
	public static BufferedImage crop(String sourceImageFile, int left, int top, int width, int height) {
		if (notImageExtName(sourceImageFile)) {
			throw new IllegalArgumentException("只支持如下几种类型的图像文件：jpg、jpeg、png、bmp");
		}

		try {
			BufferedImage bi = ImageIO.read(new File(sourceImageFile));
			width = Math.min(width, bi.getWidth());
			height = Math.min(height, bi.getHeight());
			if (width <= 0) {
				width = bi.getWidth();
			}
			if (height <= 0) {
				height = bi.getHeight();
			}

			left = Math.min(Math.max(0, left), bi.getWidth() - width);
			top = Math.min(Math.max(0, top), bi.getHeight() - height);

			BufferedImage subImage = bi.getSubimage(left, top, width, height);
			// return ImageIO.write(bi, "jpeg", fileDest);
			return subImage; 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void save(BufferedImage bi, String outputImageFile) {
		FileOutputStream newImage = null;
		try {
			// ImageIO.write(bi, "jpg", new File(outputImageFile));
			ImageIO.write(bi, getExtName(outputImageFile), new File(outputImageFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (newImage != null) {
				try {
					newImage.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * 高保真缩放
	 */
	public static BufferedImage resize(BufferedImage bi, int toWidth, int toHeight) {
		Graphics g = null;
		try {
			// 从 BufferedImage 对象中获取一个经过缩放的 image
			Image scaledImage = bi.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH);
			// 创建 BufferedImage 对象，存放缩放过的 image
			BufferedImage ret = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
			g = ret.getGraphics();
			g.drawImage(scaledImage, 0, 0, null);
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (g != null) {
				g.dispose();
			}
		}
	}

	/**
	 * jfinal.com 使用参数为宽：200, 高：200, 质量：0.8， 生成大小为：6.79 KB (6,957 字节)
	 *
	 * 如果使用参数为宽：120, 高：120, 质量：0.8， 则生成的图片大小为：3.45 KB (3,536 字节)
	 *
	 * 如果使用参数为宽：300, 高：300, 质量：0.5， 则生成的图片大小为：7.54 KB (7,725 字节)
	 *
	 *
	 * 建议使用 0.8 的 quality 并且稍大点的宽高 只选用两种质量：0.8 与 0.9，这两个差别不是很大， 但是如果尺寸大些的话，选用
	 * 0.8 比 0.9 要划算，因为占用的空间差不多的时候，尺寸大些的更清晰
	 */
	public static void saveWithQuality(BufferedImage im, float quality, String outputImageFile) {
		ImageWriter writer = null;
		FileOutputStream newImage = null;
		try {
			// 输出到文件流
			newImage = new FileOutputStream(outputImageFile);

			writer = ImageIO.getImageWritersBySuffix("jpg").next();
			ImageWriteParam param = writer.getDefaultWriteParam();
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);
			ImageOutputStream os = ImageIO.createImageOutputStream(newImage);
			writer.setOutput(os);
			writer.write((IIOMetadata) null, new IIOImage(im, null, null), param);
			os.flush();
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				try {
					writer.dispose();
				} catch (Throwable e) {
				}
			}
			if (newImage != null) {
				try {
					newImage.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/*
	 * 老版本 public static void saveWithQuality(BufferedImage im, float quality,
	 * String outputImageFile) { FileOutputStream newImage = null; try { //
	 * 输出到文件流 newImage = new FileOutputStream(outputImageFile); JPEGImageEncoder
	 * encoder = JPEGCodec.createJPEGEncoder(newImage); JPEGEncodeParam jep =
	 * JPEGCodec.getDefaultJPEGEncodeParam(im); // 压缩质量, 0.75 就算是高质量
	 * jep.setQuality(quality, true); // jep.setQuality(0.9f, true);
	 * encoder.encode(im, jep); // 近JPEG编码 // newImage.close(); } catch
	 * (Exception e) { throw new RuntimeException(e); } finally { if (newImage
	 * != null) { try {newImage.close();} catch (IOException e) {throw new
	 * RuntimeException(e);} } } }
	 */
}
