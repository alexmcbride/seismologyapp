package com.alexmcbride.android.seismologyapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Some utility methods.
 */
public class Util {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final double R = 6372.8; // In kilometers

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static String formatPretty(Date date) {
        return formatDate(date) + " " + formatTime(date);
    }

    public static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase();
    }

    /*
     * Get distance between two locations using Haversine formula. Haversine doesn't account for
     * curvature of the earth, but is efficient and fine for small distances.
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
