package com.oscarrrweb.sarva.data.storage.converters;

import androidx.room.TypeConverter;
import android.util.Log;

import com.oscarrrweb.sarva.data.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Converter used by the {@link androidx.room.Room} database entities that converts SQL DATE
 * and DATETIME string representations to and from {@link Date} objects for storage.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class DateConverter {

    /**
     * Converts an SQL date string, formatted YYYY-MM-DD or YYYY-MM-DD HH:MM:SS to a
     * {@link Date} object. Annotated with {@link TypeConverter} as designated by the Room database.
     *
     * @param dateStr   The date string
     * @return          The Date object
     */
    @TypeConverter
    public static Date fromSqlString(String dateStr) {
        if (dateStr == null) {
            return null;
        }

        try {
            return DateUtils.sqlStringToDate(dateStr);
        } catch (ParseException e) {
            Log.e(DateConverter.class.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * Converts a {@link Date} object to an SQL date string, formatted YYYY-MM-DD or
     * YYYY-MM-DD HH:MM:SS. Annotated with {@link TypeConverter} as designated by the
     * Room database.
     * <p>
     * NOTE: A date that results in a time of 00:00:00 will always return in YYYY-MM-DD
     * format.
     *
     * @param date      The Date object
     * @return          The date string
     */
    @TypeConverter
    public static String toSqlString(Date date) {
        if (date == null) {
            return null;
        }
        return DateUtils.dateToSqlString(date);
    }
}
