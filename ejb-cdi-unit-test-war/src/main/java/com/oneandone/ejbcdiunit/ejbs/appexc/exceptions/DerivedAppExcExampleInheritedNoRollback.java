package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

/**
 * @author aschoerk
 */
public class DerivedAppExcExampleInheritedNoRollback extends AppExcExampleInheritedNoRollback {

    private static final long serialVersionUID = 90439440284184355L;

    public DerivedAppExcExampleInheritedNoRollback(String message) {
        super(message);
    }
}
