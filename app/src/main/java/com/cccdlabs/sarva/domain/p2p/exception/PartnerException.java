package com.cccdlabs.sarva.domain.p2p.exception;

import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;

/**
 * General exception that occurs with {@link PartnerMessage}
 * objects.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class PartnerException extends RuntimeException {

    /**
     * No argument constructor.
     */
    public PartnerException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public PartnerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public PartnerException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public PartnerException(Throwable cause) {
        super(cause);
    }
}
