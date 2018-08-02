package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;
import sun.security.tools.PathList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "UpdateAppImageVideoPathRel", urlPatterns = "/update_app_material_path_rel")
public class UpdateAppImageVideoPathRel extends BaseHttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appName = request.getParameter("app_name");
        JsonObject json = new JsonObject();
        int err = 0;
        String message = "APP广告素材路径刷新成功";
        String sql = "SELECT config_key,config_value from web_system_config where config_key in ('fb_image_path','fb_video_path')";
        try {
            List<JSObject> configList = DB.findListBySql(sql);
            if (configList.size() > 0) {
                for (JSObject config : configList) {
                    String configKey = config.get("config_key");
                    String rootPath = config.get("config_value"); // rootPath 里是表格里存的根路径
//                        rootPath = rootPath.replace("/", File.separator);
//                        rootPath = rootPath.replace("\\",File.separator);
                    String appParentPath = rootPath + File.separatorChar + appName;
                    File file = new File(appParentPath);
                    List<String> PathList = Utils.ergodicImageDirectory(file, new ArrayList<>(), false);

                    //更新这个应用的图片路径
                    if ("fb_image_path".equals(configKey)) {
                        if (PathList.size() > 0) {
                            Set<String> set = new HashSet<>();
                            DB.delete("ad_app_image_path_rel").where(DB.filter().whereEqualTo("app_name", appName)).execute();
                            for (String imagePath : PathList) {
                                String imageRelativePath = imagePath.replace(rootPath + File.separatorChar, "");

                                try {

//                                  int index = imageRelativePath.lastIndexOf("\\");

                                    int index = imageRelativePath.lastIndexOf("/");
                                    imageRelativePath = imageRelativePath.substring(0, index);

                                    int oldSize = set.size();
                                    set.add(imageRelativePath);
                                    int newSize = set.size();
                                    if (newSize > oldSize) {
                                        DB.insert("ad_app_image_path_rel")
                                                .put("app_name", appName)
                                                .put("image_path", imageRelativePath)
                                                .execute();
                                    }
                                } catch (Exception e) {
                                    message = e.getMessage();
                                    err++;
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        //更新这个应用的视频路径
                        if (PathList.size() > 0) {
                            Set<String> set = new HashSet<>();
                            DB.delete("ad_app_video_path_rel").where(DB.filter().whereEqualTo("app_name", appName)).execute();
                            for (String videoPath : PathList) {
                                String videoRelativePath = videoPath.replace(rootPath + File.separatorChar, "");
                                try {
//                                    int index = videoRelativePath.lastIndexOf("\\");
                                    int index = videoRelativePath.lastIndexOf("/");

                                    videoRelativePath = videoRelativePath.substring(0, index);

                                    int oldSize = set.size();
                                    set.add(videoRelativePath);
                                    int newSize = set.size();
                                    if (newSize > oldSize) {
                                        DB.insert("ad_app_video_path_rel")
                                                .put("app_name", appName)
                                                .put("video_path", videoRelativePath)
                                                .execute();
                                    }

                                } catch (Exception e) {
                                    message = e.getMessage();
                                    err++;
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            message = e.getMessage();
            err = 0;
            e.printStackTrace();
        }

        json.addProperty("message", message);
        json.addProperty("err", err);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

}

