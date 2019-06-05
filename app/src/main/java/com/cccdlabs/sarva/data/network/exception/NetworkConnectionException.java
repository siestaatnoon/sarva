package com.cccdlabs.sarva.data.network.exception;

/**
 * Exception for when no internet connection exists for network tasks.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class NetworkConnectionException extends Exception {

    /**
     * No argument constructor.
     */
    public NetworkConnectionException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message   The error message
     */
    public NetworkConnectionException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause     Throwable object triggering this exception
     */
    public NetworkConnectionException(Throwable cause) {
        super(cause);
    }
}
