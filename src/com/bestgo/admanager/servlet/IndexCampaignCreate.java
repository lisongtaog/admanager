package com.bestgo.admanager.servlet;

import com.bestgo.common.database.services.DB;
import com.bestgo.common.database.utils.JSObject;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.regex.Pattern;

/**
 * Author: Yunxi
 * Date: 2018-03-14
 * Description:
 * @parameter campaign_id 是从index2 页面传来的 系列ID，以此来判断从facebook的表还是adwords的表取数据
 * 取完的数据返回js文件，用于往jsp页面上的表单填充
 */
@WebServlet(name = "IndexCampaignCreate", urlPatterns = "/IndexCampaignCreate")
public class IndexCampaignCreate extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String CampaignId = request.getParameter("campaign_id");
        Pattern p1 = Pattern.compile("^\\d{11,17}$");  //将正则表达式编译并赋给 Pattern 类
        Pattern p2 = Pattern.compile("^\\d{9,10}$");
        String facebook = p1.pattern(); //用pattern()方法把正则表达式返回为字符串
        String admob = p2.pattern();
        String sql = "";
        if(Pattern.matches(facebook,CampaignId)){
            sql = "select app_name,account_id,country_region,language,campaign_name,age,gender,id"+
                    " from ad_campaigns where campaign_id ='"+CampaignId+"'";
            JSObject fb = null;
            try{
                fb = DB.findOneBySql(sql);
            }catch(Exception e){}
            JsonObject facebook_campaign = new JsonObject();
            String app_name = fb.get("app_name");
            String account_id = fb.get("account_id");
            String country_region = fb.get("country_region");
            String campaign_name = fb.get("campaign_name");
            String age = fb.get("age");
            String gender = fb.get("gender");
            long parent_id = fb.get("id");
            sql = "select image_file_path,video_file_path from ad_ads where parent_id = "+ parent_id;
            try{
                fb = DB.findOneBySql(sql);
            }catch(Exception e){}
            String image_path = fb.get("image_file_path");
            String vedio_path = fb.get("video_file_path");
            if(fb.hasObjectData()){
                facebook_campaign.addProperty("app_name",app_name);
                facebook_campaign.addProperty("account_id",account_id);
                facebook_campaign.addProperty("country_region",country_region);
                facebook_campaign.addProperty("campaign_name",campaign_name);
                facebook_campaign.addProperty("age",age);
                facebook_campaign.addProperty("gender",gender);
                facebook_campaign.addProperty("image_path",image_path);
                facebook_campaign.addProperty("video_path",vedio_path);
                facebook_campaign.addProperty("flag","facebook");
            }else{
                facebook_campaign.addProperty("no_data","no_data");
            }

            response.setCharacterEncoding("UTF-8"); //这一行： 设置response里的编码格式
            response.getWriter().write(facebook_campaign.toString());
        }else if(Pattern.matches(admob,CampaignId)){
            sql = "select app_name,account_id,campaign_name,country_region,excluded_region,image_path"+
                    " from ad_campaigns_admob where campaign_id ='"+CampaignId+"'";
            JSObject ad = null;
            try{
                ad = DB.findOneBySql(sql);
            }catch (Exception e){}
            JsonObject admob_campaign = new JsonObject();
            String app_name = ad.get("app_name");
            String account_id = ad.get("account_id");
            String campaign_name = ad.get("campaign_name");
            String country_region = ad.get("country_region");  //这里拿到的是如'BR'一样的国家代码，有时会有多个：“BR,AR,US”
            String excluded_region = ad.get("excluded_region");
            String image_path = ad.get("image_path");
            if(ad.hasObjectData()){
                admob_campaign.addProperty("app_name",app_name);
                admob_campaign.addProperty("account_id",account_id);
                admob_campaign.addProperty("campaign_name",campaign_name);
                admob_campaign.addProperty("country_region",country_region);
                admob_campaign.addProperty("excluded_region",excluded_region);
                admob_campaign.addProperty("image_path",image_path);
                admob_campaign.addProperty("flag","admob");
            }else{
                admob_campaign.addProperty("no_data","no_data");
            }
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(admob_campaign.toString());
        }
    }
}