package com.alexmcbride.android.seismologyapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Some date utility methods, yay for dates.
 */
@SuppressWarnings("WeakerAccess")
public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static String formatDateTime(Date date) {
        return formatDate(date) + " - " + formatTime(date);
    }

    public static String formatIso8601(Date date) {
        return ISO_8601_FORMAT.format(date);
    }

    public static Date parseIso8601(String s) {
        try {
            return ISO_8601_FORMAT.parse(s);
        } catch (ParseException e) {
            // Throw this so we don't need to deal with a hundred checked exception fixes.
            throw new RuntimeException(e);
        }
    }
}
