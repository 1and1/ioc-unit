package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

import javax.ejb.ApplicationException;

/**
 * @author aschoerk
 */
@ApplicationException(inherited = true, rollback = false)
public class AppExcExampleInheritedNoRollback extends Exception {

    private static final long serialVersionUID = -4969387912800017883L;

    public AppExcExampleInheritedNoRollback(String message) {
        super(message);
    }
}
