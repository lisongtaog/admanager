 package com.bestgo.common.database.dao;

 import com.bestgo.common.database.utils.JSObject;

 import java.util.List;

 public class Sql
 {
   public static IDao getIPeinterDaoInstance()
   {
     return new DaoProxy();
   }
   
   public static List<JSObject> findList(String sql, Object... params) throws Exception {
     return getIPeinterDaoInstance().findModeResult(sql, params);
   }
   
   public static boolean update(String sql, Object... params) throws Exception
   {
     return getIPeinterDaoInstance().updateByPreparedStatement(sql, params);
   }
   
   public static JSObject findOne(String sql, Object... params) throws Exception
   {
     return getIPeinterDaoInstance().findSimpleResult(sql, params);
   }
 }
