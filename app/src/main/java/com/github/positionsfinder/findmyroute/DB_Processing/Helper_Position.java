package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;

import com.github.positionsfinder.findmyroute.R;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This is a helper class designed to take care of various operations related to the application's
 * user's position and their interaction with the Database.
 */
public class Helper_Position {

    /**
     * Parses a given timestamp and generates a Date object from it.
     *
     * @param timeStamp The timestamp string returned by the database
     * @return A Date object with the parsed timeStamp or the current time if parsing failed.
     */
    public static Date parseTimeStamp(String timeStamp) {

        Date d = new Date();
        // This pattern will be returned by the DB
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            d = sdf.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // DBG: System.out.println(d);
        return d;
    }


    public static boolean updateUsersPosition(Context cntx, String username, double latitude, double longitude) {

        boolean status = false;
        if (cntx != null && username != null) {

            // Build the Map needed to call our asyncMethod
            HashMap<String, Object> posMap = new HashMap();
            posMap.put("user", username);
            posMap.put("lat", latitude);
            posMap.put("lon", longitude);

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx) {
                @Override
                protected void onPostPostExecute(ArrayList<HashMap<String, Object>> result) {
                    // We use the return of callHttpMethod. This callback is not used here.
                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_updateUsersPosition, posMap);
        }

        return status;
    }

    public static Object getDirectionsLatLng(Context cntx, LatLng start, LatLng dest) {

        //ArrayList<LatLng> status = null;

        Object status = null;
        if (cntx != null && start != null && dest != null) {

            // The start of our route
            String strStart = "origin=" + start.latitude + "," + start.longitude;

            // The destination of our route
            String strDest = "destination=" + dest.latitude + "," + dest.longitude;

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String params = strStart + "&" + strDest + "&" + sensor;

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/json?mode=walking&" + params;

            // Build the Map needed to call our asyncMethod

            HashMap<String, Object> paramMap = new HashMap();
            paramMap.put("URL", url);

            AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx) {
                @Override
                protected void onPostPostExecute(ArrayList<HashMap<String, Object>> result) {
                    // We use the return of callHttpMethod. This callback is not used here.
                }
            };

            status =  asyncHttpReq.callHttpMethod(R.string.http_method_getDirectionsLatLng, paramMap);

        }

    return status;
    }

    public static ArrayList<LatLng> getLatLngFromJSON(ArrayList<HashMap<String, Object>> result){

        System.out.println(result);
        return new ArrayList<>();

    }

}
