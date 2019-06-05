package com.oscarrrweb.sarva.data.utils;

import java.util.UUID;

/**
 * Utility class for generating UUID values.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
final public class UuidUtils {

    /**
     * Returns a random UUID string.
     *
     * @return The UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
