package com.bestgo.common.database;

import com.bestgo.common.database.services.DB;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

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
}
