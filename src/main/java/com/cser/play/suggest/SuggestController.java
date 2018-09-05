package com.cser.play.suggest;

import com.cser.play.common.controller.BaseController;
import com.cser.play.common.model.Suggest;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

public class SuggestController extends BaseController {

	private SuggestService srv = SuggestService.ME;

	/**
	 * 提交意见
	 */
	public void add() {
		Suggest suggest = getJSONObject(getPara(), Suggest.class);
		if (!StrKit.notBlank(suggest.getContent())) {
			renderJson(Ret.fail("msg", "内容不能为空"));
			return;
		}
		Ret ret = srv.add(suggest);
		renderJson(ret);
	}
}
