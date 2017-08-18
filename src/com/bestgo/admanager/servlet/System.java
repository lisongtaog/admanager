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
import java.util.List;

/**
 * Created by jikai on 8/16/17.
 */
@WebServlet(name = "System", urlPatterns = "/system/*")
public class System extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Utils.isAdmin(request, response)) return;

        String path = request.getPathInfo();
        JsonObject json = new JsonObject();

        if (path != null) {
            if (path.startsWith("/update")) {
                String key = request.getParameter("configKey");
                String value = request.getParameter("configValue");
                OperationResult result = updateConfig(key, value);
                json.addProperty("ret", result.result ? 1 : 0);
                json.addProperty("message", result.message);
            }
        } else {

        }

        response.getWriter().write(json.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private OperationResult updateConfig(String key, String value) {
        OperationResult ret = new OperationResult();

        try {
            DB.update("web_system_config")
                    .put("config_value", value)
                    .where(DB.filter().whereEqualTo("config_key", key)).execute();

            ret.result = true;
            ret.message = "修改成功";
        } catch (Exception e) {
            ret.result = false;
            ret.message = e.getMessage();
            Logger logger = Logger.getRootLogger();
            logger.error(e.getMessage(), e);
        }

        return ret;
    }
}
