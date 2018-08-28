package com.bestgo.common.database.utils;


public class Filter {
    public static final int INVALID = 0;

    public static final int EQUAL = 1;
    public static final int NOT_EQUAL = 2;
    public static final int GREATER = 3;
    public static final int GREATER_OR_EQUAL = 4;
    public static final int LESS = 5;
    public static final int LESS_OR_EQUAL = 6;
    public static final int LIKE = 7;
    public static final String KEY = "key";
    public static final String OPERATOR = "operator";
    public static final String VALUE = "value";
    private JSObject filter = new JSObject();


    private void checkType(Object o) {
        if (o == null)
            throw new IllegalArgumentException("value is null");
        if ((o instanceof Integer)) {
            o = Long.valueOf(((Integer) o).longValue());
        } else if ((o instanceof Float)) {
            o = Double.valueOf(((Float) o).floatValue());
        } else if ((!(o instanceof Long)) && (!(o instanceof Double)) && (!(o instanceof String)) && (!(o instanceof Boolean))) {
            throw new IllegalArgumentException("unsupported type[" + o.getClass().getName() + "]");
        }
    }

    private Filter addFilter(String key, Long operator, Object value) {
        checkType(value);

        this.filter.put("key", key);
        this.filter.put("operator", operator);
        this.filter.put("value", value);
        return this;
    }

    public JSObject whereEqualTo(String key, Object value) {
        addFilter(key, Long.valueOf(1L), value);
        return getFilter();
    }

    public JSObject whereNotEqualTo(String key, Object value) {
        addFilter(key, Long.valueOf(2L), value);
        return getFilter();
    }

    public JSObject whereGreaterThan(String key, Object value) {
        addFilter(key, Long.valueOf(3L), value);
        return getFilter();
    }

    public JSObject whereGreaterThanOrEqualTo(String key, Object value) {
        addFilter(key, Long.valueOf(4L), value);
        return getFilter();
    }

    public JSObject whereLessThan(String key, Object value) {
        addFilter(key, Long.valueOf(5L), value);
        return getFilter();
    }

    public JSObject whereLessThanOrEqualTo(String key, Object value) {
        addFilter(key, Long.valueOf(6L), value);
        return getFilter();
    }

    public JSObject whereLikeTo(String key, Object value) {
        addFilter(key, Long.valueOf(7L), value);
        return getFilter();
    }

    public JSObject getFilter() {
        return this.filter;
    }
}