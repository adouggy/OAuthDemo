package com.github.adouggy.android.oauth.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.adouggy.android.oauth.R;
import com.github.adouggy.android.oauth.util.ConstantValues;
import com.github.adouggy.android.oauth.util.PackageHashUtil;
import com.github.adouggy.android.oauth.util.TwitterUtil;

import java.util.Arrays;
import java.util.List;

import twitter4j.auth.RequestToken;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private CallbackManager callbackManager;

    public static String CALLBACK_URL="myapp://twitter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        setContentView(R.layout.activity_main);

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
        final String OAUTH_URL = "https://api.instagram.com/oauth/authorize";
        String REDIRECT_URI = "http://115.28.235.57/oauth/callback?type=";
        final String uri = REDIRECT_URI + "instagram";
        Button btnLoginInstagram = (Button) findViewById(R.id.btn_login_instagram);
        btnLoginInstagram.setOnClickListener(new View.OnClickListener() {
            Dialog auth_dialog;

            @Override
            public void onClick(View v) {
                auth_dialog = new Dialog(MainActivity.this);
                auth_dialog.setContentView(R.layout.auth_dialog);
                WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                String oauthUrl = OAUTH_URL + "?redirect_uri=" + uri + "&response_type=code&client_id=4acf266c82374ba38fe29c2c45121107" + "&scope=basic";
                Log.i(TAG, "instagram oauthUrl:" + oauthUrl);
                web.loadUrl(oauthUrl);
                web.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;
                    Intent resultIntent = new Intent();

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    String authCode;

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);


                    }
                });
                auth_dialog.show();
                auth_dialog.setTitle("登录Instagram");
                auth_dialog.setCancelable(true);
            }
        });

        Button btnLoginTwitter = (Button) findViewById(R.id.btn_login_twitter);
        btnLoginTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (!sharedPreferences.getBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
                {
                    new TwitterAuthenticateTask().execute();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, TwitterActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
            startActivity(intent);
        }

        @Override
        protected RequestToken doInBackground(String... params) {
            return TwitterUtil.getInstance().getRequestToken();
        }
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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void onResume(){
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
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
