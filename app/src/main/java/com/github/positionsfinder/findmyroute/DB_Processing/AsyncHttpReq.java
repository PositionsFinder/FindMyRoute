package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.github.positionsfinder.findmyroute.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Helper_User on 11.11.2017.
 *
 */

public abstract class AsyncHttpReq extends AsyncTask<String,Void,Object> {

    private Helper_User hUser;
    private Helper_Position hPos;
    /**
     * The String containing the BaseURL from which the DB-Server can be accessed
     */
    private String baseUrl;
    private Context cntx;

    /**
     * Keeps a reference to the calling Activity to access (1) their Resources and (2) talk back
     * @param cntx The application's context to be able to access the Resources
     */
    public AsyncHttpReq(Context cntx){

        Resources res = cntx.getResources();
        this.cntx = cntx;
        baseUrl = res.getString(R.string.db_baseUrl);
    }

    /**
     * This method will generate the URL with the needed parameters to get results from the DB.
     * @param methodToCall The R.string.xxxx of the method we want to use
     * @param values A HashMap containing all parameters needed for the provided methodToCall
     * @return The status of the called Operation (boolean). In case of getFriendsLatestPosition the
     * response will be a HashMap<String, Object> containing the username, lat and lon.
     */
    public Object callHttpMethod(int methodToCall, HashMap<String, Object> values){

        // Access the Resources to get the >action< String (res.getString(xxxx))
        Resources res = cntx.getResources();
        String params = "";

        switch(methodToCall){
            case R.string.http_method_ActivateUser:

                baseUrl += "user.php";
                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&password="+password+"&invCode="+invCode;
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_LoginUser:

                baseUrl += "user.php";
                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&password="+password+"&invCode="+invCode;
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_setUserOnline:

                baseUrl += "user.php";
                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&password="+password+"&invCode="+invCode;
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_updateUsersPosition:

                baseUrl += "pos.php";
                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&lat="+lat+"&lon="+lon
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_getFriendsLatestPosition:

                baseUrl += "pos.php";
                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&friend="+friendsname
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_getDirectionsLatLng:

                baseUrl = (String) values.get("URL");
                params = "";
                //DBG: System.out.println(params);
                break;

            default:
                // return generateSimpleErrorResponse(); unfortunately we cannot return stuff here
                break;
        }

        // Concat the baseURL with our params to get the full URL
        String finalUrl = baseUrl + params;
        // DBG: System.out.println(finalUrl);

        // Execute the doInBackground method and save the retruned Object into our status Object
        Object status = null;
        try {
            status = this.execute(finalUrl, res.getString(methodToCall)).get(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * This method will be running in the background (non-UI-Thread) and will call onPostExecute()
     * when finished.
     * @param strings The URL we want to call
     * @return An ArrayList<HashMap<String, Object>> containing the HTTP response
     */
    @Override
    protected Object doInBackground(String... strings) {

        boolean status = false;
        HashMap<String, Object> friendsPositionMap;

        try {

            // The first submitted string is the url
            String link = strings[0];
            // The second string is the method called
            String methodCalled = strings[1];
            // Make an URL object from our provided link
            URL url = new URL(link);

            // To be able to notice if the VPN is connected we set a timeout as http parameter
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 2000); // 2 sec

            // Generate a HTTP client with the HTTP parameters provided
            final HttpClient httpClient = new DefaultHttpClient(httpParams);
            // Generate the HTTP GET request
            final HttpGet httpRequest = new HttpGet();
            httpRequest.setURI(new URI(link));
            // Fire our request
            HttpResponse response = httpClient.execute(httpRequest);

            // Build a single string from the HTTP response
            String strNonProcessed = buildString(response);
            ArrayList<HashMap<String, Object>> responseList;

            switch(methodCalled){
                case "activateUser":
                    responseList = processResponse(strNonProcessed,false);
                    status = Helper_User.interpretStatus(responseList);
                    break;
                case "loginUser":
                    responseList = processResponse(strNonProcessed,false);
                    status = Helper_User.interpretStatus(responseList);
                    break;
                case "setOnline":
                    responseList = processResponse(strNonProcessed,false);
                    status = Helper_User.interpretStatus(responseList);
                    break;
                case "insertPosition":
                    responseList = processResponse(strNonProcessed,false);
                    status = Helper_User.interpretStatus(responseList);
                    break;
                case "getFriendsLatestPosition":
                    responseList = processResponse(strNonProcessed,false);
                    if(responseList != null && responseList.get(0) != null){
                        friendsPositionMap = responseList.get(0);
                        return friendsPositionMap;
                    }
                    break;
                case "getDirectionsLatLng":
                    responseList = processResponse(strNonProcessed,true);
                    return null;
                default:
                    break;
            }

        } catch (IOException | URISyntaxException e) {
            // IF IOException occurs => Indicator for a timeout
            e.printStackTrace();

            return generateSimpleErrorResponse();
        }
        return status;
    }

    /**
     * Builds a single String from the provided HTTP response.
     * @return The raw JSON String
     */
    private String buildString(HttpResponse response){

        try {
            BufferedReader inReader = new BufferedReader(new
                    InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuffer buffer = new StringBuffer("");

            int i = 0;
            while ((line = inReader.readLine()) != null) {
                buffer.append(line);
                i += 1;
                //break; // Has only one line but that's the standard approach
            }

            return buffer.toString();

        } catch (IOException e){
            return "ERROR";
        }
    }

    /**
     * This method processes the provided response String. It will detect if it contains a JSONArray
     * or a single JSONObject and returns the JSON structure in form of an ArrayList-HashMap
     * construct.
     * @param response
     * @return
     */
    private ArrayList<HashMap<String, Object>> processResponse(String response, boolean directions){

        ArrayList<HashMap<String, Object>> respList = new ArrayList<HashMap<String, Object>>();

        if(!response.equals("ERROR") && !response.equals("[]")){

            // As default we expect a simple object
            Object jsonTypeIndicator = new JSONObject();

            try {
                jsonTypeIndicator = new JSONTokener(response).nextValue(); // JSONArray or JSONObject
            } catch (JSONException e) { e.printStackTrace(); }

            try {
                // try to parse our result
                if(jsonTypeIndicator instanceof JSONArray) { // Case JSONArray:
                    JSONArray jObj = null;

                    jObj = new JSONArray(response);// [{"USERNAME":"admin","PASSWORD":"admin_password"},{"USERNAME":"mosaab","PASSWORD":"mosaabs_password"}...

                    for(int i = 0; i < jObj.length(); i++){

                        JSONObject object = jObj.getJSONObject(i); // {"USERNAME":"admin","PASSWORD":"admin_password"}
                        HashMap<String, Object> curMap = new HashMap<>();
                        Iterator<String> iter = object.keys(); // eg. iter = USERNAME,PASSWORD

                        while(iter.hasNext()) {
                            String key = iter.next(); // eg. key = "USERNAME"
                            curMap.put(key,object.get(key)); // eg. value("USERNAME") = "admin"
                        }
                        respList.add(curMap);
                    }
                } else if (jsonTypeIndicator instanceof JSONObject) { // Case JSONObject:


                    if(directions) {
                        // TODO: JDOM parsing hier rein
                        System.out.println("***** " + response);

                    } else {
                        Log.e("*** ERR:"," The JSON returned does not contain an array. \n" +
                                "We currently have no method to handle this situation..");

                    }
                }

            } catch (JSONException e) {
                Log.e("*** JSON Parser", "Error parsing " + e.toString());
            }
        } else {
            return generateSimpleErrorResponse();
        }

    return respList;
    }

    private ArrayList<HashMap<String, Object>> generateSimpleErrorResponse(){

        ArrayList<HashMap<String, Object>> errorList = new ArrayList<>();
        HashMap<String,Object> errorMap = new HashMap<>();
        errorMap.put("STATUS","ERROR");
        errorList.add(errorMap);
        return errorList;
    }

    /**
     * This method will be called when doInBackground finished
     * @param result
     */
    protected void onPostExecute(Object result){
        // TODO: onPostPostExecute(Object);
    }

    /**
     * This abstract method can be used to get the response of the async process as callback.
     * @param result
     */
    protected abstract void onPostPostExecute(ArrayList<HashMap<String, Object>> result);
}
