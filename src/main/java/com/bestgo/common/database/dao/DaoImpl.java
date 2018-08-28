package com.bestgo.common.database.dao;

import com.bestgo.common.database.utils.JSObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DaoImpl
        implements IDao {
    private Connection connection = null;
    private PreparedStatement pstmt;
    private ResultSet resultSet;

    public DaoImpl(Connection con) {
        this.connection = con;
    }

    public List<JSObject> findModeResult(String con, Object... params)
            throws Exception {
        List<JSObject> list = new ArrayList();
        int index = 1;
        this.pstmt = this.connection.prepareStatement(con);
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; i++) {
                this.pstmt.setObject(index++, params[i]);
            }
        }
        this.resultSet = this.pstmt.executeQuery();
        ResultSetMetaData metaData = this.resultSet.getMetaData();
        int cols_len = metaData.getColumnCount();
        while (this.resultSet.next()) {
            JSObject map = new JSObject();
            for (int i = 0; i < cols_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = this.resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
            list.add(map);
        }
        this.resultSet.close();

        return list;
    }

    public JSObject findSimpleResult(String con, Object... params)
            throws Exception {
        JSObject map = new JSObject();
        int index = 1;
        this.pstmt = this.connection.prepareStatement(con);
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; i++) {
                this.pstmt.setObject(index++, params[i]);
            }
        }
        this.resultSet = this.pstmt.executeQuery();
        ResultSetMetaData metaData = this.resultSet.getMetaData();
        int col_len = metaData.getColumnCount();
        while (this.resultSet.next()) {
            for (int i = 0; i < col_len; i++) {
                String cols_name = metaData.getColumnName(i + 1);
                Object cols_value = this.resultSet.getObject(cols_name);
                if (cols_value == null) {
                    cols_value = "";
                }
                map.put(cols_name, cols_value);
            }
        }
        this.resultSet.close();
        return map;
    }

    public boolean updateByPreparedStatement(String con, Object... params)
            throws Exception {
        boolean flag = false;
        int result = -1;
        this.pstmt = this.connection.prepareStatement(con);
        int index = 1;
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; i++) {
                this.pstmt.setObject(index++, params[i]);
            }
        }
        result = this.pstmt.executeUpdate();
        flag = result > 0;
        return flag;
    }

    @Override
    public long insertByPreparedStatement(String con, Object... params) throws Exception {
        this.pstmt = this.connection.prepareStatement(con, Statement.RETURN_GENERATED_KEYS);
        int index = 1;
        if ((params != null) && (params.length > 0)) {
            for (int i = 0; i < params.length; i++) {
                this.pstmt.setObject(index++, params[i]);
            }
        }
        int result = this.pstmt.executeUpdate();
        ResultSet rs = this.pstmt.getGeneratedKeys();
        if (rs.next()) {
            return rs.getLong(1);
        }
        return -1;
    }
}