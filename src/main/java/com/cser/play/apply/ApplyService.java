package com.cser.play.apply;

import com.cser.play.common.model.ApplyList;
import com.cser.play.common.model.UserInfo;
import com.cser.play.user.UserInfoService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 申请列表
 * 
 * @author res
 *
 */
public class ApplyService {
	public static ApplyService ME = new ApplyService();
	private ApplyList dao = new ApplyList();

	/**
	 * 申请列表
	 * 
	 * @param companyId
	 * @param pageNumber
	 * @return
	 */
	public Page<ApplyList> applyPage(int companyId, int pageNumber) {
		SqlPara sqlPara = dao.getSqlPara("apply.applyPage", companyId);
		return dao.paginate(pageNumber, 10, sqlPara);
	}

	/**
	 * 审核员工申请
	 * 
	 * @param id
	 * @param userId
	 * @return
	 */
	public Ret acceptUser(int id, int userId) {
		try {
			ApplyList apply = dao.findById(id);
			if (null == apply) {
				return Ret.fail("msg", "申请记录不存在，审核失败");
			}
			if (apply.getUserId() != userId) {
				return Ret.fail("msg", "审核失败");
			}
			apply.setStatus("1");
			boolean result = apply.update();
			if (!result) {
				return Ret.fail("msg", "审核失败");
			}
			UserInfo user = UserInfoService.ME.findUser(apply.getUserId());
			user.setCompanyId(apply.getCompanyId());
			user.setStatus("2");
			user.update();
			return Ret.ok("msg", "审核成功");
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 是否存在申请记录
	 * @param userId
	 * @param companyId
	 * @return
	 */
	public boolean isExits(int userId, int companyId) {
		SqlPara sqlPara = dao.getSqlPara("apply.isExits", userId, companyId);
		return dao.findFirst(sqlPara) != null;
	}

}
