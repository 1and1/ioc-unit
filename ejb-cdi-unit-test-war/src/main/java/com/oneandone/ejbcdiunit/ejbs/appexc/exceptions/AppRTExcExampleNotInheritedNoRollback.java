package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

import javax.ejb.ApplicationException;

/**
 * @author aschoerk
 */
@ApplicationException(inherited = false, rollback = false)
public class AppRTExcExampleNotInheritedNoRollback extends RuntimeException {

    private static final long serialVersionUID = 3371465853506034688L;

    public AppRTExcExampleNotInheritedNoRollback(String message) {
        super(message);
    }
}
