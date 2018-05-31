package com.bestgo.admanager.servlet;

import com.bestgo.admanager.bean.User;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import com.bestgo.admanager.utils.MD5Util;

/**
 * Created by jikai on 5/31/17.
 */
@WebServlet(name = "login", urlPatterns = "/login")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("user");
        String password = request.getParameter("pass");

        String sql = "SELECT id,nickname FROM web_ad_login_user WHERE username = '"+MD5Util.digest(username)+"' AND password = '"+MD5Util.digest(password)+"'";
        JSObject one = null;
        try {
            one = DB.findOneBySql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(one != null && one.hasObjectData()){
            User loginUser = new User();
            HttpSession session = request.getSession();
            session.setAttribute("isAdmin", true);
            Long id = one.get("id");
            loginUser.setId(id.intValue());
            loginUser.setNickname(one.get("nickname"));
            loginUser.setUsername(username);
            session.setAttribute("loginUser", loginUser);
            JsonObject json = new JsonObject();
            json.addProperty("ret", 1);
            response.getWriter().write(json.toString());
        } else {
            JsonObject json = new JsonObject();
            json.addProperty("ret", 0);
            response.getWriter().write(json.toString());
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
