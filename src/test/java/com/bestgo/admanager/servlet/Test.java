package com.bestgo.admanager.servlet;

import com.bestgo.admanager.bean.AppBean;
import com.bestgo.admanager.utils.JedisPoolUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mengjun
 * @date 2018/8/28 21:26
 * @desc
 */
public class Test {
    public static void main(String[] args) {
        Jedis jedis = JedisPoolUtil.getJedis();
        String aa = jedis.get("aa");
        java.lang.System.out.println(aa);
        java.lang.System.out.println("-------------------------");
        jedis.del("aa");
        aa = jedis.get("aa");
        java.lang.System.out.println(aa);
    }
}
