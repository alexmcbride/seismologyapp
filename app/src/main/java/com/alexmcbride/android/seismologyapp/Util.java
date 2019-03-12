package com.alexmcbride.android.seismologyapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Some utility methods.
 */
class Util {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    static String formatPretty(Date date) {
        return formatDate(date) + " - " + formatTime(date);
    }
}
