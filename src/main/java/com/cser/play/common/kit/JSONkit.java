package com.cser.play.common.kit;

import com.alibaba.fastjson.JSONObject;

/**
 * json工具类
 * @author res
 *
 */
public class JSONkit {
	
	/**
	 * 根据json字符串，返回指定key value值
	 * @param jsonStr
	 * @param key
	 * @return
	 */
	public static String jsonValue(String jsonStr, String key){
		JSONObject obj = JSONObject.parseObject(jsonStr);
		return obj.getString(key);
	}
}
