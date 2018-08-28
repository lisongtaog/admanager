package com.bestgo.common.database.dao;

import com.bestgo.common.database.utils.JSObject;

import java.util.List;

public abstract interface IDao {
    public abstract List<JSObject> findModeResult(String paramString, Object... paramVarArgs)
            throws Exception;

    public abstract JSObject findSimpleResult(String paramString, Object... paramVarArgs)
            throws Exception;

    public abstract boolean updateByPreparedStatement(String paramString, Object... paramVarArgs)
            throws Exception;

    public abstract long insertByPreparedStatement(String paramString, Object... paramVarArgs)
            throws Exception;
}