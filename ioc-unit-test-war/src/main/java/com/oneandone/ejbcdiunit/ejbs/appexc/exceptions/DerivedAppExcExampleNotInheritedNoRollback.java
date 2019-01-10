package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

/**
 * @author aschoerk
 */
public class DerivedAppExcExampleNotInheritedNoRollback extends AppExcExampleNotInheritedNoRollback {


    private static final long serialVersionUID = 6553335820445314508L;

    public DerivedAppExcExampleNotInheritedNoRollback(String message) {
        super(message);
    }
}
