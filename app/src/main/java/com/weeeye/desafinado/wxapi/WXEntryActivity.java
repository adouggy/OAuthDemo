package com.weeeye.desafinado.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.adouggy.android.oauth.activity.MainActivity;
import com.github.adouggy.android.oauth.util.ConstantValues;
import com.github.adouggy.android.oauth.util.SimpleJsonUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.weeeye.desafinado.R;


public class WXEntryActivity extends Activity  implements IWXAPIEventHandler {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        Log.i(MainActivity.TAG, "我启动了..");

        MainActivity.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MainActivity.api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i(MainActivity.TAG, "onReq回调了！");
    }

    @Override
    public void onResp(BaseResp sendResp) {
        Log.i(MainActivity.TAG, "onResp回调了！");
        switch (sendResp.errCode) {
            case BaseResp.ErrCode.ERR_OK://请求成功
                SendAuth.Resp resp = (SendAuth.Resp) sendResp;
                String code = resp.code;
                Log.i(MainActivity.TAG, "code:" + code);
                break;
        }
    }

//    private void login(String code) {
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        final String url = ConstantValues.API_URL + "/login?from=wechat&token=" + code;
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i(TAG, "api :" + response);
//                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
//                        mTokenTextView.setText(SimpleJsonUtil.getTicket(response));
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(TAG, "api error:" + error.getMessage());
//            }
//        });
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//        queue.start();
//    }

}
