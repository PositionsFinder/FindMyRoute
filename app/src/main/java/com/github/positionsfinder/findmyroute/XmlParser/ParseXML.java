package com.github.positionsfinder.findmyroute.XmlParser;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * This Class Pars Google XML-URL directions API
 * BSP= https://maps.googleapis.com/maps/api/directions/xml?origin=47,11&destination=48.2,11.2&mode=walking
 *
 * @return Map with Lat and Lng (String)
 * This Class use Java 8 , You may need to Synchronize the Project
 * Dummy version - Just for Test.
 */
public class ParseXML {


    private Document document;
    private final XPathFactory readFile = readFile();


    @RequiresApi(api = Build.VERSION_CODES.N)
    public ParseXML() throws JDOMException, IOException {
        searchInFile();
    }

    private synchronized XPathFactory readFile() throws MalformedURLException, JDOMException, IOException {
        SAXBuilder jdomBuilder = new SAXBuilder();
        document = jdomBuilder.build(new URL("https://maps.googleapis.com/maps/api/directions/xml?origin=47,11&destination=48.2,11.2&mode=walking"));
        return org.jdom2.xpath.XPathFactory.instance();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void searchInFile() {
        final Map<String, String> allStyleName = new HashMap<>();
        XPathExpression<Element> styleName = readFile.compile("//route/leg/step/start_location", Filters.element());
        styleName.evaluate(document).stream().forEach(x -> {
            allStyleName.put(x.getChild("lat").getValue(), x.getChild("lng").getValue());
        });

        //For Debug.
        allStyleName.forEach((k, v) -> System.out.println("Lat : " + k + " Lng : " + v));
    }

}
