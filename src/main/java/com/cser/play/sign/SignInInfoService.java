package com.cser.play.sign;

import com.cser.play.common.model.SignInInfo;

public class SignInInfoService {

	public static final SignInInfoService ME = new SignInInfoService();
	
	private SignInInfo dao = new SignInInfo();
	
	
	/**
	 * 查询签到信息
	 * @param userId
	 * @return
	 */
	public SignInInfo query(int userId){
		return dao.findById(userId);
	}
}
