package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

import javax.ejb.ApplicationException;

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
