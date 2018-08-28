package com.bestgo.admanager.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * Created by jikai on 5/15/18.
 */
public class LogUtils {
    public static void logRequest(HttpServletRequest request) {
        Logger logger = Logger.getLogger("request");
        String clientIp = request.getRemoteHost();
        String nickName = (String)request.getSession().getAttribute("nickname");
        JsonObject json = new JsonObject();
        json.addProperty("clientIp", clientIp);
        json.addProperty("nickName", nickName);
        json.addProperty("requestPath", request.getRequestURI());
        JsonObject parameters = new JsonObject();
        Set<String> keys = request.getParameterMap().keySet();
        for (String key : keys) {
            parameters.addProperty(key, request.getParameter(key));
        }
        json.add("parameters", parameters);
        logger.debug(json);
    }
}
