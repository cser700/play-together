package com.cser.play.sign;

import java.util.Date;

import com.cser.play.common.model.SignSupplement;
import com.cser.play.common.utils.PlayDateUtil;
import com.jfinal.plugin.activerecord.SqlPara;

public class SignSupplementService {
	public static final SignSupplementService ME = new SignSupplementService();

	private SignSupplement dao = new SignSupplement();

	
	/**
	 * 查询当月是否存在补签
	 * @param userId
	 * @return
	 */
	public boolean querySupplement(int userId) {
		String nowDate = PlayDateUtil.dateToStr(new Date());
		SqlPara sqlPara = dao.getSqlPara("sign.querySupplement", userId, nowDate);
		return dao.findFirst(sqlPara) != null;
	}
}
