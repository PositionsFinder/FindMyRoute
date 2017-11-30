package com.github.positionsfinder.findmyroute.XmlParser;

import android.widget.Toast;

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
        searchInFile();
    }

    private synchronized XPathFactory readFile() throws MalformedURLException, JDOMException, IOException {
        SAXBuilder jdomBuilder = new SAXBuilder();
        document = jdomBuilder.build(file);
        return XPathFactory.instance();
    }

    private void searchInFile() {
//        final Map<String, String> allStyleName = new HashMap<>();
        XPathExpression<Element> styleName = readFile.compile("//root/row", Filters.element());
//        styleName.evaluate(document).stream().forEach(x -> {
//            allStyleName.put(x.getChild("LAT").getValue(), x.getChild("LNG").getValue());
//        });
//        allStyleName.forEach((k, v) -> System.out.println("Lat : " + k + " Lng : " + v));
        for (int i = 0; i < styleName.evaluate(document).size(); i++) {
            Element element = styleName.evaluate(document).get(i);
            try {
                double lat = Double.parseDouble(element.getChild("LAT").getValue());
                double lng = Double.parseDouble(element.getChild("LNG").getValue());

                System.out.println(lat + " " + lng);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());

            }
        }

    }

    public ArrayList<String> getDirNames() {
        final ArrayList<String> dirNames = new ArrayList<>();
        XPathExpression<Element> styleName = readFile.compile("//root/row", Filters.element());
//        styleName.evaluate(document).stream().forEach(x -> {
//            allStyleName.put(x.getChild("LAT").getValue(), x.getChild("LNG").getValue());
//        });
//        allStyleName.forEach((k, v) -> System.out.println("Lat : " + k + " Lng : " + v));
        for (int i = 0; i < styleName.evaluate(document).size(); i++) {
            Element element = styleName.evaluate(document).get(i);
            try {
                dirNames.add(element.getChild("NAME").getValue());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }

        return dirNames;

    }

}
