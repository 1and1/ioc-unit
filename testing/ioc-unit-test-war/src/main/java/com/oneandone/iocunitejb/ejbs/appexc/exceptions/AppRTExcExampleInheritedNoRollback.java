package com.oneandone.iocunitejb.ejbs.appexc.exceptions;

import jakarta.ejb.ApplicationException;

/**
 * @author aschoerk
 */
@ApplicationException(inherited = true, rollback = false)
public class AppRTExcExampleInheritedNoRollback extends RuntimeException {

    private static final long serialVersionUID = -4969387912800017883L;

    public AppRTExcExampleInheritedNoRollback(String message) {
        super(message);
    }
}
