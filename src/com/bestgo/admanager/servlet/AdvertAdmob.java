package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "AdvertAdMob", urlPatterns = {"/advert_admob/*"})
public class AdvertAdmob extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/save_advert_admob")) {
            OperationResult result = new OperationResult();
            result.result = true;
            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            String[] message1Arr = new String[4];
            String[] message2Arr = new String[4];
            String[] message3Arr = new String[4];
            String[] message4Arr = new String[4];
            message1Arr[0] = request.getParameter("message11");
            message2Arr[0] = request.getParameter("message12");
            message3Arr[0] = request.getParameter("message13");
            message4Arr[0] = request.getParameter("message14");
            message1Arr[1] = request.getParameter("message21");
            message2Arr[1] = request.getParameter("message22");
            message3Arr[1] = request.getParameter("message23");
            message4Arr[1] = request.getParameter("message24");
            message1Arr[2] = request.getParameter("message31");
            message2Arr[2] = request.getParameter("message32");
            message3Arr[2] = request.getParameter("message33");
            message4Arr[2] = request.getParameter("message34");
            message1Arr[3] = request.getParameter("message41");
            message2Arr[3] = request.getParameter("message42");
            message3Arr[3] = request.getParameter("message43");
            message4Arr[3] = request.getParameter("message44");

            for(int i=0;i<4;i++){
                String m1 = message1Arr[i];
                if(m1 != null){
                   if("Chinese".equals(language) || "Japanese".equals(language) || "Korean".equals(language)){
                       String[] sub = m1.split("");
                       int num = 0;
                       for(String s : sub){
                           if(" ".equals(s) || ".".equals(s) || "!".equals(s) || ",".equals(s)){
                               num++;
                           }else{
                               num +=2;
                           }
                       }
                        if(num > 25){
                            result.message = "组合【"+(i+1)+"】的广告语1中不能超过25个字符！";
                            result.result = false;
                            break;
                        }
                    }else  if(m1.getBytes("iso8859-1").length > 25){
                       result.message = "组合【"+(i+1)+"】的广告语1中不能超过25个字符！";
                       result.result = false;
                       break;
                   }
                }

            }
            if(result.result){
                for(int i = 0;i<4;i++){
                    String m2 = message2Arr[i];
                    if(m2 != null){
                       if("Chinese".equals(language) || "Japanese".equals(language) || "Korean".equals(language)){
                           String[] sub = m2.split("");
                           int num = 0;
                           for(String s : sub){
                               if(" ".equals(s) || ".".equals(s) || "!".equals(s) || ",".equals(s)){
                                   num++;
                               }else{
                                   num +=2;
                               }
                           }
                           if(num > 25){
                               result.message = "组合【"+(i+1)+"】的广告语2中不能超过25个字符！";
                               result.result = false;
                               break;
                           }
                        }else if(m2.getBytes("iso8859-1").length > 25){
                           result.message = "组合【"+(i+1)+"】的广告语2中不能超过25个字符！";
                           result.result = false;
                           break;
                       }
                    }

                }
            }
            if(result.result){
                for(int i=0;i<4;i++){
                    String m3 = message3Arr[i];
                    if(m3 != null){
                        if("Chinese".equals(language) || "Japanese".equals(language) || "Korean".equals(language)){
                            String[] sub = m3.split("");
                            int num = 0;
                            for(String s : sub){
                                if(" ".equals(s) || ".".equals(s) || "!".equals(s) || ",".equals(s)){
                                    num++;
                                }else{
                                    num +=2;
                                }
                            }
                            if(num > 25){
                                result.message = "组合【"+(i+1)+"】的广告语3中不能超过25个字符！";
                                result.result = false;
                                break;
                            }
                        } else if(m3.getBytes("iso8859-1").length > 25){
                            result.message = "组合【"+(i+1)+"】的广告语3中不能超过25个字符！";
                            result.result = false;
                            break;
                        }
                    }

                }
            }
            if(result.result){
                for(int i=0;i<4;i++){
                    String m4 = message4Arr[i];
                    if(m4 != null){
                        if("Chinese".equals(language) || "Japanese".equals(language) || "Korean".equals(language)){
                            String[] sub = m4.split("");
                            int num = 0;
                            for(String s : sub){
                                if(" ".equals(s) || ".".equals(s) || "!".equals(s) || ",".equals(s)){
                                    num++;
                                }else{
                                    num +=2;
                                }
                            }
                            if(num > 25){
                                result.message = "组合【"+(i+1)+"】的广告语4中不能超过25个字符！";
                                result.result = false;
                                break;
                            }
                        }else if(m4.getBytes("iso8859-1").length > 25){
                            result.message = "组合【"+(i+1)+"】的广告语4中不能超过25个字符！";
                            result.result = false;
                            break;
                        }
                    }

                }
            }
            if(result.result){
                List<JSObject> list = new ArrayList<>();
                String sql = "select group_id,message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name='" + appName + "' and language = '" + language + "'";
                list = fetchData(sql);

                try {
                    if(list != null && list.size() > 0){
                        HashSet<Integer> set = new HashSet<>();
                        for(JSObject j : list){
                            Integer i = j.get("group_id");
                            set.add(i);
                        }
                        for(int i=1;i<=4;i++){
                            if(set.contains(i)){
                                DB.update("web_ad_descript_dict_admob")
                                        .put("message1", message1Arr[i-1])
                                        .put("message2", message2Arr[i-1])
                                        .put("message3", message3Arr[i-1])
                                        .put("message4", message4Arr[i-1])
                                        .where(DB.filter().whereEqualTo("app_name", appName))
                                        .and(DB.filter().whereEqualTo("language", language))
                                        .and(DB.filter().whereEqualTo("group_id", i))
                                        .execute();
                            }else{
                                DB.insert("web_ad_descript_dict_admob")
                                        .put("language", language)
                                        .put("message1", message1Arr[i-1])
                                        .put("message2", message2Arr[i-1])
                                        .put("message3", message3Arr[i-1])
                                        .put("message4", message4Arr[i-1])
                                        .put("group_id", i)
                                        .put("app_name", appName)
                                        .execute();
                            }
                        }
                        json.addProperty("existData","true");
                    }else{
                        for(int i=1;i<=4;i++){
                            DB.insert("web_ad_descript_dict_admob")
                                    .put("language", language)
                                    .put("message1", message1Arr[i-1])
                                    .put("message2", message2Arr[i-1])
                                    .put("message3", message3Arr[i-1])
                                    .put("message4", message4Arr[i-1])
                                    .put("group_id", i)
                                    .put("app_name", appName)
                                    .execute();
                        }
                        json.addProperty("existData","false");
                    }
                    result.result = true;
                } catch (Exception ex) {
                    result.message = ex.getMessage();
                    result.result = false;
                }
            }

            json.addProperty("ret", result.result ? 1 : 0);
            json.addProperty("message", result.message);
        } else if (path.startsWith("/query_before_admob_insert")) {
            String appNameAdmob = request.getParameter("appNameAdmob");
            String languageAdmob = request.getParameter("languageAdmob");
            List<JSObject> list =null;
            try {
                if(appNameAdmob != null && languageAdmob != null){
                    String sql = "select group_id,message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name = '" + appNameAdmob + "' and language = '" + languageAdmob + "'";
                    list = DB.findListBySql(sql);
                    if(list != null && list.size()>0){
                        JsonArray array = new JsonArray();
                        for(JSObject two: list){
                            JsonObject j = new JsonObject();
                            String message1 = two.get("message1");
                            String message2 = two.get("message2");
                            String message3 = two.get("message3");
                            String message4 = two.get("message4");
                            int groupId = two.get("group_id");
                            j.addProperty("message1", message1);
                            j.addProperty("message2", message2);
                            j.addProperty("message3", message3);
                            j.addProperty("message4", message4);
                            j.addProperty("group_id", groupId);
                            array.add(j);
                        }
                        json.add("array",array);
                        json.addProperty("ret", 1);
                    }
                }

            } catch (Exception e) {
                json.addProperty("ret", 0);
                e.printStackTrace();
            }
        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static List<JSObject> fetchData(String sql) {
        List<JSObject> list = new ArrayList<>();
        try {
            return DB.findListBySql(sql);
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return list;
    }
}
