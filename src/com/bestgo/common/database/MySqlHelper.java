package com.bestgo.common.database;

import com.bestgo.common.database.services.DB;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class MySqlHelper {
	private static DataSource ds = null;
	
	public static void uninit() {
	    try {
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        while(drivers.hasMoreElements()) {
	            DriverManager.deregisterDriver(drivers.nextElement());
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
		ds = null;
	}
	
	public static void init() {
		if (ds == null) {
			Context ctx = null;
			Object obj = null;
			try {
				ctx = new InitialContext();
				obj = ctx.lookup("java:comp/env/jdbc/mysql");
			} catch (NamingException e1) {
				e1.printStackTrace();
			}
			
			ds = (DataSource) obj;

			DB.init();
		}
	}
	
	public Connection getConnection() {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}

	/**
	 * 批量持久化 到数据库
	 * @param sqlList
	 * @return
	 */
	public boolean excuteBatch2DB(List<String> sqlList){
		boolean rtn = true;
		Connection conn = getConnection();
		Statement stmt = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			int index = 0;
			for (String sql : sqlList){
				index ++;
				stmt.addBatch(sql);
				if (index % 1000 == 0){//1000条一次提交
					stmt.executeBatch();
					conn.commit();
				}
			}
			stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		}catch (Exception e){
			rtn = false;
			e.printStackTrace();
		}
		return rtn;
	}

}
