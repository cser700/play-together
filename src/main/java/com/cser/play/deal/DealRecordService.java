package com.cser.play.deal;

import java.math.BigDecimal;

import com.cser.play.common.model.DealRecord;
import com.cser.play.common.utils.PlayDateUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 交易记录
 * 
 * @author res
 *
 */
public class DealRecordService {
	public static final DealRecordService ME = new DealRecordService();
	private DealRecord dao = new DealRecord();

	/**
	 * 收入列表
	 * 
	 * @param userId
	 * @param pageNum
	 * @return
	 */
	public Page<DealRecord> incomeList(int userId, int pageNum) {
		SqlPara sqlPara = dao.getSqlPara("deal.incomeList", userId);
		return dao.paginate(pageNum, 100, sqlPara);
	}
	

	/**
	 * 支出列表
	 * 
	 * @param userId
	 * @param pageNum
	 * @return
	 */
	public Page<DealRecord> expenditureList(int userId, int pageNum) {
		SqlPara sqlPara = dao.getSqlPara("deal.expenditureList", userId);
		return dao.paginate(pageNum, 100, sqlPara);
	}
	
	/**
	 * 交易记录 dealAllList
	 * 
	 * @param userId
	 * @param pageNum
	 * @return
	 */
	public Page<DealRecord> dealAllList(int userId,int targetUserId, int pageNum) {
		SqlPara sqlPara = null;
		if (userId != targetUserId) {
			sqlPara = dao.getSqlPara("deal.dealAllList", targetUserId);
		} else {
			sqlPara = dao.getSqlPara("deal.dealAllList", userId);
		}
		return dao.paginate(pageNum, 100, sqlPara);
	}

	/**
	 * 添加交易记录
	 * 
	 * @param userId
	 * @param dealType
	 * @param dealDesc
	 */
	public void addDealRecord(DealRecord dealRecord) {
		try {
			if (dealRecord.getAmount().compareTo(BigDecimal.valueOf(0)) == 1 && StrKit.notBlank(dealRecord.getUserId() + "", dealRecord.getDealType(),
					dealRecord.getDealDesc(), dealRecord.getStatus(), dealRecord.getCategory())) {
				dealRecord.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加交易记录
	 * 
	 * @param userId
	 * @param dealType
	 * @param dealDesc
	 */
	public void addDealRecord(int userId, int acceptUserId, String dealType, String dealDesc, String status,
			String category) {
		DealRecord dealRecord = new DealRecord();
		dealRecord.setUserId(userId);
		dealRecord.setDealType(dealType);
		dealRecord.setDealDesc(dealDesc);
		dealRecord.setStatus(status);
		dealRecord.setCategory(category);
		try {
			if (StrKit.notBlank(userId + "", dealType, dealDesc, status, category)) {
				dealRecord.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 周榜
	 * 
	 * @param today
	 * @return
	 */
	public Page<DealRecord> getWeekList(String today, int pageNum, int comapnyId) {
		Page<DealRecord> page = new Page<DealRecord>();
		try {
			String[] arr = PlayDateUtil.getWeekStartandEndDate(today);
			String beginDate = arr[0], endDate = arr[1];
			SqlPara sqlPara = dao.getSqlPara("deal.dealList", beginDate, endDate, comapnyId);
			page = dao.paginate(pageNum, 100, sqlPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 月榜
	 * 
	 * @param today
	 * @return
	 */
	public Page<DealRecord> getMonthList(String today, int pageNum, int comapnyId) {
		Page<DealRecord> page = new Page<DealRecord>();
		try {
			String[] arr = PlayDateUtil.getMonthStartAndEndDate(today);
			String beginDate = arr[0], endDate = arr[1];
			SqlPara sqlPara = dao.getSqlPara("deal.dealList", beginDate, endDate, comapnyId);
			page = dao.paginate(pageNum, 100, sqlPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 年榜
	 * 
	 * @param today
	 * @return
	 */
	public Page<DealRecord> getYearList(String today, int pageNum, int comapnyId) {
		Page<DealRecord> page = new Page<DealRecord>();
		try {
			String[] arr = PlayDateUtil.getYearStartAndEndDate(today);
			String beginDate = arr[0], endDate = arr[1];
			SqlPara sqlPara = dao.getSqlPara("deal.dealList", beginDate, endDate, comapnyId);
			page = dao.paginate(pageNum, 100, sqlPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	/**
	 * 公司分配记录
	 * 
	 * @param pageNumber
	 * @param adminUserId
	 * @param companyId
	 * @return
	 */
	public Page<DealRecord> assignPage(int pageNumber, int adminUserId, int companyId) {
		SqlPara sqlPara = dao.getSqlPara("deal.assignPage", adminUserId, companyId);
		return dao.paginate(pageNumber, 100, sqlPara);
	}

}
