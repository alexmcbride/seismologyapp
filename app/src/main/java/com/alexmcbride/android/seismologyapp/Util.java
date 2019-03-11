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

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static String formatPretty(Date date) {
        return formatDate(date) + " - " + formatTime(date);
    }
}
