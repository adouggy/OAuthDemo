package com.github.adouggy.android.oauth.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liyazi on 15/11/26.
 */
public class PackageHashUtil {
    public static String getHash(Context context) {
        StringBuilder sb = new StringBuilder();
        try {
            //paste Your package name at the first parameter
            PackageInfo info = context.getPackageManager().getPackageInfo("com.weeeye.desafinado",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                sb.append(sign + "\n");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
