package com.oneandone.ejbcdiunit.persistence;

/**
 * A Runtime used to encapsulate exceptions that occur during TestTransactions.
 * This way the Closure can be defined in a unique way and does not have to handle checked exceptions inside.
 * On the other side the Testcode can react on these exceptions by catching this type.
 *
 * @author aschoerk
 */
public class TestTransactionException extends RuntimeException {


    private static final long serialVersionUID = 7661640564009975747L;

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public TestTransactionException(Throwable cause) {
        super(cause);
    }

}
