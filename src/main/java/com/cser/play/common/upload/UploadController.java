package com.cser.play.common.upload;

import com.cser.play.common.controller.BaseController;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

public class UploadController extends BaseController{
	private UploadService srv = UploadService.ME;
	
	public void upload(){
		UploadFile file = getFile();
		Ret ret = srv.upload(file);
		renderJson(ret);
	}
}
