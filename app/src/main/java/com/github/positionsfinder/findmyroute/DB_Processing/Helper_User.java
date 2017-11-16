package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.github.positionsfinder.findmyroute.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Paolo on 14.11.2017.
 */

/**
 * This is a helper class designed to take care of various operations related to the application's
 * user and their interaction with the Database. It will hide most of the code needed for the async
 * communication with the DB.
 */
public class Helper_User {

    private static boolean isUserLoggedIn;

    public static boolean loginUser(Context cntx, String username, String password){

        if(cntx != null && username != null && password != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user",username);
            userMap.put("password",generateHashedPassword(password));

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx) {
                @Override
                protected void onPostPostExecute(ArrayList<HashMap<String, Object>> result) {
                    getUserLoggedIn(result);
                }
            };

            asyncHttpReq.callHttpMethod(R.string.http_method_LoginUser,userMap);

            try {
                // Wait 2 seconds for the login task to succeed
                asyncHttpReq.get(3000, TimeUnit.MILLISECONDS);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // We will come here if we did not succeed in logging the user in
                isUserLoggedIn = false;
                e.printStackTrace();
            }
        }
        return isUserLoggedIn;
    }

    public static String generateHashedPassword(String password) {

        MessageDigest digest = null;
        String pwSha256Base64 = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {}

        if (digest != null) {
            byte[] hash = digest.digest((password.getBytes(StandardCharsets.UTF_8)));
            pwSha256Base64 = android.util.Base64.encodeToString(hash, Base64.NO_WRAP | Base64.URL_SAFE).toString();

        }

        return pwSha256Base64;
    }

    public static void getUserLoggedIn(ArrayList<HashMap<String, Object>> result){

        if(result != null && result.get(0) != null){
            HashMap<String, Object> resMap = result.get(0);
            if(resMap.containsKey("SUCCESSS") && resMap.get("SUCCESS") == 1){
                isUserLoggedIn = true;
            } else {
                isUserLoggedIn = false;
            }
        } else {
            isUserLoggedIn = false;
        }

    }

}
