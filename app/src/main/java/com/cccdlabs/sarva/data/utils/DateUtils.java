package com.cccdlabs.sarva.data.utils;

import com.cccdlabs.sarva.data.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Static class providing helper methods to convert {@link Date} objects to and from SQL string
 * representations plus retrieving a Date with the current system time.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class DateUtils {

    /**
     * Returns a {@link Date} object with the current system timestamp.
     *
     * @return The Date with the current timestamp
     */
    public static Date currentTimestamp() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Formats a {@link Date} object to a String representation.
     *
     * @param date      The Date object to convert to String format
     * @param format    The format for the date output
     * @return          The formatted date string
     * @see             SimpleDateFormat for format types.
     */
    public static String formatDate(Date date, String format) {
        if (date == null || format == null) {
            return null;
        }

        long msInDay = TimeUnit.DAYS.toMillis(1);
        long dateMs = date.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    /**
     * Converts a given {@link Date} object to an SQL date string formatted "yyyy-mm-dd"
     * or datetime formatted "yyyy-mm-dd hh:mm:ss".
     * <p>
     * NOTE: If the resulting Date in milliseconds & ms/day is zero, then will return the
     * "yyyy-mm-dd" string. Otherwise the "yyyy-mm-dd hh:mm:ss" string will be returned.
     *
     * @param date  The Date object
     * @return      The formatted SQL date or datetime string
     */
    public static String dateToSqlString(Date date) {
        if (date == null) {
            return null;
        }

        long msInDay = TimeUnit.DAYS.toMillis(1);
        long dateMs = date.getTime();
        String format = dateMs % msInDay == 0
                ? Constants.DATE_SQL_FORMAT
                : Constants.DATETIME_SQL_FORMAT;
        return DateUtils.formatDate(date, format);
    }

    /**
     * Converts an SQL date string formatted "yyyy-mm-dd" or a datetime string formatted
     * "yyyy-mm-dd hh:mm:ss" to a {@link Date} object.
     *
     * @param dateStr   The SQL date string
     * @return          The converted Date object
     * @throws          ParseException if dateStr parameter is not a valid "yyyy-mm-dd" or
     *                  "yyyy-mm-dd hh:mm:ss" format.
     */
    public static Date sqlStringToDate(String dateStr) throws ParseException {
        if (dateStr == null) {
            return null;
        }

        String format = "";
        if (dateStr.length() == Constants.DATE_SQL_FORMAT.length()) {
            format = Constants.DATE_SQL_FORMAT;
        } else if (dateStr.length() == Constants.DATETIME_SQL_FORMAT.length()) {
            format = Constants.DATETIME_SQL_FORMAT;
        } else {
            throw new ParseException("dateStr invalid to parse as Date", 0);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.parse(dateStr);
    }
}
