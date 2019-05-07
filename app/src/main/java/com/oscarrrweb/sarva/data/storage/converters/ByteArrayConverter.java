package com.oscarrrweb.sarva.data.storage.converters;

import androidx.room.TypeConverter;
import android.util.Base64;

/**
 * Converter used by the {@link androidx.room.Room} database entities that converts file/image
 * byte array fields to and from base64 encoded Strings for storage.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class ByteArrayConverter {

    /**
     * Converts a base64 encoded string to a byte array. Annotated with {@link TypeConverter}
     * as designated by the Room database.
     * <p>
     * NOTE: Android Base64 used for backward compatibility (API 23+)
     * java.util.Base64 is Java 8 an API 26+.
     *
     * @param base64Str The base64 encoded string
     * @return          The string converted to a byte array
     */
    @TypeConverter
    public static byte[] toByteArray(String base64Str) {
        if (base64Str == null) {
            return null;
        }

        return Base64.decode(base64Str, Base64.DEFAULT);
    }

    /**
     * Converts a byte array to a base64 encoded string. Annotated with {@link TypeConverter}
     * as designated by the Room database.
     * <p>
     * NOTE: Android Base64 used for backward compatibility (API 23+)
     * java.util.Base64 is Java 8 an API 26+.
     *
     * @param bytes     The byte array to be converted
     * @return          The converted string
     */
    @TypeConverter
    public static String fromBase64String(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
