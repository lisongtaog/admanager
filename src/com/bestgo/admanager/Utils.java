package com.bestgo.admanager;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

public class Utils {
    public static boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        Object isAdmin = session.getAttribute("isAdmin");
        if (isAdmin == null) {
            JsonObject json = new JsonObject();
            json.addProperty("ret", 0);
            json.addProperty("message", "请先登录");
            response.getWriter().write(json.toString());
            return false;
        }
        return true;
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static double convertDouble(Object value, double defaultValue) {
        try {
            if (value instanceof BigDecimal) {
                return ((BigDecimal)value).doubleValue();
            } else if (value instanceof Double) {
                return (double)value;
            } else if (value instanceof Long) {
                return (Long)value;
            } else if (value instanceof Integer) {
                return (Long)value;
            }
        } catch (Exception ex) {
        }
        return defaultValue;
    }
}
