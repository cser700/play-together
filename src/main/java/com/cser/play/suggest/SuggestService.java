package com.cser.play.suggest;

import com.cser.play.common.model.Suggest;
import com.jfinal.kit.Ret;

/**
 * 意见反馈
 * 
 * @author res
 *
 */
public class SuggestService {
	public static final SuggestService ME = new SuggestService();

	/**
	 * 新增意见反馈
	 * 
	 * @param suggest
	 * @return
	 */
	public Ret add(Suggest suggest) {
		boolean result = suggest.save();
		if (result) {
			return Ret.ok("msg", "保存成功");
		} else {
			return Ret.fail("msg", "保存失败");
		}
	}

}
