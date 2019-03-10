package com.alexmcbride.android.seismologyapp.models;

import com.google.common.collect.Lists;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRssReader {
    private static final SimpleDateFormat PUB_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

    public List<Earthquake> parse(String url) throws IOException, XmlPullParserException, ParseException {
        URLConnection connection = new URL(url).openConnection();
        try (InputStream source = connection.getInputStream()) {
            return parseXml(source);
        }
    }

    private List<Earthquake> parseXml(InputStream source) throws XmlPullParserException, IOException, ParseException {
        List<Earthquake> earthquakes = Lists.newArrayList();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(source, null);

        int eventType = parser.getEventType();
        Earthquake earthquake = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if (name.equalsIgnoreCase("item")) {
                    earthquake = new Earthquake();
                }

                if (earthquake != null) {
                    getEarthquakeElement(parser, earthquake, name);
                }
            }

            if (eventType == XmlPullParser.END_TAG) {
                if (parser.getName().equalsIgnoreCase("item") && earthquake != null) {
                    earthquakes.add(earthquake);
                }
            }

            eventType = parser.next();
        }
        return earthquakes;
    }

    private void getEarthquakeElement(XmlPullParser parser, Earthquake earthquake, String name) throws IOException, XmlPullParserException, ParseException {
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
