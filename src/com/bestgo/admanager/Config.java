package com.bestgo.admanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Copyright (c) 2015-present, Facebook, Inc. All rights reserved.
 * <p>
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 * <p>
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

public class Config {
    public static final String ACCESS_TOKEN = "EAATOyglKSZCEBALV5RkjJUmfnEVZAGAGUY3mVdQLhd4PwOAJOoZAidGnJRyxZBhKJmKZBHfOYh0rhajQcTUCIAXVgTTBJ5rXScbzHkJYtINcWocDRjcWwn5jcKHlDFmD8EKx6kjCL0mbZBYlEYr96AstiwdUzirq6fq5ig6FY6eCOXN5uV1JJBecDZBzVG9UDQZD";
    public static final Long ACCOUNT_ID = 160457491007145l;
    public static final String APP_ID = "1353267041422321";
    public static final String APP_SECRET = "e18625b3a697595077b1f7d51a840dcb";
    public static final String IMAGE_FILE = "image.png";
    public static final String VIDEO_FILE = "video.mp4";
    public static final String PAGE_ID = "Your Page ID";
    public static final Long BUSINESS_ID = 637537766429928l;
    public static final String DPA_FEED_FILE_PATH = "dpa-feed-example.xml";
    public static final String CAMPAIGN_ID = "Your campaign ID";

    public static final String ACCESS_TOKEN_URL = "https://www.facebook.com/v2.9/dialog/oauth?client_id=1353267041422321&redirect_uri=http://suijide.info&response_type=token+code&scope=ads_management";


    public static void setProxy() {
        Properties prop = System.getProperties();
        prop.setProperty("http.proxyHost", "218.93.127.86");
        prop.setProperty("http.proxyPort", "7790");
        prop.setProperty("https.proxyHost", "218.93.127.86");
        prop.setProperty("https.proxyPort", "7790");
    }


    public static Map<String, String> getRegionLanguageRelMap() {
        Map<String, String> regionLanguageRelMap = new HashMap<>();


        //同一个国家，不同的写法
        regionLanguageRelMap.put("Côte d'Ivoire","French");
        regionLanguageRelMap.put("Cote d'Ivoire","French");
        regionLanguageRelMap.put("Ivory Coast","French");

        //台湾
        regionLanguageRelMap.put("Taiwan, China","Traditional");
        regionLanguageRelMap.put("Taiwan","Traditional");

        //A
        regionLanguageRelMap.put("Afghanistan","Pashto");
        regionLanguageRelMap.put("Andorra","Catalan");
        regionLanguageRelMap.put("Austria","German");
        regionLanguageRelMap.put("Antigua","English");
        regionLanguageRelMap.put("Anguilla","English");
        regionLanguageRelMap.put("American Samoa","English");
        regionLanguageRelMap.put("Aruba","Dutch");
        regionLanguageRelMap.put("Argentina","Spanish");
        regionLanguageRelMap.put("Azerbaijan","Azerbaijani");
        regionLanguageRelMap.put("Armenia","Armenian");
        regionLanguageRelMap.put("Algeria","Arabic");
        regionLanguageRelMap.put("Angola","Portuguese");
        regionLanguageRelMap.put("Australia","English");

        //B
        regionLanguageRelMap.put("Bosnia and Herzegovina","Bosnian");
        regionLanguageRelMap.put("Burundi","French");
        regionLanguageRelMap.put("Benin","French");
        regionLanguageRelMap.put("Bangladesh","Bengali");
        regionLanguageRelMap.put("Bhutan","English");
        regionLanguageRelMap.put("Botswana","English");
        regionLanguageRelMap.put("Bulgaria","Bulgarian");
        regionLanguageRelMap.put("Belarus","Russian");
        regionLanguageRelMap.put("Bahrain","Arabic");
        regionLanguageRelMap.put("Brunei","Malay");
        regionLanguageRelMap.put("Belgium","Dutch");
        regionLanguageRelMap.put("British Virgin Islands","English");
        regionLanguageRelMap.put("Bermuda","English");
        regionLanguageRelMap.put("Barbados","English");
        regionLanguageRelMap.put("Brazil","Portuguese");
        regionLanguageRelMap.put("Belize","English");
        regionLanguageRelMap.put("Bolivia","Spanish");
        regionLanguageRelMap.put("Burkina Faso","French");

        //C
        regionLanguageRelMap.put("Central African Republic","French");
        regionLanguageRelMap.put("Cameroon","French");
        regionLanguageRelMap.put("Côte d'Ivoire","French");
        regionLanguageRelMap.put("Cambodia","English");
        regionLanguageRelMap.put("China","Chinese");
        regionLanguageRelMap.put("Costa Rica","Spanish");
        regionLanguageRelMap.put("Croatia","Croatian");
        regionLanguageRelMap.put("Chile","Spanish");
        regionLanguageRelMap.put("Czech Republic","Czech");
        regionLanguageRelMap.put("Czechia","Czech");
        regionLanguageRelMap.put("Canada","English");

        regionLanguageRelMap.put("Dominican Republic","Spanish");
        regionLanguageRelMap.put("Malawi","English");
        regionLanguageRelMap.put("The Gambia","Arabic");
        regionLanguageRelMap.put("Madagascar","French");
        regionLanguageRelMap.put("Niger","French");
        regionLanguageRelMap.put("Liberia","English");
        regionLanguageRelMap.put("Guinea","French");
        regionLanguageRelMap.put("Somalia","Somali");
        regionLanguageRelMap.put("Togo","French");

        regionLanguageRelMap.put("Uganda","English");
        regionLanguageRelMap.put("Mozambique","Portuguese");
        regionLanguageRelMap.put("Eritrea","English");

        regionLanguageRelMap.put("Mali","French");
        regionLanguageRelMap.put("Sierra Leone","English");
        regionLanguageRelMap.put("Ethiopia","Amharic");
        regionLanguageRelMap.put("South Sudan","English");
        regionLanguageRelMap.put("Comoros","Arabic");
        regionLanguageRelMap.put("Rwanda","French");
        regionLanguageRelMap.put("Nepal","Nepali");
        regionLanguageRelMap.put("Haiti","French");
        regionLanguageRelMap.put("Tajikistan","Tajik");
        regionLanguageRelMap.put("Palestine","Arabic");
        regionLanguageRelMap.put("Monaco","French");
        regionLanguageRelMap.put("Senegal","French");
        regionLanguageRelMap.put("Tanzania","Swahili");
        regionLanguageRelMap.put("Chad","French");
        regionLanguageRelMap.put("Zimbabwe","English");
        regionLanguageRelMap.put("Lesotho","English");
        regionLanguageRelMap.put("Timor-Leste","Portuguese");
        regionLanguageRelMap.put("Kyrgyzstan","Kyrgyz");
        regionLanguageRelMap.put("Yemen","Arabic");
        regionLanguageRelMap.put("Mauritania","Arabic");
        regionLanguageRelMap.put("Myanmar","Burmese");
        regionLanguageRelMap.put("Myanmar (Burma)","Burmese");
        regionLanguageRelMap.put("Ghana","English");
        regionLanguageRelMap.put("Pakistan","Urdu");
        regionLanguageRelMap.put("Kenya","Swahili");
        regionLanguageRelMap.put("Kiribati","English");
        regionLanguageRelMap.put("Zambia","English");
        regionLanguageRelMap.put("Sao Tome and Principe","Portuguese");
        regionLanguageRelMap.put("India","Hindi");
        regionLanguageRelMap.put("Moldova","English");
        regionLanguageRelMap.put("Laos","Lao");
        regionLanguageRelMap.put("Djibouti","French");
        regionLanguageRelMap.put("Nicaragua","Spanish");
        regionLanguageRelMap.put("Solomon Islands","English");
        regionLanguageRelMap.put("Ukraine","Ukrainian");
        regionLanguageRelMap.put("Uzbekistan","Uzbek");
        regionLanguageRelMap.put("Vietnam","Vietnamese");
        regionLanguageRelMap.put("Papua New Guinea","English");
        regionLanguageRelMap.put("Honduras","Spanish");
        regionLanguageRelMap.put("Nigeria","English");
        regionLanguageRelMap.put("Vanuatu","English");
        regionLanguageRelMap.put("Philippines","Filipino");
        regionLanguageRelMap.put("Federated States of Micronesia","English");
        regionLanguageRelMap.put("Morocco","Arabic");
        regionLanguageRelMap.put("Cape Verde","Portuguese");
        regionLanguageRelMap.put("Egypt","Arabic");
        regionLanguageRelMap.put("Indonesia","Indonesian");
        regionLanguageRelMap.put("Kosovo","Albanian");
        regionLanguageRelMap.put("Georgia","Georgian");
        regionLanguageRelMap.put("Sri Lanka","Sinhala");
        regionLanguageRelMap.put("Swaziland","English");
        regionLanguageRelMap.put("Guatemala","Spanish");
        regionLanguageRelMap.put("Samoa","Samoan");
        regionLanguageRelMap.put("Tunisia","Arabic");
        regionLanguageRelMap.put("Guyana","English");
        regionLanguageRelMap.put("El Salvador","Spanish");


        regionLanguageRelMap.put("Paraguay","Spanish");
        regionLanguageRelMap.put("Mongolia","Mongolian");
        regionLanguageRelMap.put("Albania","Albanian");
        regionLanguageRelMap.put("Venezuela","Spanish");
        regionLanguageRelMap.put("Tonga","English");

        regionLanguageRelMap.put("Iraq","Arabic");
        regionLanguageRelMap.put("Libya","Arabic");
        regionLanguageRelMap.put("Macedonia","Macedonian");
        regionLanguageRelMap.put("Jamaica","English");
        regionLanguageRelMap.put("Fiji","English");
        regionLanguageRelMap.put("Serbia","Serbian");
        regionLanguageRelMap.put("Thailand","Thai");
        regionLanguageRelMap.put("Jordan","English");
        regionLanguageRelMap.put("Peru","Spanish");
        regionLanguageRelMap.put("Colombia","Spanish");
        regionLanguageRelMap.put("South Africa","English");
        regionLanguageRelMap.put("Namibia","English");
        regionLanguageRelMap.put("Ecuador","Spanish");
        regionLanguageRelMap.put("Montenegro","English");
        regionLanguageRelMap.put("Dominica","Spanish");
        regionLanguageRelMap.put("Saint Vincent and the Grenadines","English");
        regionLanguageRelMap.put("Turkmenistan","English");
        regionLanguageRelMap.put("St. Lucia","English");
        regionLanguageRelMap.put("Russia","Russian");
        regionLanguageRelMap.put("Gabon","French");
        regionLanguageRelMap.put("Maldives","English");
        regionLanguageRelMap.put("Grenada","English");
        regionLanguageRelMap.put("Romania","Romanian");
        regionLanguageRelMap.put("Suriname","Dutch");
        regionLanguageRelMap.put("Mauritius","English");
        regionLanguageRelMap.put("Turkey","Turkish");
        regionLanguageRelMap.put("Mexico","Spanish");
        regionLanguageRelMap.put("Malaysia","Malay");
        regionLanguageRelMap.put("Kazakhstan","Russian");
        regionLanguageRelMap.put("Panama","Spanish");
        regionLanguageRelMap.put("Lebanon","Arabic");
        regionLanguageRelMap.put("Hungary","Hungarian");
        regionLanguageRelMap.put("Equatorial Guinea","Spanish");
        regionLanguageRelMap.put("Poland","Polish");
        regionLanguageRelMap.put("Latvia","Latvian");
        regionLanguageRelMap.put("Lithuania","Lithuanian");
        regionLanguageRelMap.put("Oman","Arabic");
        regionLanguageRelMap.put("Slovakia","Slovak");
        regionLanguageRelMap.put("Uruguay","Spanish");
        regionLanguageRelMap.put("Estonia","Estonian");
        regionLanguageRelMap.put("Greece","Greek");
        regionLanguageRelMap.put("Portugal","Portuguese");
        regionLanguageRelMap.put("Saudi Arabia","Arabic");
        regionLanguageRelMap.put("Trinidad and Tobago","English");
        regionLanguageRelMap.put("Slovenia","Slovenian");
        regionLanguageRelMap.put("Cyprus","Greek");
        regionLanguageRelMap.put("Malta","English");



        regionLanguageRelMap.put("The Bahamas","English");
        regionLanguageRelMap.put("Spain","Spanish");
        regionLanguageRelMap.put("South Korea","Korean");
        regionLanguageRelMap.put("Italy","Italian");
        regionLanguageRelMap.put("Kuwait","Arabic");
        regionLanguageRelMap.put("Japan","Japanese");
        regionLanguageRelMap.put("United Arab Emirates","Arabic");
        regionLanguageRelMap.put("Israel","Hebrew");
        regionLanguageRelMap.put("New Zealand","English");
        regionLanguageRelMap.put("France","French");
        regionLanguageRelMap.put("Germany","German");
        regionLanguageRelMap.put("Hong Kong","Traditional");
        regionLanguageRelMap.put("Finland","Finnish");
        regionLanguageRelMap.put("United Kingdom","English");
        regionLanguageRelMap.put("Netherlands","Dutch");
        regionLanguageRelMap.put("Ireland","English");
        regionLanguageRelMap.put("Sweden","Swedish");
        regionLanguageRelMap.put("Iceland","Icelandic");
        regionLanguageRelMap.put("Denmark","Danish");

        regionLanguageRelMap.put("Singapore","English");
        regionLanguageRelMap.put("United States","English");
        regionLanguageRelMap.put("Norway","Norwegian");
        regionLanguageRelMap.put("Qatar","Arabic");
        regionLanguageRelMap.put("Switzerland","German");
        regionLanguageRelMap.put("Luxembourg","Luxembourgish");
        regionLanguageRelMap.put("Dominican","Spanish");
        regionLanguageRelMap.put("Netherlands Antilles","Dutch");
        regionLanguageRelMap.put("Democratic Republic of the Congo","French");
        regionLanguageRelMap.put("Republic of the Congo","French");
        regionLanguageRelMap.put("Curaçao","Dutch");
        regionLanguageRelMap.put("French Guiana","French");
        regionLanguageRelMap.put("Guernsey","English");
        regionLanguageRelMap.put("Gibraltar","English");
        regionLanguageRelMap.put("Greenland","Danish");
        regionLanguageRelMap.put("Guam","English");
        regionLanguageRelMap.put("Guinea-Bissau","English");
        regionLanguageRelMap.put("Isle Of Man","English");
        regionLanguageRelMap.put("Jersey","English");
        regionLanguageRelMap.put("Saint Kitts and Nevis","English");
        regionLanguageRelMap.put("Cayman Islands","English");
        regionLanguageRelMap.put("Marshall Islands","English");
        regionLanguageRelMap.put("Macau","Traditional");
        regionLanguageRelMap.put("Northern Mariana Islands","English");
        regionLanguageRelMap.put("Martinique","French");
        regionLanguageRelMap.put("New Caledonia","French");
        regionLanguageRelMap.put("French Polynesia","French");
        regionLanguageRelMap.put("Puerto Rico","Spanish");
        regionLanguageRelMap.put("Réunion","French");
        regionLanguageRelMap.put("Seychelles","French");
        regionLanguageRelMap.put("Sint Maarten","English");
        regionLanguageRelMap.put("Turks and Caicos Islands","English");
        regionLanguageRelMap.put("US Virgin Islands","English");
        regionLanguageRelMap.put("Mayotte","French");
        return regionLanguageRelMap;
    }


    /**
     * 语言与ISO-639-1 Code的对照表
     * @return Map集合
     */
    public static Map<String, String> getLanguageAndCodeRelMap() {
        Map<String, String> languageAndCodeRelMap = new HashMap<>();

        languageAndCodeRelMap.put("Afrikaans", "en");
        languageAndCodeRelMap.put("Albanian", "sq");
        languageAndCodeRelMap.put("Amharic", "am");
        languageAndCodeRelMap.put("Arabic", "ar");
        languageAndCodeRelMap.put("Armenian", "hy");
        languageAndCodeRelMap.put("Azeerbaijani", "az");
        languageAndCodeRelMap.put("Basque", "eu");
        languageAndCodeRelMap.put("Belarusian", "be");
        languageAndCodeRelMap.put("Bengali", "bn");
        languageAndCodeRelMap.put("Bosnian", "bs");
        languageAndCodeRelMap.put("Bulgarian", "bg");
        languageAndCodeRelMap.put("Catalan", "ca");
        languageAndCodeRelMap.put("Chinese", "zh-CN");
        languageAndCodeRelMap.put("Corsican", "co");
        languageAndCodeRelMap.put("Croatian", "hr");
        languageAndCodeRelMap.put("Czech", "cs");
        languageAndCodeRelMap.put("Danish", "da");
        languageAndCodeRelMap.put("Dutch", "nl");
        languageAndCodeRelMap.put("English", "en");
        languageAndCodeRelMap.put("Esperanto", "eo");
        languageAndCodeRelMap.put("Estonian", "et");
        languageAndCodeRelMap.put("Finnish", "fi");
        languageAndCodeRelMap.put("French", "fr");
        languageAndCodeRelMap.put("Frisian", "fy");
        languageAndCodeRelMap.put("Galician", "gl");
        languageAndCodeRelMap.put("Georgian", "ka");
        languageAndCodeRelMap.put("German", "de");
        languageAndCodeRelMap.put("Greek", "el");
        languageAndCodeRelMap.put("Gujarati", "gu");
        languageAndCodeRelMap.put("Haitian Creole", "ht");
        languageAndCodeRelMap.put("Hebrew", "iw");
        languageAndCodeRelMap.put("Hindi", "hi");
        languageAndCodeRelMap.put("Hungarian", "hu");
        languageAndCodeRelMap.put("Hungarian", "hu");
        languageAndCodeRelMap.put("Icelandic", "is");
        languageAndCodeRelMap.put("Indonesian", "id");
        languageAndCodeRelMap.put("Italian", "it");
        languageAndCodeRelMap.put("Japanese", "ja");
        languageAndCodeRelMap.put("Korean", "ko");
        languageAndCodeRelMap.put("Kyrgyz", "ky");
        languageAndCodeRelMap.put("Lao", "lo");
        languageAndCodeRelMap.put("Latvian", "lv");
        languageAndCodeRelMap.put("Lithuanian", "lt");
        languageAndCodeRelMap.put("Luxembourgish", "lb");
        languageAndCodeRelMap.put("Macedonian", "mk");
        languageAndCodeRelMap.put("Malagasy", "mg");
        languageAndCodeRelMap.put("Malay", "ms");
        languageAndCodeRelMap.put("Mongolian", "mn");
        languageAndCodeRelMap.put("Nepali", "ne");
        languageAndCodeRelMap.put("Pashto", "ps");
        languageAndCodeRelMap.put("Polish", "pl");
        languageAndCodeRelMap.put("Portuguese", "pt");
        languageAndCodeRelMap.put("Romanian", "ro");
        languageAndCodeRelMap.put("Russian", "ru");
        languageAndCodeRelMap.put("Samoan", "sm");
        languageAndCodeRelMap.put("Serbian", "sr");
        languageAndCodeRelMap.put("Sinhala", "si");
        languageAndCodeRelMap.put("Slovak", "sk");
        languageAndCodeRelMap.put("Slovenian", "sl");
        languageAndCodeRelMap.put("Somali", "so");
        languageAndCodeRelMap.put("Spanish", "es");
        languageAndCodeRelMap.put("Swahili", "sw");
        languageAndCodeRelMap.put("Swedish", "sv");
        languageAndCodeRelMap.put("Tajik", "tg");
        languageAndCodeRelMap.put("Thai", "th");
        languageAndCodeRelMap.put("Norwegian", "no");
        languageAndCodeRelMap.put("Traditional", "zh-TW");
        languageAndCodeRelMap.put("Turkish", "tr");
        languageAndCodeRelMap.put("Ukrainian", "uk");
        languageAndCodeRelMap.put("Urdu", "ur");
        languageAndCodeRelMap.put("Uzbek", "uz");
        languageAndCodeRelMap.put("Vietnamese", "vi");

        return languageAndCodeRelMap;
    }
}
