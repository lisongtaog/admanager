package com.bestgo.admanager.servlet;

import com.bestgo.admanager.utils.LoginUserSessionCacheUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseHttpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        LoginUserSessionCacheUtil.loadSessionFromCache(getServletContext(), req.getSession());
    }
}
