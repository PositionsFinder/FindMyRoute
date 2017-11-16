package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Paolo on 14.11.2017.
 */

/**
 * This is a helper class designed to take care of various operations related to the application's
 * user and their interaction with the Database.
 */
public class Helper_User {

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

}
