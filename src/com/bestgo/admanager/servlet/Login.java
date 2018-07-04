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
@WebServlet(name = "login", urlPatterns = "/login/*")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        JsonObject json = new JsonObject();
        if(path != null){
            if(path.startsWith("/login")){
                String username = request.getParameter("user");
                String password = request.getParameter("pass");

                String sql = "SELECT id,nickname,user_type FROM web_ad_login_user WHERE username = '"+MD5Util.digest(username)+"' AND password = " +
                        "'"+MD5Util.digest(password)+"'";
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
                    loginUser.setUserType(one.get("user_type"));
                    loginUser.setNickname(one.get("nickname"));
                    loginUser.setUsername(username);
                    session.setAttribute("loginUser", loginUser);

                    json.addProperty("ret", 1);
                    response.getWriter().write(json.toString());
                } else {

                    json.addProperty("ret", 0);
                    response.getWriter().write(json.toString());
                }
            }else if(path.startsWith("/register")){
                String user_name = request.getParameter("user_name");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String sql = "SELECT user_name FROM new_user_application WHERE user_name = '" + user_name + "'";
                try{
                    JSObject user = DB.findOneBySql(sql);
                    if(user.hasObjectData()){
                        json.addProperty("ret", 0);
                        json.addProperty("message","用户名被占用");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(json.toString());
                    }else{
                        boolean check = DB.insert("new_user_application")
                                .put("user_name",user_name)
                                .put("password",password)
                                .put("email",email)
                                .execute();
                        if(check){
                            json.addProperty("ret", 1);
                            json.addProperty("message","新用户 "+user_name+" 等待管理员审核");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(json.toString());
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    json.addProperty("ret",0);
                    json.addProperty("message",e.getMessage());
                    response.getWriter().write(json.toString());
                }
            }else if(path.startsWith("/applicationCheck")){
                try{
                    JSObject count = DB.simpleScan("new_user_application").select(DB.func(DB.COUNT, "id")).execute();
                    Long count_id = count.get("count(id)");
                    json.addProperty("count",count_id);
                    response.getWriter().write(json.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    json.addProperty("count",0);
                    json.addProperty("message",e.getMessage());
                    response.getWriter().write(json.toString());
                }
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
