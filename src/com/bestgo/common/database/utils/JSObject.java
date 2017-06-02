package com.bestgo.common.database.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

public class JSObject {
    private HashMap<String, Object> data = new HashMap();


    private boolean checkType(Object o) {
        return ((o instanceof String)) || ((o instanceof Integer)) || ((o instanceof Short)) || ((o instanceof Long)) || ((o instanceof Double)) || ((o instanceof Boolean)) || ((o instanceof java.util.List)) || ((o instanceof JSObject)) || ((o instanceof Date) || ((o instanceof BigDecimal)));
    }

    private void ensureType(Object o) {
        if (o == null)
            throw new IllegalArgumentException("value is empty");
        if (!checkType(o)) {
            throw new IllegalArgumentException("unsupported type[" + o.getClass().getName() + "]");
        }
    }

    public <T> T get(String key) {
        Object o = this.data.get(key);
        return (T) o;
    }

    public <T> JSObject put(String key, T value) {
        ensureType(value);
        this.data.put(key, value);
        return this;
    }

    public boolean contains(String key) {
        return this.data.containsKey(key);
    }

    public java.util.Set<String> getKeys() {
        return this.data.keySet();
    }

    public JSObject add(String key, Object value) {
        ensureType(value);
        Object o = this.data.get(key);
        Object as;
        if (o == null) {
            as = new java.util.ArrayList();
        } else {
            if (!(o instanceof java.util.ArrayList)) {
                throw new IllegalArgumentException("this key is not an arraylist");
            }

            as = (java.util.List) o;
        }

        ((java.util.List) as).add(value);
        this.data.put(key, as);
        return this;
    }

    public boolean hasObjectData() {
        return !this.data.isEmpty();
    }

    public HashMap<String, Object> getObjectData() {
        return this.data;
    }

    public void setObjectData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void delete(String key) {
        this.data.remove(key);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        java.util.Set keys = getKeys();
        java.util.Iterator iterator = keys.iterator();
        int i = 0;

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            sb.append("\"" + key + "\":");
            if (get(key) == null) {
                sb.append("\"\"");
            } else if ((get(key) instanceof java.util.List)) {
                sb.append("[");
                java.util.List objects = (java.util.List) get(key);
                int j = 0;
                java.util.Iterator i$ = objects.iterator();

                while (i$.hasNext()) {
                    Object object = i$.next();
                    if (object == null) {
                        sb.append("\"\"");
                    } else if ((object instanceof String)) {
                        sb.append("\"" + object.toString() + "\"");
                    } else if (checkType(object)) {
                        sb.append(object.toString());
                    }

                    j++;
                    if (j != objects.size()) {
                        sb.append(",");
                    }
                }

                sb.append("]");
            } else if ((get(key) instanceof String)) {
                sb.append("\"" + get(key).toString() + "\"");
            } else if (checkType(get(key))) {
                sb.append(get(key).toString());
            }

            i++;
            if (i != keys.size()) {
                sb.append(",");
            }
        }

        sb.append("}");
        return sb.toString();
    }
}