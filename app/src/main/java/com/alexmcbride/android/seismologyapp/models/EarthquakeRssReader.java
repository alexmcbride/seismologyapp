package com.alexmcbride.android.seismologyapp.models;

import com.google.common.collect.Lists;

import java.util.List;

public class EarthquakeRssReader {
    public static List<Earthquake> parse(String url) {
        List<Earthquake> earthquakes = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Earthquake earthquake = new Earthquake();
            earthquake.setTitle("Earthquake " + (i + 1));
            earthquakes.add(earthquake);
        }
        return earthquakes;
    }
}
