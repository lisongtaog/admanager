package com.bestgo.common.database.services;

import com.bestgo.common.database.dao.Sql;
import com.bestgo.common.database.utils.Func;
import com.bestgo.common.database.utils.IllegalArgeeMentException;
import com.bestgo.common.database.utils.JSObject;
import com.bestgo.common.database.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StoreClient
        extends Store {
    public String func(int func, String column) {
        return new Func().func(func, column);
    }

    public Store.Insert insert(String table) {
        if ((table == null) || (table.length() <= 0)) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }
        Insert insert = new Insert(table);

        return insert;
    }

    public Store.Delete delete(String table) {
        if ((table == null) || (table.length() <= 0)) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        Delete delete = new Delete(table);
        return delete;
    }

    public Store.Update update(String table) {
        if ((table == null) || (table.length() <= 0)) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        Update update = new Update(table);
        return update;
    }

    public Store.Scan scan(String table) {
        if ((table == null) || (table.length() <= 0)) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        Scan scan = new Scan(table);
        return scan;
    }

    public Store.SimpleScan simpleScan(String table) {
        if ((table == null) || (table.length() <= 0)) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        SimpleScan simpleScan = new SimpleScan(table);
        return simpleScan;
    }

    private boolean parseACObjectKVs(JSObject zo, boolean canEmpty, Object... kvs) {
        if ((kvs.length % 2 == 0) && ((canEmpty) || (kvs.length != 0))) {
            for (int i = 0; i < kvs.length - 1; i += 2) {
                Object key = kvs[i];
                Object value = kvs[(i + 1)];
                if ((key == null) || (!(key instanceof String)) || ((!(value instanceof String)) && (!(value instanceof Integer)) && (!(value instanceof Long)) && (!(value instanceof Float)) && (!(value instanceof Double)) && (!(value instanceof Boolean)))) {
                    Log.d("wrong parameter type. key[" + key + "] key_type[" + key.getClass().getName() + "] value[" + value + "] value_type[" + value.getClass().getName() + "]");
                    return false;
                }

                zo.put((String) key, value);
            }

            return true;
        }
        Log.d("invalid parameters.");
        return false;
    }

    public class SimpleScan implements Store.SimpleScan {
        private JSObject object;
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;
        private List<String> selects;

        public SimpleScan(String table) {
            this.object = new JSObject();
            this.filterObject = new JSObject();
            this.values = new ArrayList();
            this.orderByMap = new HashMap();
            this.table = table;
            this.selects = new ArrayList();
        }

        public Store.SimpleScan groupBy(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                this.object.add("groupBy", key);
            }

            return this;
        }

        public Store.SimpleScan select(String... var1) {
            for (int i = 0; i < var1.length; i++) {
                this.selects.add(var1[i]);
            }
            return this;
        }

        public Store.SimpleScan where(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                }
                this.whereFlag = true;
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(3L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgumentException("invalid filter");
        }

        public Store.SimpleScan and(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(1L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.SimpleScan or(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(2L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.SimpleScan orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 1L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(1L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(1L));
                }
            }
            return this;
        }

        public Store.SimpleScan orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 2L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(2L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(2L));
                }
            }

            return this;
        }

        public JSObject execute() throws Exception {
            if (this.selects.size() <= 0) {
                throw new IllegalArgeeMentException("select column must not be empty");
            }
            this.sql += "select";
            int seCount = 0;
            for (String se : this.selects) {
                seCount++;
                this.sql = (this.sql + " " + se + (seCount >= this.selects.size() ? "" : ","));
            }
            this.sql = (this.sql + " from " + this.table);

            if (this.whereFlag) {


                List<JSObject> filters = (List) this.filterObject.get("filters");
                if ((filters != null) && (filters.size() > 0)) {
                    for (JSObject filter : filters) {
                        long con = ((Long) filter.get("connector")).longValue();
                        switch ((int) con) {
                            case 3: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " where " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 1: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " and " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 2: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " or " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;
                        }

                    }
                }
            }


            List<String> groupBys = (List) this.filterObject.get("groupBy");
            int count;
            if ((groupBys != null) && (groupBys.size() > 0)) {
                this.sql += " group by";
                count = 0;
                for (String crObject : groupBys) {
                    count++;
                    this.sql = (this.sql + " " + crObject + (count >= groupBys.size() ? "" : ","));
                }
            }

            List<JSObject> orderBys = (List) this.filterObject.get("orderBy");
//            count = 0;
            if ((orderBys != null) && (orderBys.size() > 0)) {
                this.sql += " order by";
                count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    this.sql = (this.sql + " " + JSObject.get("key") + " " + StoreClient.this.getOrder(((Long) JSObject.get("order")).longValue()) + (count >= orderBys.size() ? "" : ","));
                }
            }

            this.sql += " limit 1";

            int size = this.values.size();
            Object[] valuesArray = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = this.values.get(i);
            }
            Log.d(this.sql);
            return Sql.findOne(this.sql, valuesArray);
        }
    }

    public class Scan implements Store.Scan {
        private JSObject object;
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private JSObject flagNumber;
        private boolean limitFlag = false;
        private boolean startFlag = false;
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;
        private List<String> selects;

        public Scan(String table) {
            this.object = new JSObject();
            this.filterObject = new JSObject();
            this.flagNumber = new JSObject();
            this.values = new ArrayList();
            this.orderByMap = new HashMap();
            this.table = table;
            this.selects = new ArrayList();
        }

        public Store.Scan groupBy(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                this.object.add("groupBy", key);
            }

            return this;
        }

        public Store.Scan select(String... var1) {
            for (int i = 0; i < var1.length; i++) {
                this.selects.add(var1[i]);
            }
            return this;
        }

        public Store.Scan start(int var1) {
            if (!this.limitFlag) {
                throw new IllegalArgeeMentException("limit must be called first");
            }
            this.startFlag = true;
            this.flagNumber.put("start", Long.valueOf(var1));
            return this;
        }

        public Store.Scan limit(int number) {
            this.limitFlag = true;
            this.flagNumber.put("limit", Long.valueOf(number));
            return this;
        }

        public Store.Scan where(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                }
                this.whereFlag = true;
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(3L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgumentException("invalid filter");
        }

        public Store.Scan and(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(1L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.Scan or(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(2L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.Scan orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 1L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(1L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(1L));
                }
            }
            return this;
        }

        public Store.Scan orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 2L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(2L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(2L));
                }
            }

            return this;
        }

        public List<JSObject> execute() throws Exception {
            if (this.selects.size() <= 0) {
                throw new IllegalArgeeMentException("select column must not be empty");
            }
            this.sql += "select";
            int seCount = 0;
            for (String se : this.selects) {
                seCount++;
                this.sql = (this.sql + " " + se + (seCount >= this.selects.size() ? "" : ","));
            }
            this.sql = (this.sql + " from " + this.table);

            if (this.whereFlag) {


                List<JSObject> filters = (List) this.filterObject.get("filters");
                if ((filters != null) && (filters.size() > 0)) {
                    for (JSObject filter : filters) {
                        long con = ((Long) filter.get("connector")).longValue();
                        switch ((int) con) {
                            case 3: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " where " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 1: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " and " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 2: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " or " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;
                        }

                    }
                }
            }


            List<String> groupBys = (List) this.filterObject.get("groupBy");
            int count;
            if ((groupBys != null) && (groupBys.size() > 0)) {
                this.sql += " group by";
                count = 0;
                for (String crObject : groupBys) {
                    count++;
                    this.sql = (this.sql + " " + crObject + (count >= groupBys.size() ? "" : ","));
                }
            }

            List<JSObject> orderBys = (List) this.filterObject.get("orderBy");
//            int count;
            if ((orderBys != null) && (orderBys.size() > 0)) {
                this.sql += " order by";
                count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    this.sql = (this.sql + " " + JSObject.get("key") + " " + StoreClient.this.getOrder(((Long) JSObject.get("order")).longValue()) + (count >= orderBys.size() ? "" : ","));
                }
            }


            if (this.limitFlag) {
                if (this.startFlag) {
                    this.sql = (this.sql + " limit " + this.flagNumber.get("start") + "," + this.flagNumber.get("limit"));
                } else {
                    this.sql = (this.sql + " limit " + this.flagNumber.get("limit"));
                }
            }

            int size = this.values.size();
            Object[] valuesArray = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = this.values.get(i);
            }
            Log.d(this.sql);
            return Sql.findList(this.sql, valuesArray);
        }
    }

    public class Update implements Store.Update {
        private JSObject object;
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private JSObject flagNumber;
        private boolean limitFlag = false;
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;

        public Update(String table) {
            this.object = new JSObject();
            this.filterObject = new JSObject();
            this.flagNumber = new JSObject();
            this.values = new ArrayList();
            this.orderByMap = new HashMap();
            this.table = table;
        }

        public Store.Update put(String var1, Object var2) {
            this.object.put(var1, var2);
            return this;
        }

        public Store.Update limit(int number) {
            this.limitFlag = true;
            this.flagNumber.put("limit", Long.valueOf(number));
            return this;
        }

        public Store.Update where(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                }
                this.whereFlag = true;
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(3L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgumentException("invalid filter");
        }

        public Store.Update and(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(1L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.Update or(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(2L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.Update orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 1L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(1L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(1L));
                }
            }
            return this;
        }

        public Store.Update orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 2L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(2L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(2L));
                }
            }

            return this;
        }

        public boolean execute() throws Exception {
            this.sql += "update ";
            this.sql += this.table;

            Set<String> keys = this.object.getKeys();
            int keyCount = 0;
            if (keys.size() > 0) {
                this.sql += " set";
                for (String key : keys) {
                    keyCount++;
                    this.sql += " ";
                    this.sql += key;
                    this.sql += (keyCount == keys.size() ? "=?" : "=?,");
                }
            }
            for (String key : keys) {
                this.values.add(this.object.get(key));
            }

            if (this.whereFlag) {


                List<JSObject> filters = (List) this.filterObject.get("filters");
                if ((filters != null) && (filters.size() > 0)) {
                    for (JSObject filter : filters) {
                        long con = ((Long) filter.get("connector")).longValue();
                        switch ((int) con) {
                            case 3: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " where " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 1: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " and " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 2: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " or " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;
                        }

                    }
                }
            }


            List<JSObject> orderBys = (List) this.filterObject.get("orderBy");
            int count;
            if ((orderBys != null) && (orderBys.size() > 0)) {
                this.sql += " order by";
                count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    this.sql = (this.sql + " " + JSObject.get("key") + " " + StoreClient.this.getOrder(((Long) JSObject.get("order")).longValue()) + (count >= orderBys.size() ? "" : ","));
                }
            }

            if (this.limitFlag) {
                this.sql = (this.sql + " limit " + this.flagNumber.get("limit"));
            }

            int size = this.values.size();
            Object[] valuesArray = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = this.values.get(i);
            }
            Log.d(this.sql);
            return Sql.update(this.sql, valuesArray);
        }
    }

    public class Delete implements Store.Delete {
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private JSObject flagNumber;
        private boolean limitFlag = false;
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;

        public Delete(String table) {
            this.filterObject = new JSObject();
            this.flagNumber = new JSObject();
            this.values = new ArrayList();
            this.orderByMap = new HashMap();
            this.table = table;
        }

        public Store.Delete limit(int number) {
            this.limitFlag = true;
            this.flagNumber.put("limit", Long.valueOf(number));
            return this;
        }

        public Store.Delete where(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                }
                this.whereFlag = true;
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(3L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgumentException("invalid filter");
        }

        public Store.Delete and(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(1L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.Delete or(JSObject filter) {
            if ((filter != null) && (filter.get("key") != null)) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                }
                JSObject zo = new JSObject();
                zo.put("connector", Long.valueOf(2L));
                zo.put("filter", filter);
                this.filterObject.add("filters", zo);
                return this;
            }

            throw new IllegalArgeeMentException("invalid filter");
        }

        public Store.Delete orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 1L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(1L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(1L));
                }
            }
            return this;
        }

        public Store.Delete orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; i$++) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != 2L) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(2L));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(2L));
                }
            }

            return this;
        }

        public boolean execute() throws Exception {
            this.sql += "delete from ";
            this.sql += this.table;
            if (this.whereFlag) {


                List<JSObject> filters = (List) this.filterObject.get("filters");
                if ((filters != null) && (filters.size() > 0)) {
                    for (JSObject filter : filters) {
                        long con = ((Long) filter.get("connector")).longValue();
                        switch ((int) con) {
                            case 3: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " where " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 1: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " and " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;

                            case 2: {
                                JSObject fi = (JSObject) filter.get("filter");
                                this.sql = (this.sql + " or " + fi.get("key") + " " + StoreClient.this.getOp(((Long) fi.get("operator")).longValue()) + " ?");
                                this.values.add(fi.get("value"));
                            }
                            break;
                        }

                    }
                }
            }


            List<JSObject> orderBys = (List) this.filterObject.get("orderBy");
            int count;
            if ((orderBys != null) && (orderBys.size() > 0)) {
                this.sql += " order by";
                count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    this.sql = (this.sql + " " + JSObject.get("key") + " " + StoreClient.this.getOrder(((Long) JSObject.get("order")).longValue()) + (count >= orderBys.size() ? "" : ","));
                }
            }

            if (this.limitFlag) {
                this.sql = (this.sql + " limit " + this.flagNumber.get("limit"));
            }

            int size = this.values.size();
            Object[] valuesArray = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = this.values.get(i);
            }
            Log.d(this.sql);
            return Sql.update(this.sql, valuesArray);
        }
    }

    private String getOrder(long i) {
        switch ((int) i) {
            case 1:
                return "asc";
            case 2:
                return "desc";
        }
        return "desc";
    }

    public String getOp(long i) {
        switch ((int) i) {
            case 1:
                return "=";
            case 2:
                return "!=";
            case 3:
                return ">";
            case 4:
                return ">=";
            case 5:
                return "<";
            case 6:
                return "<=";
            case 7:
                return "like";
        }
        return "=";
    }

    public class Insert implements Store.Insert {
        private JSObject object;
        private String sql = "";
        private String table = "";

        public Insert(String table) {
            this.table = table;
            this.object = new JSObject();
        }

        public Store.Insert put(String key, Object value) {
            this.object.put(key, value);
            return this;
        }

        public boolean execute() throws Exception {
            this.sql = ("insert into " + this.table + " (");
            Set<String> keys = this.object.getKeys();
            int count = 0;
            for (String key : keys) {
                count++;
                this.sql += key;
                this.sql += (count == keys.size() ? ")" : ",");
            }
            count = 0;
            this.sql += " values (";
            Object[] objects = new Object[keys.size()];
            for (String key : keys) {
                objects[count] = this.object.get(key);
                count++;
                this.sql += (count == keys.size() ? "?)" : "?,");
            }
            Log.d(this.sql);
            return Sql.update(this.sql, objects);
        }
    }
}