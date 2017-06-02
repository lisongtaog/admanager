package com.bestgo.common.database.services;

import com.bestgo.common.database.utils.JSObject;

import java.util.List;

public abstract class Store {
    public static final int INVALID_TYPE = 0;
    public static final int INT_TYPE = 1;
    public static final int FLOAT_TYPE = 2;
    public static final int BOOL_TYPE = 3;
    public static final int STRING_TYPE = 4;
    public static final int OBJECT_TYPE = 5;
    public static final int ARRAY_TYPE = 6;
    public static final int INVALID_CONNECTOR = 0;
    public static final int AND = 1;
    public static final int OR = 2;
    public static final int WHERE = 3;
    public static final int INVALID_ORDER = 0;
    public static final int ASC = 1;
    public static final int DESC = 2;

    public abstract String func(int paramInt, String paramString);

    public abstract Insert insert(String paramString);

    public abstract Delete delete(String paramString);

    public abstract Update update(String paramString);

    public abstract Scan scan(String paramString);

    public abstract SimpleScan simpleScan(String paramString);

    public static abstract interface Insert {
        public abstract Insert put(String paramString, Object paramObject)
                throws Exception;

        public abstract boolean execute()
                throws Exception;
    }

    public static abstract interface Delete {
        public abstract Delete orderByAsc(String... paramVarArgs);

        public abstract Delete orderByDesc(String... paramVarArgs);

        public abstract Delete limit(int paramInt);

        public abstract Delete where(JSObject paramJSObject);

        public abstract Delete and(JSObject paramJSObject);

        public abstract Delete or(JSObject paramJSObject);

        public abstract boolean execute()
                throws Exception;
    }

    public static abstract interface Update {
        public abstract Update put(String paramString, Object paramObject);

        public abstract Update limit(int paramInt);

        public abstract Update orderByAsc(String... paramVarArgs);

        public abstract Update orderByDesc(String... paramVarArgs);

        public abstract Update where(JSObject paramJSObject);

        public abstract Update and(JSObject paramJSObject);

        public abstract Update or(JSObject paramJSObject);

        public abstract boolean execute()
                throws Exception;
    }

    public static abstract interface Scan {
        public abstract Scan select(String... paramVarArgs);

        public abstract Scan start(int paramInt);

        public abstract Scan limit(int paramInt);

        public abstract Scan where(JSObject paramJSObject);

        public abstract Scan and(JSObject paramJSObject);

        public abstract Scan or(JSObject paramJSObject);

        public abstract Scan orderByAsc(String... paramVarArgs);

        public abstract Scan orderByDesc(String... paramVarArgs);

        public abstract Scan groupBy(String... paramVarArgs);

        public abstract List<JSObject> execute()
                throws Exception;
    }

    public static abstract interface SimpleScan {
        public abstract SimpleScan select(String... paramVarArgs);

        public abstract SimpleScan where(JSObject paramJSObject);

        public abstract SimpleScan and(JSObject paramJSObject);

        public abstract SimpleScan or(JSObject paramJSObject);

        public abstract SimpleScan orderByAsc(String... paramVarArgs);

        public abstract SimpleScan orderByDesc(String... paramVarArgs);

        public abstract SimpleScan groupBy(String... paramVarArgs);

        public abstract JSObject execute()
                throws Exception;
    }
}