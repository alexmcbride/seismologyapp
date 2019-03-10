package com.alexmcbride.android.seismologyapp.models;

import com.google.common.collect.Lists;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class EarthquakeRssReader {
    private static final SimpleDateFormat PUB_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

    public List<Earthquake> parse(String url) throws IOException, XmlPullParserException, ParseException {
        String xml = loadXml(url);
        return parseXml(xml);
    }

    private String loadXml(String url) throws IOException {
        URLConnection connection = new URL(url).openConnection();
        try (InputStream source = connection.getInputStream();
             Scanner in = new Scanner(source)) {
            in.useDelimiter("\\Z"); // Cause scanner to read to the end of the input
            return in.next();
        }
    }

    private List<Earthquake> parseXml(String xml) throws XmlPullParserException, IOException, ParseException {
        List<Earthquake> earthquakes = Lists.newArrayList();

        try (StringReader reader = new StringReader(xml)) {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(reader);

            int eventType = parser.getEventType();
            Earthquake earthquake = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equalsIgnoreCase("item")) {
                        earthquake = new Earthquake();
                    }

                    if (earthquake != null) {
                        if (name.equalsIgnoreCase("title")) {
                            earthquake.setTitle(parser.nextText());
                        } else if (name.equalsIgnoreCase("description")) {
                            earthquake.setDescription(parser.nextText());
                        } else if (name.equalsIgnoreCase("link")) {
                            earthquake.setLink(parser.nextText());
                        } else if (name.equalsIgnoreCase("category")) {
                            earthquake.setCategory(parser.nextText());
                        } else if (name.equalsIgnoreCase("geo:lat")) {
                            earthquake.setLat(Double.parseDouble(parser.nextText()));
                        } else if (name.equalsIgnoreCase("geo:long")) {
                            earthquake.setLon(Double.parseDouble(parser.nextText()));
                        } else if (name.equalsIgnoreCase("pubDate")) {
                            earthquake.setPubDate(PUB_DATE_FORMAT.parse(parser.nextText()));
                        }
                    }
                }

                if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equalsIgnoreCase("item") && earthquake != null) {
                        earthquakes.add(earthquake);
                    }
                }

                eventType = parser.next();
            }
        }

        return earthquakes;
    }
}
