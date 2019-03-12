package com.alexmcbride.android.seismologyapp.model;

import java.util.Comparator;

/*
 * Comparator for sorting earthquakes by distance from current lon/lat using the Haversine formula.
 */
class EarthquakeDistanceComparator implements Comparator<Earthquake> {
    private static final double R = 6372.8; // In kilometers
    private final double mCurrentLat;
    private final double mCurrentLon;
    private final boolean mAscending;

    EarthquakeDistanceComparator(double currentLat, double currentLon, boolean ascending) {
        mCurrentLat = currentLat;
        mCurrentLon = currentLon;
        mAscending = ascending;
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
    public int compare(Earthquake a, Earthquake b) {
        double distanceA = haversine(mCurrentLat, mCurrentLon, a.getLat(), a.getLon());
        double distanceB = haversine(mCurrentLat, mCurrentLon, b.getLat(), b.getLon());
        return mAscending ? Double.compare(distanceA, distanceB) : Double.compare(distanceB, distanceA);
    }
}

