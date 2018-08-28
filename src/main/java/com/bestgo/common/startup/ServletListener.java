package com.bestgo.common.startup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import org.apache.log4j.PropertyConfigurator;

import com.bestgo.common.database.MySqlHelper;

@WebListener()
@WebServlet(name = "startup", loadOnStartup = 0)
public class ServletListener implements ServletContextListener {
	
	public void contextDestroyed(ServletContextEvent arg0) {
		MySqlHelper.uninit();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext context = arg0.getServletContext();
		initLog4J(context);
		MySqlHelper.init();
	}

	private void initLog4J(ServletContext context){
		String webappPath = context.getRealPath("/");
		String log4jProp = webappPath + "/WEB-INF/log4j.properties";
        PropertyConfigurator.configure(log4jProp);//装入log4j配置信息 
	}
}