package com.cccdlabs.sarva.domain.repository.exception;

/**
 * Exception for delete errors occurring in the implemented {@link com.cccdlabs.sarva.domain.repository.base.Repository}
 * classes.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public class RepositoryDeleteException extends RepositoryException {

    /**
     * No argument constructor.
     */
    public RepositoryDeleteException() {}

    /**
     * Constructor.
     *
     * @param message   The error message
     * @param cause     Throwable object triggering this exception
     */
    public RepositoryDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message   The error message
     */
    public RepositoryDeleteException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause     Throwable object triggering this exception
     */
    public RepositoryDeleteException(Throwable cause) {
        super(cause);
    }
}
