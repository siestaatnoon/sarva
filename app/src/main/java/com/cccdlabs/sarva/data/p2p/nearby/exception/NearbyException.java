package com.cccdlabs.sarva.data.p2p.nearby.exception;

/**
 * Root Exception for checked errors in the <code>p2p/nearby</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class NearbyException extends Exception {

    /**
     * No argument constructor.
     */
    public NearbyException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public NearbyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public NearbyException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public NearbyException(Throwable cause) {
        super(cause);
    }
}
