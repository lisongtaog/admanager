package com.bestgo.admanager.servlet;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by jikai on 5/31/17.
 */
@WebServlet(name = "login", urlPatterns = "/login")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");

        //暂时写死，抽时间加密保存
        if(
                ("xiong".equals(user) && "5251234".equals(pass)) || ("qiuflora".equals(user) && "qiuflora123".equals(pass)) ||
                ("lijiao".equals(user) && "lijiao123".equals(pass)) || ("xiaofan".equals(user) && "xiaofan6821763".equals(pass)) ||
                ("zmj".equals(user) && "zmj123".equals(pass)) || ("bsjg123".equals(user) && "bsjgzxp123".equals(pass)) ||
                ("bestgo".equals(user) && "bestgo123".equals(pass)) || ("meizhenshi".equals(user) && "shimeizhen2018".equals(pass))
           ){

            HttpSession session = request.getSession();
            session.setAttribute("isAdmin", true);
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
