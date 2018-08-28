package com.bestgo.common.database.dao;

import com.bestgo.common.database.utils.JSObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DaoProxy implements IDao {
    private DatabaseConnection dbc = null;
    private IDao dao = null;

    public DaoProxy() {
        this.dbc = new DatabaseConnection();
        this.dao = new DaoImpl(this.dbc.getConnection());
    }

    public List<JSObject> findModeResult(String con, Object... params) {
        ArrayList<JSObject> list = new ArrayList();
        try {
            return this.dao.findModeResult(con, params);
        } catch (Exception e) {
            Logger logger = Logger.getRootLogger();
            logger.error(con);
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                this.dbc.releaseConn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public JSObject findSimpleResult(String con, Object... params) {
        JSObject map = new JSObject();
        try {
            return this.dao.findSimpleResult(con, params);
        } catch (Exception e) {
            Logger logger = Logger.getRootLogger();
            logger.error(con);
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                this.dbc.releaseConn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }


    public boolean updateByPreparedStatement(String con, Object... params) {
        boolean flag = false;
        try {
            return this.dao.updateByPreparedStatement(con, params);
        } catch (Exception e) {
            Logger logger = Logger.getRootLogger();
            logger.error(con);
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                this.dbc.releaseConn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    @Override
    public long insertByPreparedStatement(String con, Object... params) throws Exception {
        try {
            return this.dao.insertByPreparedStatement(con, params);
        } catch (Exception e) {
            Logger logger = Logger.getRootLogger();
            logger.error(con);
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                this.dbc.releaseConn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}