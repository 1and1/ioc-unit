package com.oneandone.iocunit.jpa.tra;

/**
 * @author aschoerk
 */
public class TransactionRolledbackLocalException extends RuntimeException {
    private static final long serialVersionUID = 443027816619346715L;

    public TransactionRolledbackLocalException(final String simulated_transaction_manager) { super(simulated_transaction_manager); }
}
