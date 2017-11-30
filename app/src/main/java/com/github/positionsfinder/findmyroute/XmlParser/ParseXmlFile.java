package com.github.positionsfinder.findmyroute.XmlParser;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseXmlFile {


    private Document document;
    private XPathFactory readFile;
    private InputStream file;


    public ParseXmlFile(InputStream file) throws JDOMException, IOException {
        this.file = file;
        readFile = readFile();
    }

    private synchronized XPathFactory readFile() throws MalformedURLException, JDOMException, IOException {
        SAXBuilder jdomBuilder = new SAXBuilder();
        document = jdomBuilder.build(file);
        return XPathFactory.instance();
    }

    public ArrayList<String> getDirectionName() {
        final ArrayList<String> directionNames = new ArrayList<>();
        XPathExpression<Element> styleName = readFile.compile("//root/row", Filters.element());
        for (int i = 0; i < styleName.evaluate(document).size(); i++) {
            Element element = styleName.evaluate(document).get(i);
            try {
                directionNames.add(element.getChild("NAME").getValue());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }

        return directionNames;

    }

    public String getDescription(String name) {
     String description ="";
        XPathExpression<Element> styleName = readFile.compile("//root/row", Filters.element());
        for (int i = 0; i < styleName.evaluate(document).size(); i++) {
            Element element = styleName.evaluate(document).get(i);
            try {
                if(element.getChild("NAME").getValue().equals(name)){
                    description = element.getChild("Description").getValue();
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }

        return description;

    }

    public LatLng getLatLng(String name) {
        LatLng latlng = null;
        XPathExpression<Element> styleName = readFile.compile("//root/row", Filters.element());
        for (int i = 0; i < styleName.evaluate(document).size(); i++) {
            Element element = styleName.evaluate(document).get(i);
            try {
                if(element.getChild("NAME").getValue().equals(name)){
                    latlng  = new LatLng(Double.parseDouble(element.getChild("LAT").getValue().toString()),Double.parseDouble(element.getChild("LNG").getValue().toString()));
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        return latlng;
    }
}
