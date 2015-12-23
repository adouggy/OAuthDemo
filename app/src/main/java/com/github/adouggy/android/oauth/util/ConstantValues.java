package com.github.adouggy.android.oauth.util;

/**
 * Created with IntelliJ IDEA.
 * User: ServusKevin
 * Date: 5/2/13
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstantValues {
    //    public static final String API_URL = "http://115.28.235.57";
//    public static final String API_URL = "http://192.168.13.110:28080/boom";
    public static final String API_URL = "http://api.boom.weeeye.com";


    public static final String INSTAGRAM_URL = "https://api.instagram.com/oauth/authorize";
    public static final String INSTAGRAM_CALLBACK = API_URL + "/oauth/callback?type=instagram";
    public static final String INSTAGRAM_CLIENT_ID = "c3d4805f1d654f518412653adf37f3c1";
    public static final String INSTAGRAM_OAUTH_URL = INSTAGRAM_URL + "?redirect_uri=" + INSTAGRAM_CALLBACK + "&response_type=code&client_id=" + INSTAGRAM_CLIENT_ID + "&scope=basic";


    public static final String TWITTER_URL = "https://api.twitter.com";
    public static final String TWITTER_REQUEST_TOKEN = TWITTER_URL + "/oauth/request_token";
    public static final String TWITTER_AUTHENTICATE = TWITTER_URL + "/oauth/authenticate";

}
