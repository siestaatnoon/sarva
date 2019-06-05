package com.oscarrrweb.sarva.data;

/**
 * Constant used in the data package of this application.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
final public class Constants {

    /**
     * Name of database, per user, for application. "%s" added to specify user or
     * other identifier.
     */
    public static final String APP_DATABASE         = "sandbox%s.db";

    /**
     * Base URL to connect to server API.
     */
    public static final String API_URL              = "https://sandbox.oscarrrweb.com";

    /**
     * Default character encoding in this package.
     */
    public static final String CHAR_ENCODING        = "UTF-8";

    /**
     * String format to use to convert a {@link java.util.Date} object to an SQL DATE type String
     * to store in a database.
     */
    public static final String DATE_SQL_FORMAT      = "yyyy-MM-dd";

    /**
     * String format to use to convert a {@link java.util.Date} object to an SQL DATETIME
     * or TIMESTAMP type String to store in a database.
     */
    public static final String DATETIME_SQL_FORMAT  = "yyyy-MM-dd HH:mm:ss";

    /**
     * Key used in request header used to store a token for API validation.
     */
    public static final String HEADER_TOKEN         = "X-Token";

    /**
     * Key used in request header used to store a token hash for API validation.
     */
    public static final String HEADER_TOKEN_HASH    = "X-Token-Hash";

    /**
     * Default volume setting for search tone.
     */
    public static final int SEARCH_TONE_VOLUME      = 10;
}
