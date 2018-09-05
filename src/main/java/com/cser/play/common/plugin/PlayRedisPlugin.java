package com.cser.play.common.plugin;

import com.cser.play.common.PlayConfig;
import com.jfinal.kit.Prop;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.RedisPlugin;

public class PlayRedisPlugin {
	private String host;
	private int port;
	private int timeout;// 过期时间 单位 秒
	private String password;
	private int maxTotal; // 设置最大实例总数
	private int maxIdle; // 空闲数
	private int maxWait; // 等待时间 单位 ： 秒
	private String defualtCacheName = "redis";// 默认cacheName

	RedisPlugin redisPlugin;

	Prop p = PlayConfig.p;

	public RedisPlugin config() {
		try {
			host = p.get("redis_host").trim();
			port = Integer.valueOf(p.get("redis_port").trim());
			timeout = Integer.valueOf(p.get("redis_timeout").trim());
			password = null != p.get("redis_password") ? p.get("redis_password").trim() : null;
			maxTotal = Integer.valueOf(p.get("redis_maxTotal").trim());
			maxIdle = Integer.valueOf(p.get("redis_maxIdle").trim());
			maxWait = Integer.valueOf(p.get("redis_maxWait").trim());

			if (StrKit.notBlank(password)) {
				redisPlugin = new RedisPlugin(defualtCacheName, host, port, timeout * 1000, password);
			} else {
				redisPlugin = new RedisPlugin(defualtCacheName, host, port, timeout * 1000);
			}

			redisPlugin.getJedisPoolConfig().setMaxTotal(maxTotal);
			redisPlugin.getJedisPoolConfig().setMaxIdle(maxIdle);
			redisPlugin.getJedisPoolConfig().setMaxWaitMillis(maxWait * 1000);
			return redisPlugin;
		} catch (Exception e) {
			throw new RuntimeException("init RedisPlugin config exception ", e);
		}

	}
}
