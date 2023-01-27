package com.oneandone.iocunitejb.ejbs.appexc.exceptions;

import jakarta.ejb.ApplicationException;

/**
 * @author aschoerk
 */
@ApplicationException(inherited = false, rollback = true)
public class AppRTExcExampleNotInheritedRollback extends RuntimeException {

    private static final long serialVersionUID = -8559815538639804640L;

    public AppRTExcExampleNotInheritedRollback(String message) {
        super(message);
    }
}
