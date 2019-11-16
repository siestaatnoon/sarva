package com.cccdlabs.sarva.data.p2p.nearby.exception;

/**
 * Root Exception for runtime errors in the <code>p2p/nearby</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerNearbyException extends RuntimeException {

    /**
     * No argument constructor.
     */
    public PartnerNearbyException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public PartnerNearbyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public PartnerNearbyException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public PartnerNearbyException(Throwable cause) {
        super(cause);
    }
}
