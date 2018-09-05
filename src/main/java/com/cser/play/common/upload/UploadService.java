package com.cser.play.common.upload;

import java.io.File;
import java.util.UUID;

import com.cser.play.common.PlayConfig;
import com.cser.play.common.kit.ImageKit;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

public class UploadService {

	public static UploadService ME = new UploadService();

	/**
	 * 上传图片允许的最大尺寸，目前只允许 2M ，【实际调用需乘以1024】
	 */
	public static final int FILE_MAX_SIZE = 2000 ;

	public static final String UPLOAD_PATH = PlayConfig.UPLOAD_PATH;

	public static final String BASE_URL = PlayConfig.p.get("baseUrl");

	/**
	 * 上传图片，返回图片访问地址
	 * 
	 * @param file
	 * @return
	 */
	public Ret upload(UploadFile file) {
		Ret ret = checkUeditorUploadFile(file);
		if (ret != null) {
			return ret;
		}
		String fileName = UUID.randomUUID().toString().replace("-", "");
		String fileType = file.getFileName().substring(file.getFileName().lastIndexOf("."), file.getFileName().length());
		fileName = fileName + fileType;
		file.getFile().renameTo(new File(UPLOAD_PATH + "/" + fileName));
		String imageUrl = BASE_URL + "/images/" + fileName;
		return Ret.ok("msg", "上传成功").set("imageUrl", imageUrl);

	}

	/**
	 * 检查  上传图片的合法性
	 */
	private Ret checkUeditorUploadFile(UploadFile uf) {
		if (uf == null || uf.getFile() == null) {
			return Ret.fail("msg", "上传文件为 null");
		}
		if (ImageKit.notImageExtName(uf.getFileName())) {
			uf.getFile().delete(); // 非图片类型，立即删除，避免浪费磁盘空间
			return Ret.fail("msg", "只支持 jpg、jpeg、png、bmp 四种图片类型");
		}
		if (uf.getFile().length() > FILE_MAX_SIZE * 1024) {
			uf.getFile().delete(); // 图片大小超出范围，立即删除，避免浪费磁盘空间
			return Ret.fail("msg", "图片尺寸只允许" + FILE_MAX_SIZE + "K 大小");
		}
		return null;
	}

}
