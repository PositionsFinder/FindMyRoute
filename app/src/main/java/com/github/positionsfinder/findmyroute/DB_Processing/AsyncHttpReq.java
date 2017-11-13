package com.github.positionsfinder.findmyroute.DB_Processing;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.github.positionsfinder.findmyroute.R; // TODO: Is this working

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by User on 11.11.2017.
 *
 * TODO: REMOVE AFTER IMPL
 * SAMPLE IMPLEMENTATION:
 * AsyncHttpReq asyncRequest = new AsyncHttpReq(getApplicationContext()) {
 *      @Override
 *      void onPostPostExecute(ArrayList<HashMap<String, Object>> result) {
 *          for(int i = 0; i < result.size(); i++){
 *              HashMap<String, Object> map = result.get(i);
 *              for(Map.Entry entry: map.entrySet()){
 *                  entry.getKey(); // The KEY
 *                  entry.getValue(); // The VALUE
 *               }
 *           }
 *       }
 *   };
 *
 * HashMap<String, Object> methodMap = new HashMap<>();
 * methodMap.put("user","paolo");
 * methodMap.put("password","paolo");
 *
 * // fire the async Request
 * asyncRequest.callHttpMethod(getApplicationContext(),R.string.http_method_LoginUser,methodMap);
 *
 *
 */

public abstract class AsyncHttpReq extends AsyncTask<String,Void,ArrayList<HashMap<String, Object>>> {

    /**
     * The String containing the BaseURL from which the DB-Server can be accessed
     */
    private String baseUrl;

    /**
     * Keeps a reference to the calling Activity to access (1) their Resources and (2) talk back
     * @param cntx The application's context to be able to access the Resources
     */
    public AsyncHttpReq(Context cntx){

        Resources res = cntx.getResources();
        baseUrl = res.getString(R.string.db_baseUrl);
    }

    /**
     * This method will generate the URL with the needed parameters to get results from the DB.
     * @param methodToCall The R.string.xxxx of the method we want to use
     * @param values A HashMap containing all parameters needed for the provided methodToCall
     */
    public void callHttpMethod(Context cntx, int methodToCall, HashMap<String, Object> values){

        // Access the Resources to get the >action< String (res.getString(xxxx))
        Resources res = cntx.getResources();
        String params = "";

        switch(methodToCall){
            case R.string.http_method_ActivateUser:

                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&password="+password+"&invCode="+invCode;

                    if(entry.getKey().equals("password")){
                        //String hashedPw = userInstance.generateHashedPassword(password);
                        // params += "&" + entry.getKey() + "=" + hashedPw;
                        params += "&" + entry.getKey() + "=" + entry.getValue(); // TODO: remove and use code above
                    } else {
                        params += "&" + entry.getKey() + "=" + entry.getValue();
                    }

                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_LoginUser:

                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&password="+password+"&invCode="+invCode;
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            case R.string.http_method_setUserOnline:

                params = "?action=" + res.getString(methodToCall);

                for(Map.Entry entry: values.entrySet()){ // +"&user="+userName+"&password="+password+"&invCode="+invCode;
                    params += "&" + entry.getKey() + "=" + entry.getValue();
                }
                //DBG: System.out.println(params);
                break;

            default:
                // return generateSimpleErrorResponse(); unfortunately we cannot return stuff here
                return;
        }

        // Concat the baseURL with our params to get the full URL
        String finalUrl = baseUrl + params;
        // DBG: System.out.println(finalUrl);

        // Execute the doInBackground method
        this.execute(finalUrl);
    }

    /**
     * This method will be running in the background (non-UI-Thread) and will call onPostExecute()
     * when finished.
     * @param strings The URL we want to call
     * @return An ArrayList<HashMap<String, Object>> containing the HTTP response
     */
    @Override
    protected ArrayList<HashMap<String, Object>> doInBackground(String... strings) {

        try {

            // We only submit one string which is our url
            String link = strings[0];
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
            return processResponse(strNonProcessed);

        } catch (IOException | URISyntaxException e) {
                // TODO: IF IOException occurs => Indikator for timeout
            e.printStackTrace();

            return generateSimpleErrorResponse();
        }
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

            while ((line = inReader.readLine()) != null) {
                buffer.append(line);
                break; // Has only one line but that's the standard approach
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
    private ArrayList<HashMap<String, Object>> processResponse(String response){

        ArrayList<HashMap<String, Object>> respList = new ArrayList<HashMap<String, Object>>();

        if(!response.equals("ERROR") && !response.equals("[]")){

            // As default we expect a simple object
            Object jsonTypeIndicator = new JSONObject();

            try {
                jsonTypeIndicator = new JSONTokener(response).nextValue(); // JSONArray or JSONObject
            } catch (JSONException e) { e.printStackTrace(); }

            JSONArray jObj = null;
            try {
                // try to parse our result
                if(jsonTypeIndicator instanceof JSONArray) { // Case JSONArray:

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

                    // TODO: I do not know if this case ever happens. Even the STATUS=true comes
                    // back as [{Status,"ERROR"}] and will be interpreted as array

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing " + e.toString());
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
    protected void onPostExecute(ArrayList<HashMap<String, Object>> result){
        onPostPostExecute(result);
    }

    /**
     * This abstract method will be called when doInBackground finished and will be executed on the
     * UI-Thread. It will provide the HTML response >result< in form of an ArrayList-HashMap
     * structure.
     * @param result The HTML response >result< in form of an ArrayList-HashMap structure
     */
    abstract void onPostPostExecute(ArrayList<HashMap<String, Object>> result);
}
