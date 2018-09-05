package com.cser.play.common;

import java.sql.Connection;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.cser.play.common.model._MappingKit;
import com.cser.play.common.plugin.PlayRedisPlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

/**
 * 
 * @author res
 *
 */
public class PlayConfig extends JFinalConfig {

	// 先加载开发环境配置，再追加生产环境的少量配置覆盖掉开发环境配置
	public static Prop p = PropKit.use("play_config_pro.txt");

	/**
	 * 图片上传保存地址
	 */
	public static final String UPLOAD_PATH = p.get("upload_path");
	public static final String QRCODE_UPLOAD_PATH = p.get("qrcode_upload_path");

	private WallFilter wallFilter;

	/**
	 * 启动入口，运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 * 
	 * 使用本方法启动过第一次以后，会在开发工具的 debug、run configuration 中自动生成
	 * 一条启动配置项，可对该自动生成的配置再继续添加更多的配置项，例如 VM argument 可配置为： -XX:PermSize=64M
	 * -XX:MaxPermSize=256M 上述 VM 配置可以缓解热加载功能出现的异常
	 */
	public static void main(String[] args) {
		/**
		 * 特别注意：Eclipse 之下建议的启动方式
		 */
		JFinal.start("src/main/webapp", 8080, "/", 5);

		/**
		 * 特别注意：IDEA 之下建议的启动方式，仅比 eclipse 之下少了最后一个参数
		 */
		// JFinal.start("src/main/webapp", 80, "/");
	}

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(p.getBoolean("devMode", false));
		me.setJsonFactory(MixedJsonFactory.me());
		me.setBaseUploadPath(UPLOAD_PATH);
		me.setBaseDownloadPath(UPLOAD_PATH);
	}

	@Override
	public void configRoute(Routes me) {
		me.add(new FrontRoutes());
	}

	@Override
	public void configEngine(Engine me) {

	}

	/**
	 * 抽取成独立的方法，便于 _Generator 中重用该方法，减少代码冗余
	 */
	public static DruidPlugin getDruidPlugin() {
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin druidPlugin = getDruidPlugin();
		// 加强数据库安全
		wallFilter = new WallFilter();
		wallFilter.setDbType("mysql");
		druidPlugin.addFilter(wallFilter);
		druidPlugin.addFilter(new StatFilter());
		me.add(druidPlugin);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
		_MappingKit.mapping(arp);
		// 强制指定复合主键的次序，避免不同的开发环境生成在 _MappingKit 中的复合主键次序不相同
		arp.setPrimaryKey("document", "mainMenu,subMenu");
		me.add(arp);
		arp.setShowSql(p.getBoolean("devMode", false));
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/all_sqls.sql");
		arp.setShowSql(true);
		arp.setDevMode(true);

		me.add(new EhCachePlugin());
		me.add(new Cron4jPlugin(p));

		// redis
		RedisPlugin redis = new PlayRedisPlugin().config();
		me.add(redis);
	}

	@Override
	public void configInterceptor(Interceptors me) {
	}

	@Override
	public void configHandler(Handlers me) {
	}

	/**
	 * 本方法会在 jfinal 启动过程完成之后被回调，详见 jfinal 手册
	 */
	@Override
	public void afterJFinalStart() {
		wallFilter.getConfig().setSelectUnionCheck(false);
	}

}
