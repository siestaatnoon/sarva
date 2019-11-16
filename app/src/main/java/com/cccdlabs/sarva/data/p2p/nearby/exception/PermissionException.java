package com.cccdlabs.sarva.data.p2p.nearby.exception;

/**
 * Runtime exception for when a a user sets the device to not allow Nearby.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PermissionException extends PartnerNearbyException {

    /**
     * No argument constructor.
     */
    public PermissionException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public PermissionException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public PermissionException(Throwable cause) {
        super(cause);
    }
}
