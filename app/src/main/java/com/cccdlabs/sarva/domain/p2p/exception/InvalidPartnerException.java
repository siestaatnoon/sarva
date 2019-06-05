package com.cccdlabs.sarva.domain.p2p.exception;

import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;

/**
 * Exception when a {@link com.cccdlabs.sarva.domain.model.partners.Partner} object retrieved
 * from a {@link PartnerMessage} object results in a null
 * or invalid object (e.g. missing UUID).
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class InvalidPartnerException extends PartnerException {

    /**
     * No argument constructor.
     */
    public InvalidPartnerException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public InvalidPartnerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public InvalidPartnerException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public InvalidPartnerException(Throwable cause) {
        super(cause);
    }
}
