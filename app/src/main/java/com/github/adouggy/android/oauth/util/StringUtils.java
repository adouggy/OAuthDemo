package com.github.adouggy.android.oauth.util;

/**
 * Created by liyazi on 15/11/30.
 */
public class StringUtils {

    public static boolean isNotBlank(String string){
        return !isBlank(string);
    }

    public static boolean isBlank(String string){
        return string == null || string.length() == 0;
    }
}
