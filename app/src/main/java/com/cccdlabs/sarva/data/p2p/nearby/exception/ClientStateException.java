package com.cccdlabs.sarva.data.p2p.nearby.exception;

/**
 * Runtime exception for when a Nearby Messages client is in a state where publishing and
 * subscribing cannot start or continue.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class ClientStateException extends PartnerNearbyException {

    /**
     * No argument constructor.
     */
    public ClientStateException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public ClientStateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public ClientStateException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public ClientStateException(Throwable cause) {
        super(cause);
    }
}
