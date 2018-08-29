package com.bestgo.admanager.utils;

import org.apache.log4j.Logger;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author mengjun
 * @date 2018/8/28 21:35
 * @desc Jedis连接池工具类
 */
public class JedisPoolUtil {
	private static final Logger LOGGER = Logger.getLogger(JedisPoolUtil.class);
	private static final JedisPoolConfig CONFIG = new JedisPoolConfig(); // 连接池配置
	public static JedisPool jedisPool; // 连接池

	static {
		ResourceBundle rb = ResourceBundle.getBundle("redis");
		if (rb == null) {
			LOGGER.error(" [redis.properties] is not found!");
		} else {
			//连接耗尽时是否阻塞, false报异常,true阻塞直到超时, 默认true
			CONFIG.setBlockWhenExhausted("true".equals(rb.getString("blockWhenExhausted")));
			//是否启用后进先出, 默认true
			CONFIG.setLifo("true".equals(rb.getString("lifo")));
			//最大空闲连接数
			CONFIG.setMaxIdle(Integer.valueOf(rb.getString("maxIdle")));
			CONFIG.setMinIdle(Integer.valueOf(rb.getString("minIdle")));
			//最大连接数
			CONFIG.setMaxTotal(Integer.valueOf(rb.getString("maxTotal")));
			//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
			CONFIG.setMaxWaitMillis(Integer.valueOf(rb.getString("maxWaitMillis")));
			//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
			CONFIG.setMinEvictableIdleTimeMillis(Integer.valueOf(rb.getString("minEvictableIdleTimeMillis")));
			//创建连接时检查是否可用
			CONFIG.setTestOnCreate("true".equals(rb.getString("testOnCreate")));
			//获取连接时检查是否可用
			CONFIG.setTestOnBorrow("true".equals(rb.getString("testOnBorrow")));
			//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
			CONFIG.setTimeBetweenEvictionRunsMillis(Integer.valueOf(rb.getString("timeBetweenEvictionRunsMillis")));
			//在空闲时检查有效性, 默认false
			CONFIG.setTestWhileIdle("true".equals(rb.getString("testWhileIdle")));
			//每次逐出检查时 逐出的最大数目 默认3
			CONFIG.setNumTestsPerEvictionRun(Integer.valueOf(rb.getString("numTestsPerEvictionRun")));

			//单点模式下
			jedisPool = new JedisPool(CONFIG, rb.getString("redis.address"), Integer.valueOf(rb.getString("redis.port"))); // 初始化连接池
		}
	}

	/**
	 * 获取Jedis连接
	 * 
	 * @return Jedis连接
	 */
	public static Jedis getJedis() {
		return jedisPool.getResource();
	}

}