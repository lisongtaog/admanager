package com.bestgo.admanager.servlet;

import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.webkit.network.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: mengjun
 * Date: 2018/3/15 10:08
 * Desc: 有关app_image_path表和app_video_path表的操作
 */
@WebServlet(name = "AppImageVideoRel",urlPatterns = "/app_image_video_rel/*")
public class AppImageVideoRel extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;
        String path = request.getPathInfo();
        JsonObject jsonObject = new JsonObject();

        if(path.matches("/query_image_and_video_by_app")){
            JsonArray imageArray = new JsonArray();
            JsonArray videoArray = new JsonArray();
            try {
                String appName = request.getParameter("appName");
                if(appName != null){
                    String sql = "SELECT id,image_path FROM ad_app_image_path_rel WHERE app_name = '" + appName + "' order by id desc";
                    List<JSObject> imagePathList = new ArrayList<>();
                    try {
                        imagePathList = DB.findListBySql(sql);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(imagePathList != null && imagePathList.size() > 0){
                        for(JSObject image : imagePathList){
                            if(image.hasObjectData()){
                                JsonObject jo = new JsonObject();
                                long id = image.get("id");
                                String imagePath = image.get("image_path");
                                jo.addProperty("id",id);
                                jo.addProperty("image_path",imagePath);
                                imageArray.add(jo);
                            }
                        }
                    }

                    sql = "SELECT id,video_path FROM ad_app_video_path_rel WHERE app_name = '" + appName + "' order by id desc";
                    List<JSObject> videoPathList = new ArrayList<>();
                    try {
                        videoPathList = DB.findListBySql(sql);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(videoPathList != null && videoPathList.size() > 0){
                        for(JSObject video : videoPathList){
                            if(video.hasObjectData()){
                                JsonObject jo = new JsonObject();
                                long id = video.get("id");
                                String videoPath = video.get("video_path");
                                jo.addProperty("id",id);
                                jo.addProperty("video_path",videoPath);
                                videoArray.add(jo);
                            }
                        }
                    }
                    jsonObject.add("image_array", imageArray);
                    jsonObject.add("video_array", videoArray);
                    jsonObject.addProperty("ret", 1);
                }
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        } else if(path.matches("/create")){
            try {
                String appName = request.getParameter("appName");
                String imagePath = request.getParameter("imagePath");
                imagePath = imagePath.trim();
                String videoPath = request.getParameter("videoPath");
                videoPath = videoPath.trim();

                if(appName != null){
                    String sql = "";
                    if(imagePath != ""){
                        sql = "SELECT id FROM ad_app_image_path_rel WHERE app_name = '" + appName + "' and image_path = '" + imagePath + "'";
                        JSObject image = DB.findOneBySql(sql);
                        if(!image.hasObjectData()){
                            DB.insert("ad_app_image_path_rel")
                                    .put("app_name",appName)
                                    .put("image_path",imagePath)
                                    .execute();
                        }
                    }

                    if(videoPath != ""){
                        sql = "SELECT id FROM ad_app_video_path_rel WHERE app_name = '" + appName + "' and video_path = '" + videoPath + "'";
                        JSObject video = DB.findOneBySql(sql);
                        if(!video.hasObjectData()){
                            DB.insert("ad_app_video_path_rel")
                                    .put("app_name",appName)
                                    .put("video_path",videoPath)
                                    .execute();
                        }
                    }

                    jsonObject.addProperty("ret", 1);
                }
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }else if(path.matches("/delete")){
            try {
                String id = request.getParameter("id");
                String type = request.getParameter("type");
                String sql = "";
                if("image_path".equals(type)){
                    sql = "DELETE FROM ad_app_image_path_rel WHERE id = " + id;
                    DB.updateBySql(sql);
                }else if("video_path".equals(type)){
                    sql = "DELETE FROM ad_app_video_path_rel WHERE id = " + id;
                    DB.updateBySql(sql);
                }
                jsonObject.addProperty("ret", 1);
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }else if(path.matches("/query_image_path_by_app")){
            JsonArray imageArray = new JsonArray();
            try {
                String appName = request.getParameter("appName");
                String sql = "SELECT image_path FROM ad_app_image_path_rel WHERE app_name = '" + appName + "'";
                List<JSObject> imagePathList = DB.findListBySql(sql);
                if(imagePathList != null && imagePathList.size() > 0){
                    for(JSObject j : imagePathList){
                        if(j.hasObjectData()){
                            String imagePath = j.get("image_path");
                            JsonObject jo = new JsonObject();
                            jo.addProperty("image_path",imagePath);
                            imageArray.add(jo);
                        }
                    }
                    jsonObject.add("image_array", imageArray);
                    jsonObject.addProperty("ret", 1);
                }
            } catch (Exception e) {
                jsonObject.addProperty("ret", 0);
                jsonObject.addProperty("message", e.getMessage());
            }
        }
        response.getWriter().write(jsonObject.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
}
