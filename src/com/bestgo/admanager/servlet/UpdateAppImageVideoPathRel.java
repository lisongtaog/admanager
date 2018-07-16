package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;

import javax.rmi.CORBA.Util;
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

@WebServlet(name="UpdateAppImageVideoPathRel",urlPatterns = "/update_app_material_path_rel")
public class UpdateAppImageVideoPathRel extends HttpServlet {
    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
        doPost(request,response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appName = request.getParameter("app_name");
        JsonObject json = new JsonObject();
        int err =0;
        String message = "APP广告素材路径刷新成功";
        String sql = "SELECT config_key,config_value from web_system_config where config_key in ('fb_image_path','fb_video_path')";
        try {
            List<JSObject> configList = DB.findListBySql(sql);
            if (configList.size()>0){
                for (JSObject config: configList){
                    String configKey = config.get("config_key");
                    if ("fb_image_path".equals(configKey)){
                        String ParentPath = config.get("config_value"); // ParentPath 里是表格里存的根路径
                        ParentPath = ParentPath.replace("/",File.separator);
                        ParentPath = ParentPath.replace("\\",File.separator);
                        String appParentPath = ParentPath + File.separatorChar + appName;
                        File file = new File(appParentPath);
                        List<String> PathList = Utils.ergodicImageDirectory(file, new ArrayList<>(), false,true);

                        if(PathList.size() > 0){
                            DB.delete("ad_app_image_path_rel").where(DB.filter().whereEqualTo("app_name", appName)).execute();
                            for(String imagePath : PathList){
                                String imageRelativePath = imagePath.replace(ParentPath + File.separatorChar,"");
                                try{
                                    DB.insert("ad_app_image_path_rel")
                                            .put("app_name",appName)
                                            .put("image_path",imageRelativePath)
                                            .execute();
                                }catch(Exception e){
                                    message = e.getMessage();
                                    err++;
                                    e.printStackTrace();
                                }
                            }
                        }
                    }else{
                        // "fb_video_path"的情况
                        String ParentPath = config.get("config_value");
                        File file = new File(ParentPath);
                        List<String> PathList = Utils.ergodicDirectory(file, new ArrayList<>(), false,true);
                        if(PathList.size() > 0){
                            DB.delete("ad_app_video_path_rel").where(DB.filter().whereEqualTo("app_name", appName)).execute();
                            //截取，判视频，去重
                            HashSet<String> set = new HashSet<>();
                            for(String videoPath : PathList){
                                videoPath = videoPath.replace(ParentPath + "/","");
                                int i = videoPath.lastIndexOf("/");
                                if (i > 0) {
                                    videoPath =  videoPath.substring(0,videoPath.lastIndexOf("/"));
                                    set.add(videoPath);
                                }
                            }
                            for(String path:set){
                                try{
                                    if(path.startsWith(appName)){
                                        boolean execute = DB.insert("ad_app_video_path_rel")
                                                .put("app_name", appName)
                                                .put("video_path", path)
                                                .execute();
                                        if(!execute){
                                            err++;
                                        }
                                    }
                                }catch(Exception e){
                                    message = e.getMessage();
                                    err++;
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }catch(Exception e){
            message = e.getMessage();
            err = 0;
            e.printStackTrace();
        }

        json.addProperty("message",message);
        json.addProperty("err",err);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }

}

