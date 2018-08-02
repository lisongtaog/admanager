package com.bestgo.admanager.utils;

import com.bestgo.admanager.bean.User;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class LoginUserSessionCacheUtil {
    public static void saveSessionInCache(ServletContext context, HttpSession session) {
        if (context == null || session == null) return;
        Jedis jedis = (Jedis)context.getAttribute("cache");
        if (jedis == null) {
            jedis = new Jedis("52.14.153.90", 6379);
            context.setAttribute("cache", jedis);
        }
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            JsonObject json = new JsonObject();
            json.addProperty("isAdmin", true);
            json.addProperty("id", loginUser.getId());
            json.addProperty("userType", loginUser.getUserType());
            json.addProperty("nickName", loginUser.getNickname());
            json.addProperty("userName", loginUser.getUsername());
            String jsonString = json.toString();
            jedis.set(session.getId(), jsonString);
        }
    }

    public static void loadSessionFromCache(ServletContext context, HttpSession session) {
        if (context == null || session == null) return;
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) return;
        Jedis jedis = (Jedis)context.getAttribute("cache");
        if (jedis == null) return;
        String jsonString = jedis.get(session.getId());
        if (jsonString == null) return;
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
        session.setAttribute("isAdmin", json.get("isAdmin").getAsBoolean());
        loginUser = new User();
        loginUser.setId(json.get("id").getAsInt());
        loginUser.setUserType(json.get("userType").getAsInt());
        loginUser.setNickname(json.get("nickName").getAsString());
        loginUser.setUsername(json.get("userName").getAsString());
        session.setAttribute("loginUser", loginUser);
    }
}
