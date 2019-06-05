package com.cccdlabs.sarva.data.p2p.nearby.exception;

/**
 * Runtime exception for when a Nearby Messages object suddenly stops publishing.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PublishExpiredException extends NearbyException {

    /**
     * No argument constructor.
     */
    public PublishExpiredException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public PublishExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public PublishExpiredException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public PublishExpiredException(Throwable cause) {
        super(cause);
    }
}
