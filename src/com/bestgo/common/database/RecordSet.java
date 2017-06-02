package com.bestgo.common.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecordSet {
	private Connection _conn;
	private Statement _stmt;
	private ResultSet _rs;
	
	public RecordSet() {
		
	}
	
	public RecordSet(Connection conn, Statement stmt, ResultSet rs) {
		_conn = conn;
		_stmt = stmt;
		_rs = rs;
	}
	
	public void setConnection(Connection conn) {
		_conn = conn;
	}
	
	public void setStatement(Statement stmt) {
		_stmt = stmt;
	}
	
	public void setResultSet(ResultSet rs){
		_rs = rs;
	}
	
	public ResultSet getResultSet() {
		return _rs;
	}
	
	public void close() {
		if (_rs != null) {
			try {
				_rs.close();
			} catch (SQLException e) {
			}
		}
		if (_stmt != null) {
			try {
				_stmt.close();
			} catch (SQLException e) {
			}
		}
		if (_conn != null) {
			try {
				_conn.close();
			} catch (SQLException e) {
			}
		}
	}
}
