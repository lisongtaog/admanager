package com.bestgo.admanager.servlet;

import com.bestgo.admanager.OperationResult;
import com.bestgo.admanager.utils.Utils;
import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdvertAdMob2", urlPatterns = {"/advert_admob2/*"})
public class AdvertAdmob2 extends BaseHttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path.startsWith("/save_advert_admob")) {
            OperationResult result = new OperationResult();   //OperationResult 是有两个实例的封装类
            result.result = true;

            String appName = request.getParameter("appName");
            String language = request.getParameter("language");
            String groupNumber = request.getParameter("groupNumber");
            String saveVersion = request.getParameter("version");
            String[] messageArr = new String[4];
            messageArr[0] = request.getParameter("message1");
            messageArr[1] = request.getParameter("message2");
            messageArr[2] = request.getParameter("message3");
            messageArr[3] = request.getParameter("message4");
            try{
                if(saveVersion.equals("English")){   //这里如果没有传入saveVersion的值会出现空指针错误
                    language = "English";
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            for(int i=0;i<4;i++){
                String m1 = messageArr[i];
                if(m1 != null){
                    if("Chinese".equals(language) || "Japanese".equals(language) || "Korean".equals(language)){
                        String[] sub = m1.split("");
                        int num = 0;
                        //计算字符数
                        for(String s : sub){
                            if(" ".equals(s) || ".".equals(s) || "!".equals(s) || ",".equals(s)){
                                num++;
                            }else{
                                num +=2;
                            }
                        }
                        if(num > 25){
                            result.message = "组合【"+groupNumber+"】的广告语"+(i+1)+"中不能超过25个字符！";
                            result.result = false;
                            break;
                        }
                    }else  if(m1.getBytes("iso8859-1").length > 25) {
                        result.message = "组合【" + groupNumber + "】的广告语" + (i + 1) + "中不能超过25个字符！";
                        result.result = false;
                        break;
                    }
                }

            }

            if(result.result){
                JSObject item = new JSObject();
                String sql = "select group_id,message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name='"
                              + appName + "' and language = '" + language + "'and group_id='"+ groupNumber +"'";
                item = fetchOneData(sql);

                try {
                    //插入组合里的4条广告语
                    if(item.hasObjectData()){
                        DB.update("web_ad_descript_dict_admob")
                                .put("message1", messageArr[0])
                                .put("message2", messageArr[1])
                                .put("message3", messageArr[2])
                                .put("message4", messageArr[3])
                                .where(DB.filter().whereEqualTo("app_name", appName))
                                .and(DB.filter().whereEqualTo("language", language))
                                .and(DB.filter().whereEqualTo("group_id", groupNumber))
                                .execute();
                        json.addProperty("existData","true");
                    }else{
                        DB.insert("web_ad_descript_dict_admob")
                                .put("language", language)
                                .put("message1", messageArr[0])
                                .put("message2", messageArr[1])
                                .put("message3", messageArr[2])
                                .put("message4", messageArr[3])
                                .put("group_id", groupNumber)
                                .put("app_name", appName)
                                .execute();
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
            String groupNumberAdmob = request.getParameter("groupNumberAdmob");
            JSObject item_translation = new JSObject();
            JSObject item_english = new JSObject();
            try {
                if(appNameAdmob != null && languageAdmob != null){
                    String sql = "select message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name = '"
                                 + appNameAdmob + "' and language = '" + languageAdmob + "'and group_id='"+groupNumberAdmob+"'";
                    item_translation = DB.findOneBySql(sql);
                    sql = "select message1,message2,message3,message4 from web_ad_descript_dict_admob where app_name = '"
                            + appNameAdmob + "' and language = 'English'and group_id='"+groupNumberAdmob+"'";
                    item_english  = DB.findOneBySql(sql);
                    if(item_translation.hasObjectData() || item_english.hasObjectData()){
                        String[] message = new String[8];
                        if(item_translation.hasObjectData()){
                            for(int i=1;i<=4;i++){
                                message[i-1]= item_translation.get("message"+i);
                            }
                        }else{
                            for(int i=1;i<=4;i++){
                                message[i-1]= "";
                            }
                        }
                        if(item_english.hasObjectData()){
                            for(int i=1;i<=4;i++){
                                message[i+3]= item_english.get("message"+i);
                            }
                        }else{
                            for(int i=1;i<=4;i++){
                                message[i+3]= "";
                            }
                        }

                        json.addProperty("message1", message[0]);
                        json.addProperty("message2",message[1]);
                        json.addProperty("message3", message[2]);
                        json.addProperty("message4",message[3]);
                        json.addProperty("message1_en", message[4]);
                        json.addProperty("message2_en",message[5]);
                        json.addProperty("message3_en", message[6]);
                        json.addProperty("message4_en",message[7]);
                        json.addProperty("ret", 1);
                    }else{
                        json.addProperty("ret",0);
                    }
                }else{
                    json.addProperty("ret",0);
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

    public static JSObject fetchOneData(String sql) {
        JSObject item = new JSObject();
        try {
            return DB.findOneBySql(sql);
        } catch (Exception ex) {
            Logger logger = Logger.getRootLogger();
            logger.error(ex.getMessage(), ex);
        }
        return item;
    }
}
