package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;

import com.github.positionsfinder.findmyroute.R;
import com.github.positionsfinder.findmyroute.XmlParser.ParseXML;
import com.google.android.gms.maps.GoogleMap;
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

    /**
     * This method will fetch the friend's latest position
     * @param cntx The application's context to get the resources from
     * @param friendsName The friend's name to catch the position from
     * @return TODO: Return only LatLng, or a HashMap with LatLng + Timestamp (+ maybe other infos)??
     */
    public static HashMap<String, Object> getFriendsLatestPosition(Context cntx, String friendsName){

        HashMap<String, Object> positionMap = null;

        AsyncHttpReq asyncHttpReq = new AsyncHttpReq(cntx) {
            @Override
            protected void onPostPostExecute(Object result) {

            }
        };
        HashMap<String, Object> friendMap = new HashMap<>();
        friendMap.put("friendsName",friendsName);

        positionMap = (HashMap<String, Object>) asyncHttpReq.callHttpMethod(R.string.http_method_getFriendsLatestPosition, friendMap);
        // TODO: Either process the map right here, or let it get processed by the calling Activity?!
        return positionMap;
    }


    /**
     * Updates the user's position, so other users can fetch it.
     * @param cntx The application's context to get the resources from
     * @param username The user's name
     * @param latitude The user's current latitude
     * @param longitude The user's current longitude
     * @return True if successful, false otherwise
     */
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
                protected void onPostPostExecute(Object result) {
                    // We use the return of callHttpMethod. This callback is not used here.
                }
            };
            status = (boolean) asyncHttpReq.callHttpMethod(R.string.http_method_updateUsersPosition, posMap);
        }

        return status;
    }

    public static void drawDirectionsLatLng(GoogleMap map, LatLng start, LatLng dest) {

        if (start != null && dest != null) {

            // The start of our route
            String strStart = "origin=" + start.latitude + "," + start.longitude;

            // The destination of our route
            String strDest = "destination=" + dest.latitude + "," + dest.longitude;

            // Obsolete parameter
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String params = strStart + "&" + strDest + "&" + sensor;

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/directions/xml?mode=walking&" + params;// +"&key=AIzaSyBJdqWTAf4e5Ty2590JjFF39gg-ey4YVoE";
            // Build the Map needed to call our asyncMethod

            System.out.println("***URL:"+url);
            ParseXML xmlParser = new ParseXML(map);
            xmlParser.execute(url);
        }
    }

    public static ArrayList<LatLng> getLatLngFromJSON(Object result){

        System.out.println(result);
        return new ArrayList<>();

    }

}
