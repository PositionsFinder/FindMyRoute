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
     * Log the user in and return true if successful.
     * @param cntx The application's context to get the resources from
     * @param username The user's name to log in
     * @param password The user's password
     * @return True if successful, false otherwise
     */
    public static boolean loginUser(Context cntx, String username, String password){

        boolean status = false;
        if(cntx != null && username != null && password != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user", username);
            userMap.put("password", generateHashedPassword(password));

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
                @Override
                protected void onPostPostExecute(Object result) {

                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_LoginUser,userMap);
        }

        return status;
    }

    public static boolean checkCodeIfValid(Context cntx, String activationCode) {

        boolean status = false;


        // Build the Map needed to call our asyncMethod
        HashMap<String, Object> userMap = new HashMap();
        userMap.put("invCode", activationCode);


        AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx) {
            @Override
            protected void onPostPostExecute(Object result) {

            }
        };
        status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_checkCodeIfValid, userMap);
        System.out.println("Status : " + status);

        return status;
    }

    public static boolean testDBConnection(Context cntx) {

        boolean status = false;
        if(cntx != null){

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
                @Override
                protected void onPostPostExecute(Object result) {}
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_dbConnectionTest, null);
        }

        return status;
    }

    /**
     *  This method will activate the user, with a new password if the submitted activation code is valid.
     * @param cntx The application's context to get the resources from
     * @param username The user's name to activate
     * @param password The user's password.
     * @return True is successful, false otherwise
     */
    public static boolean activateUser(Context cntx, String username, String password, String activationCode){

        boolean status = false;
        if(cntx != null && username != null && password != null){

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> userMap = new HashMap();
            userMap.put("user", username);
            userMap.put("password", generateHashedPassword(password));
            userMap.put("actCode", activationCode);

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
     * Should be called on application's startup, or after login.
     * @param cntx The application's context to get the resources from
     * @param username The user's name to set online
     * @return True if successful, false otherwise
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

    /**
     * Sets the user offline on app closing
     * @param cntx The application's context to get the resources from
     * @param username The user's name to set offline
     * @return True if successful, false otherwise
     */
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

    /**
     * This method returns a list of users reported to be online.
     * @param cntx The application's context to get the resources from
     * @return A list containing all active users
     */
    public static ArrayList<String> getOnlineUsers(Context cntx) {

        ArrayList<String> usersOnline = new ArrayList<>();

        AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx){
            @Override
            protected void onPostPostExecute(Object result) {
                // We use the return of callHttpMethod. This callback is not used here.
            }
        };

        usersOnline = (ArrayList<String>) asyncHttpReq.callHttpMethod(R.string.http_method_getOnlineUsers, null);

        return usersOnline;
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

    /**
     * Generic method to check if the submitted List contains keywords indicating a successful
     * execution of a DB-call happened before.
     * @param result The result of the DB-call fired in AsyncHttpReq
     * @return True if the result indicates a success, false otherwise
     */
    public static boolean interpretStatus(ArrayList<HashMap<String, Object>> result){

        boolean status;

        if(result != null && result.get(0) != null){
            HashMap<String, Object> respMap = result.get(0);
            if(respMap.containsKey("STATUS") && (respMap.get("STATUS").equals("1") || respMap.get("STATUS").equals("true"))){
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
