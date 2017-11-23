package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;
import android.util.Base64;

import com.github.positionsfinder.findmyroute.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is a helper class designed to take care of various operations related to the application's
 * user and their interaction with the Database. It will wrap most of the code needed for the async
 * communication with the DB.
 */
public class Helper_User {

    /**
     *
     * @param cntx
     * @param username
     * @param password
     * @return
     */
    public static boolean loginUser(Context cntx, String username, String password){

        boolean status = false;
        if(cntx != null && username != null && password != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user",username);
            userMap.put("password",generateHashedPassword(password));

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
                @Override
                protected void onPostPostExecute(Object result) {

                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_LoginUser,userMap);
        }

        return status;
    }

    /**
     *
     * @param cntx
     * @param username
     * @param password
     * @return
     */
    public static boolean activateUser(Context cntx, String username, String password, String activationCode){

        boolean status = false;
        if(cntx != null && username != null && password != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user",username);
            userMap.put("password",generateHashedPassword(password));
            userMap.put("actCode",activationCode);

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
                @Override
                protected void onPostPostExecute(Object result) {
                    // We use the return of callHttpMethod. This callback is not used here.
                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_LoginUser,userMap);
        }

        return status;
    }

    /**
     * Should be called in a loop every minute as long as the user uses the application
     * @param cntx
     * @param username
     * @return
     */
    public static boolean setUserOnline(Context cntx, String username){
    
        boolean status = false;
        if(cntx != null && username != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user",username);

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
                @Override
                protected void onPostPostExecute(Object result) {
                    // We use the return of callHttpMethod. This callback is not used here.
                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_LoginUser,userMap);
        }

        return status;
    }

    //Set user offline if app closing.
    public static boolean setUserOffline(Context cntx, String username){

        boolean status = false;
        if(cntx != null && username != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user",username);

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
                @Override
                protected void onPostPostExecute(Object result) {
                    // We use the return of callHttpMethod. This callback is not used here.
                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_setUserOffline,userMap);
        }

        return status;
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

    public static boolean interpretStatus(ArrayList<HashMap<String, Object>> result){

        boolean status;

        if(result != null && result.get(0) != null){
            HashMap<String, Object> respMap = result.get(0);
            if(respMap.containsKey("STATUS") && respMap.get("STATUS").equals("1")){
                status = true;
            } else {
                status = false;
            }
        } else {
            status = false;
        }
    return status;
    }


}
