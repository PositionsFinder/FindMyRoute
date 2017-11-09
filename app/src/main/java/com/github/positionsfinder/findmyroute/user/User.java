package com.github.positionsfinder.findmyroute.user;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;

import com.github.positionsfinder.findmyroute.R;
import com.github.positionsfinder.findmyroute.Settings;
import com.github.positionsfinder.findmyroute.database.JDBC_Connector;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mosaab on 11/9/2017.
 */

public class User {

    // The instance of this class
    private static User instance;
    private Settings settingsInstance = Settings.getInstance();
    private JDBC_Connector jdbc = JDBC_Connector.getInstance();

    // No new objects can be instantiated
    private User() {
    }

    // If existent returns the instance - otherwise a new instance (object) will be generated
    public static User getInstance() {
        if (User.instance == null) {
            User.instance = new User();
        }
        return User.instance;
    }


    /**
     * Saves the new credentials to the prefs and database
     *
     * @param userName
     * @param password
     */
    public void createUser(Context cntx, String userName, String password) {

        Resources res = cntx.getResources();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
        }
        ;

        String pwSha256Base64 = "";
        if (digest != null) {
            byte[] hash = digest.digest((password.getBytes(StandardCharsets.UTF_8)));
            pwSha256Base64 = android.util.Base64.encodeToString(hash, Base64.NO_WRAP | Base64.URL_SAFE).toString();

        }

        if (pwSha256Base64 != null) {

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put(res.getString(R.string.prefs_username_key), userName);
            userMap.put(res.getString(R.string.prefs_password_key), pwSha256Base64);
            settingsInstance.saveSettings(cntx, userMap);

            if (!setUserActive(cntx)) {
                jdbc.insertSTMNT(new String[]{res.getString(R.string.usertable_column_user), res.getString(R.string.usertable_column_password)},
                        new String[]{userName, pwSha256Base64}, "USERS");
            }

        }
    }

    public boolean setUserActive(Context cntx) {

        Resources res = cntx.getResources();
        String userName = settingsInstance.getString(res.getString(R.string.prefs_username_key));
        String userPassword = settingsInstance.getString(res.getString(R.string.prefs_password_key));

        HashMap<String, Object> userMap = new HashMap<String, Object>();
        userMap.put(res.getString(R.string.usertable_column_user), userName);
        userMap.put(res.getString(R.string.usertable_column_password), userPassword);

        ArrayList<HashMap<String, Object>> result
                = jdbc.selectSTMNT(new String[]{res.getString(R.string.usertable_column_user)}, res.getString(R.string.usertable_name), userMap);

        if (result != null) {
            // We only expect one dataset as the user is unique and therefor also the combination w/ password
            if (result.size() >= 1 && result.get(0).get("USERNAME").equals(userName)) {
                // the result contains the username so the login was successful
                HashMap<String, Object> updateMap = new HashMap<>();
                updateMap.put(res.getString(R.string.usertable_column_active), true);
                HashMap<String, Object> whereClauseMap = new HashMap<>();
                whereClauseMap.put(res.getString(R.string.usertable_column_user), userName);

                jdbc.updateSTMNT(updateMap, res.getString(R.string.usertable_name), whereClauseMap);

                return true;
            } else {
                // Username/password combination not found
                return false;
            }
        } else {
            // We did not get a proper response
            return false;
        }
    }


}
