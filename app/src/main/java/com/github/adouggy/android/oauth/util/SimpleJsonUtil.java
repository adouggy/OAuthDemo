package com.github.adouggy.android.oauth.util;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by liyazi on 15/12/21.
 */
public class SimpleJsonUtil {
    public static Map<String, Object> parseJson(String json){
        return  (Map<String,Object>)JSON.parse(json);
    }

    public static String getTicket(String json){
        Map<String, Object> map = parseJson(json);
        if( map.containsKey("data") ){
            Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
            if( dataMap!=null && dataMap.containsKey("p") ){
                return (String) dataMap.get("p");
            }
        }
        return null;
    }
}
