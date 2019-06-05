package com.cccdlabs.sarva.domain.p2p.exception;

import com.cccdlabs.sarva.domain.model.partners.PartnerMessage;

/**
 * Exception when a {@link com.cccdlabs.sarva.domain.model.partners.Partner} object retrieved
 * from a {@link PartnerMessage} object is not paired (saved),
 * on the user's device.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class UnpairedPartnerException extends PartnerException {

    /**
     * No argument constructor.
     */
    public UnpairedPartnerException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public UnpairedPartnerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message The error message
     */
    public UnpairedPartnerException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable object triggering this exception
     */
    public UnpairedPartnerException(Throwable cause) {
        super(cause);
    }
}
