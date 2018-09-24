package com.bestgo.admanager.constant;

/**
 * @author mengjun
 * @date 2018/9/22 20:00
 * @desc Jedis使用的常量类
 */
public class JedisConstant {
    //所有的数据都是字符串存储，如果遇到数字类型，需要转换
    public static final String TAG_NAME_BIDDING_MAP = "tagNameBiddingMap";         //标签名称与最大出价Map
    public static final String TAG_NAME_APP_ID_MAP = "tagNameAppIdMap";        //标签名称与应用ID(包ID)Map
    public static final String COUNTRY_NAME_CODE_MAP = "countryNameCodeMap";      //对应国家代号表的数据key=国家名称value=国家代号
    public static final String COUNTRY_CODE_NAME_MAP = "countryCodeNameMap";       //对应国家代号表的数据key=国家代号value=国家名称
}
