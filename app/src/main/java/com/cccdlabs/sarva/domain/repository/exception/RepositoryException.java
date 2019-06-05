package com.cccdlabs.sarva.domain.repository.exception;

/**
 * Root Exception for errors occurring in the implemented {@link com.cccdlabs.sarva.domain.repository.base.Repository}
 * classes.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class RepositoryException extends Exception {

    /**
     * No argument constructor.
     */
    public RepositoryException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message   The error message
     */
    public RepositoryException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause     Throwable object triggering this exception
     */
    public RepositoryException(Throwable cause) {
        super(cause);
    }
}
