package com.alexmcbride.android.seismologyapp.models;

import java.util.Comparator;

/*
 * Comparator for sorting earthquakes by distance from current lon/lat using the Haversine formula.
 */
class EarthquakeDistanceComparator implements Comparator<Earthquake> {
    private static final double R = 6372.8; // In kilometers
    private final double mCurrentLat;
    private final double mCurrentLon;

    EarthquakeDistanceComparator(double currentLat, double currentLon) {
        mCurrentLat = currentLat;
        mCurrentLon = currentLon;
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    @Override
    public int compare(Earthquake earthquakeA, Earthquake earthquakeB) {
        double distanceA = haversine(mCurrentLat, mCurrentLon, earthquakeA.getLat(), earthquakeA.getLon());
        double distanceB = haversine(mCurrentLat, mCurrentLon, earthquakeB.getLat(), earthquakeB.getLon());
        return Double.compare(distanceA, distanceB);
    }
}

