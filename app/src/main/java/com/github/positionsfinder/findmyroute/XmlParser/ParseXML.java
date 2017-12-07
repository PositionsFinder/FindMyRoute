package com.github.positionsfinder.findmyroute.XmlParser;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * This Class Pars Google XML-URL directions API
 * BSP= https://maps.googleapis.com/maps/api/directions/xml?origin=47,11&destination=48.2,11.2&mode=walking
 *
 * @return ArrayList containing all routing points as LatLng
 */
public class ParseXML extends AsyncTask<String, Void, ArrayList<LatLng>> {

    private GoogleMap map;

    public ParseXML(GoogleMap googleMap) {
        map = googleMap;
    }

    private ArrayList<LatLng> getLatLngList(byte[] httpRespUTF8) {

        SAXBuilder jdomBuilder = new SAXBuilder();
        InputStream inStream = new ByteArrayInputStream(httpRespUTF8);

        Document document = null;
        ArrayList<LatLng> latLngs = null;
        try {
            document = jdomBuilder.build(inStream);
            latLngs = searchInFile(org.jdom2.xpath.XPathFactory.instance(), document);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLngs;
    }


    private ArrayList<LatLng> searchInFile(XPathFactory readFile, Document document) {

        ArrayList<LatLng> latLngs = new ArrayList<>();

        XPathExpression<Element> startLocs = readFile.compile("//route/leg/step/start_location", Filters.element());
        XPathExpression<Element> endLocs = readFile.compile("//route/leg/step/end_location", Filters.element());

        for (int i = 0; i < startLocs.evaluate(document).size(); i++) {

            Element eStart = startLocs.evaluate(document).get(i);
            Element eEnd = endLocs.evaluate(document).get(i);
            String sLat = eStart.getChild("lat").getValue();
            String sLng = eStart.getChild("lng").getValue();
            String eLat = eEnd.getChild("lat").getValue();
            String eLng = eEnd.getChild("lng").getValue();

            try {
                latLngs.add(new LatLng(Double.parseDouble(sLat), Double.parseDouble(sLng)));
                latLngs.add(new LatLng(Double.parseDouble(eLat), Double.parseDouble(eLng)));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        return latLngs;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(String... strings) {

        ArrayList<LatLng> posList = new ArrayList<>();

        try {
            // The first submitted string is the url
            String link = strings[0];
            // Make an URL object from our provided link
            URL url = new URL(link);

            // To be able to notice if the VPN is connected we set a timeout as http parameter
            final HttpParams httpParams = new BasicHttpParams();

            // Generate a HTTP client with the HTTP parameters provided
            final HttpClient httpClient = new DefaultHttpClient(httpParams);
            // Generate the HTTP GET request
            final HttpGet httpRequest = new HttpGet();
            httpRequest.setURI(new URI(link));
            // Fire our request
            HttpResponse response = httpClient.execute(httpRequest);

            // The glued HTTP-Response as string.
            String strNonProcessed = buildString(response);

            if (!strNonProcessed.equals("ERROR")) {
                posList = getLatLngList(strNonProcessed.getBytes("UTF-8"));
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return posList;
    }

    /**
     * Builds a single String from the provided HTTP response.
     *
     * @return The raw JSON String
     */
    private String buildString(HttpResponse response) {

        try {
            BufferedReader inReader = new BufferedReader(new
                    InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuffer buffer = new StringBuffer("");

            int i = 0;
            while ((line = inReader.readLine()) != null) {
                buffer.append(line);
            }

            return buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR";
        }

    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> result) {
        //map.clear();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(25);
        polylineOptions.isClickable();
        polylineOptions.add(result.toArray(new LatLng[result.size()]));

        if (result != null && !result.isEmpty()) {
            LatLng item = result.get(result.size() - 1);
            map.addMarker(new MarkerOptions().position(item).title("Ziel!"));
        }


        map.addPolyline(polylineOptions);

    }
}