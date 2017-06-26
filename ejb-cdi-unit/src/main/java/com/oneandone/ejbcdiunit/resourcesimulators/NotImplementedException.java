package com.oneandone.ejbcdiunit.resourcesimulators;

/**
 * @author aschoerk
 */
public class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = -3958406648738999778L;

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public NotImplementedException(String message) {
        super(message);
    }
}
