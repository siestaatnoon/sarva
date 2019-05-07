package com.oscarrrweb.sarva.data.network.base;

/**
 * Abstraction for determining Exception types that occur in the networking API.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public interface ApiError {

    /**
     * Returns true if an HTTP response status received is 401 (unauthorized) or 423
     * (locked out).
     *
     * @return True if response status code is 401 or 423, false otherwise
     */
    boolean isAuthFailureError();

    /**
     * Returns true if an HTTP response status received is an internal server error, status
     * code 500.
     *
     * @return True if response status code is 500, false otherwise
     */
    boolean isNetworkError();

    /**
     * Returns true if no internet connection exists on device.
     *
     * @return True if internet connection does not exist, false otherwise
     */
    boolean isNoConnectionError();

    /**
     * Returns true if a request was timed out.
     *
     * @return True if timeout occurred, false otherwise
     */
    boolean isTimeoutError();
}
