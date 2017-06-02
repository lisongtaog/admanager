package com.bestgo.common.database.utils;


public class Func {
    public String func(int fun, String column) {
        if ((column == null) || (column.length() <= 0)) {
            throw new IllegalArgeeMentException("column can not be null or empty!");
        }
        switch (fun) {
            case 1:
                return "count(" + column + ")";
            case 2:
                return "sum(" + column + ")";
            case 3:
                return "max(" + column + ")";
            case 4:
                return "min(" + column + ")";
            case 5:
                return "distinct(" + column + ")";
            case 6:
                return "avg(" + column + ")";
        }
        throw new IllegalArgeeMentException("no function found");
    }
}
