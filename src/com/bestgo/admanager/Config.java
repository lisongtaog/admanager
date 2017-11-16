package com.bestgo.admanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Copyright (c) 2015-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
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
    //第一句为了测试用
    regionLanguageRelMap.put("United Arab Emirates", "French");
    regionLanguageRelMap.put("Sint Maarten", "French");

    regionLanguageRelMap.put("Burundi", "French");
    regionLanguageRelMap.put("Central African Republic", "French");
    regionLanguageRelMap.put("Malawi", "English");
    regionLanguageRelMap.put("The Gambia", "Arabic");
    regionLanguageRelMap.put("Madagascar", "French");
    regionLanguageRelMap.put("Niger", "French");
    regionLanguageRelMap.put("Liberia", "English");
    regionLanguageRelMap.put("Guinea", "French");
    regionLanguageRelMap.put("Somalia", "Somali");
    regionLanguageRelMap.put("Togo", "French");
    regionLanguageRelMap.put("Afghanistan", "Pashto");
    regionLanguageRelMap.put("Uganda", "English");
    regionLanguageRelMap.put("Mozambique", "Portuguese");
    regionLanguageRelMap.put("Eritrea", "English");
    regionLanguageRelMap.put("Burkina Faso", "French");
    regionLanguageRelMap.put("Mali", "French");
    regionLanguageRelMap.put("Sierra Leone", "English");
    regionLanguageRelMap.put("Ethiopia", "Amharic");
    regionLanguageRelMap.put("Benin", "French");
    regionLanguageRelMap.put("South Sudan", "English");
    regionLanguageRelMap.put("Comoros", "Arabic");
    regionLanguageRelMap.put("Rwanda", "French");
    regionLanguageRelMap.put("Nepal", "Nepali");
    regionLanguageRelMap.put("Haiti", "French");
    regionLanguageRelMap.put("Tajikistan", "Tajik");
    regionLanguageRelMap.put("Palestine", "Arabic");
    return regionLanguageRelMap;
  }
}
