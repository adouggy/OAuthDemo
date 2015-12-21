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

    public static String TWITTER_CONSUMER_KEY = "YFrgOubzZhK8jrsU1XGNeF4Ex";
    public static String TWITTER_CONSUMER_SECRET = "tbAf2Rnu8x390ZmHKOYBpW46rPKTgfc7g7VzUreylWjaRUjFX9";
    public static String TWITTER_CALLBACK_URL = "oauth://com.hintdesk.Twitter_oAuth";
    public static String URL_PARAMETER_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN="TWITTER_OAUTH_TOKEN";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET="TWITTER_OAUTH_TOKEN_SECRET";
    public static String PREFERENCE_TWITTER_IS_LOGGED_IN="TWITTER_IS_LOGGED_IN";
}
