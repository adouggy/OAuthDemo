package com.github.adouggy.android.oauth.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.adouggy.android.oauth.util.ConstantValues;
import com.github.adouggy.android.oauth.util.OAuthType;
import com.github.adouggy.android.oauth.util.PackageHashUtil;
import com.github.adouggy.android.oauth.util.SimpleJsonUtil;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weeeye.desafinado.R;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements WeiboAuthListener {

    public final static String TAG = "MainActivity";

    private CallbackManager callbackManager;

    public static TextView mTokenTextView = null;

    final String appId = "wxb3c2e9592e1086d7";

    // IWXAPI 是第三方app和微信通信的openapi接口
    public static IWXAPI api;


    private void loginWithWeixin() {
        if( api == null ) {
            api = WXAPIFactory.createWXAPI(this, appId, false);
            boolean regWechat = api.registerApp(appId);
            Log.i(TAG, "注册微信:" + regWechat);
        }

        if (!api.isWXAppInstalled()) {
            Log.i(TAG, "您还未安装微信客户端");
            return;
        }

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        boolean res = api.sendReq(req);
        Log.i(TAG, "send:" + res);
    }

    SsoHandler mSsoHandler = null;
    private void loginWithWeibo() {
        final String APP_KEY      = "1847700491";		   // 应用的APP_KEY
        final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页
        final String SCOPE = 							   // 应用申请的高级权限
                "email,direct_messages_read,direct_messages_write,"
                        + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                        + "follow_app_official_microblog," + "invitation_write";

        AuthInfo authInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
        if( mSsoHandler == null )
            mSsoHandler = new SsoHandler(this, authInfo);
        mSsoHandler.authorize(this);
    }

    Tencent mTencent = null;
    private IUiListener qqCallback =  new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Log.i(TAG, "complete");
            Log.i(TAG, o.toString());
        }

        @Override
        public void onError(UiError uiError) {
            Log.i(TAG, "error");
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "cancel");
        }
    };

    private void loginWithQQ() {
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, "get_simple_userinfo", qqCallback);

        }else{
            Log.i(TAG, "not valid session");
        }

        //Log.i(TAG, "send:" + res);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i(TAG, "package hash:" + PackageHashUtil.getHash(this));

        setTitle("Boom Pre");

        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        setContentView(R.layout.activity_main);

        mTokenTextView = (TextView) findViewById(R.id.tv_token);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i(TAG, "managerLogin onSuccess:" + loginResult.toString());
                        AccessToken token = loginResult.getAccessToken();
                        String tokenStr = token.getToken();

                        Log.i(TAG, "token:" + tokenStr + ",expires:" + token.getExpires() + ",last refersh:" + token.getLastRefresh());

                        loginApi(OAuthType.facebook.name(), tokenStr);
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "manager Login onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(TAG, "manager Login onError:" + exception.toString());
                    }
                });

        LoginButton loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(permissionNeeds);


        Button btnGetHash = (Button) findViewById(R.id.btn_get_hash);
        btnGetHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hash = PackageHashUtil.getHash(MainActivity.this);
                Log.i("MainActivity", "hash:" + hash);
                Toast.makeText(MainActivity.this.getApplicationContext(), hash, Toast.LENGTH_LONG).show();
            }
        });

        //for instagram
        Button btnLoginInstagram = (Button) findViewById(R.id.btn_login_instagram);
        btnLoginInstagram.setOnClickListener(new View.OnClickListener() {
            Dialog auth_dialog;

            @Override
            public void onClick(View v) {
                auth_dialog = new Dialog(MainActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                final WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);

//                Log.i(TAG, "instagram oauthUrl:" + ConstantValues.INSTAGRAM_OAUTH_URL);
//                web.loadUrl(ConstantValues.INSTAGRAM_OAUTH_URL);
                web.loadUrl(ConstantValues.API_URL + "/oauth/instagram");
                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("登录Instagram");
                auth_dialog.setCancelable(true);
            }
        });

        //https://dev.twitter.com/web/sign-in/implementing
        Button btnLoginTwitter = (Button) findViewById(R.id.btn_login_twitter);
        btnLoginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog auth_dialog = new Dialog(MainActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                final WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);

                web.loadUrl(ConstantValues.API_URL + "/oauth/twitter");
                web.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("登录Twitter");
                auth_dialog.setCancelable(true);
            }
        });

        //for wechat
        Button btnLoginWechat = (Button) findViewById(R.id.btn_login_wechat);
        btnLoginWechat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i(TAG, "准备登陆微信");
                loginWithWeixin();
            }
        });

        //for weibo
        Button btnLoginWeibo = (Button) findViewById(R.id.btn_login_weibo);
        btnLoginWeibo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i(TAG, "准备登陆微博");
                loginWithWeibo();
            }
        });

        final String APP_KEY      = "1105117093";		   // 应用的APP_KEY
        mTencent = Tencent.createInstance(APP_KEY, this.getApplicationContext());
        //for weibo
        Button btnLoginQQ = (Button) findViewById(R.id.btn_login_qq);
        btnLoginQQ.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i(TAG, "准备登陆QQ");
                loginWithQQ();
            }
        });

        Button btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                String p = mTokenTextView.getText().toString();
                Toast.makeText(MainActivity.this, "logout with ticket:" + p, Toast.LENGTH_SHORT).show();
                logout(p);
             }
         }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }


    @Override
    protected void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }


       // mTencent.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult");
        mTencent.onActivityResultData(requestCode, resultCode, data, qqCallback);
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * boom api for /login
     *
     * @param type
     * @param token
     */
    private void loginApi(String type, String token) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = ConstantValues.API_URL + "/login?from=" + type + "&token=" + token;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "api :" + response);
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                        mTokenTextView.setText(SimpleJsonUtil.getTicket(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "api error:" + error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();
    }

    private void logout(final String ticket){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
//        final String url = ConstantValues.API_URL + "/logout";
        final String url = ConstantValues.API_URL + "/login";

        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "api :" + response);
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "api error:" + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("p", ticket);
                return map;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        queue.start();
    }


        @Override
        public void onComplete(Bundle bundle) {
            Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(bundle); // 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
                Log.i(TAG, "微博token:" + mAccessToken.getToken());
            } else {
                Log.i(TAG, "微博token错误");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.i(TAG, "微博EXCEPTION:" + e.getMessage());
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "微博cancel");
        }

}
