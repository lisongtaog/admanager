package com.bestgo.common.database.dao;

import com.bestgo.common.database.MySqlHelper;

import java.sql.Connection;

public class DatabaseConnection {
    private Connection connection;
    private MySqlHelper helper;

    public DatabaseConnection() {
        helper = new MySqlHelper();
    }

    public Connection getConnection() {
        try {
            this.connection = helper.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.connection;
    }


    public void releaseConn()
            throws Exception {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }
}
