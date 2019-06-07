package com.cccdlabs.sarva.data.p2p.nearby.exception;

/**
 * Checked exception for when a Nearby Messages object suddenly stops subscribing.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class SubscribeExpiredException  extends NearbyException {

    /**
     * No argument constructor.
     */
    public SubscribeExpiredException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public SubscribeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public SubscribeExpiredException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public SubscribeExpiredException(Throwable cause) {
        super(cause);
    }
}
