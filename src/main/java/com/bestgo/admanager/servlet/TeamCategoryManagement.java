package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.DateUtil;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Desc: 对应前端 项目组/应用品类管理 页面；对表 web_ad_category_team 和 web_ad_tag_category 的增删改
 */
@WebServlet(name = "TeamCategoryManagement", urlPatterns = {"/team_category_management/*"})
public class TeamCategoryManagement extends BaseHttpServlet{
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        String path = request.getPathInfo();
        OperationResult result = new OperationResult();
        JsonObject jsonResponse = new JsonObject();

        if(path.startsWith("/add_new_team")){
            String team_name = request.getParameter("team_name");
            try{
                result.result = DB.insert("web_ad_category_team").put("team_name",team_name).execute();
            }catch(Exception e){
                result.message = e.getMessage();
                result.result = false;
            }
            jsonResponse.addProperty("ret",result.result? 1:0);
            jsonResponse.addProperty("message",result.message);
            response.getWriter().write(jsonResponse.toString());
        }else if(path.startsWith("/update_team")){
            String team_name = request.getParameter("team_name");
            String id = request.getParameter("id");
            try{
                result.result = DB.update("web_ad_category_team").put("team_name",team_name)
                        .where(DB.filter().whereEqualTo("id",id)).execute();
            }catch(Exception e){
                result.message = e.getMessage();
                result.result = false;
            }
            jsonResponse.addProperty("ret",result.result? 1:0);
            jsonResponse.addProperty("message",result.message);
            response.getWriter().write(jsonResponse.toString());
        }else if(path.startsWith("/delete_team")){
            String id = request.getParameter("id");
            try{
                result.result = DB.delete("web_ad_category_team").where(DB.filter().whereEqualTo("id",id)).execute();
            }catch(Exception e){
                result.message = e.getMessage();
                result.result = false;
            }
            jsonResponse.addProperty("ret",result.result? 1:0);
            jsonResponse.addProperty("message",result.message);
            response.getWriter().write(jsonResponse.toString());
        }else if(path.startsWith("/add_category")){
            String category = request.getParameter("category");
            String team_id = request.getParameter("team_id");
            try{
                result.result = DB.insert("web_ad_tag_category")
                        .put("category_name",category)
                        .put("team_id",Integer.parseInt(team_id))
                        .execute();
            }catch(Exception e){
                result.message = e.getMessage();
                result.result = false;
            }
            jsonResponse.addProperty("ret",result.result? 1:0);
            jsonResponse.addProperty("message",result.message);
            response.getWriter().write(jsonResponse.toString());
        }else if(path.startsWith("/update_category")){
            String category_id = request.getParameter("category_id");
            String category = request.getParameter("category");
            String team_id = request.getParameter("team_id");
            try{
                result.result = DB.update("web_ad_tag_category")
                        .put("category_name",category)
                        .put("team_id",team_id)
                        .where(DB.filter().whereEqualTo("id",category_id))
                        .execute();
            }catch(Exception e){
                result.message = e.getMessage();
                result.result = false;
            }
            jsonResponse.addProperty("ret",result.result? 1:0);
            jsonResponse.addProperty("message",result.message);
            response.getWriter().write(jsonResponse.toString());
        }else if(path.startsWith("/delete_category")){
            String category_id = request.getParameter("category_id");
            try{
                result.result = DB.delete("web_ad_tag_category").where(DB.filter().whereEqualTo("id",category_id)).execute();
            }catch(Exception e){
                result.message = e.getMessage();
                result.result = false;
            }
            jsonResponse.addProperty("ret",result.result? 1:0);
            jsonResponse.addProperty("message",result.message);
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
