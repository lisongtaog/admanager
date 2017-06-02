package com.bestgo.common.database.utils;

import com.bestgo.common.database.services.DB;

public class Log {
    public static void d(String message) {
        if (DB.isDebuged()) {
            System.out.println(message);
        }
    }
}