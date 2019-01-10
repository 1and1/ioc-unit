package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

import javax.ejb.ApplicationException;

/**
 * @author aschoerk
 */
@ApplicationException(inherited = true, rollback = true)
public class AppRTExcExampleInheritedRollback extends RuntimeException {
    private static final long serialVersionUID = -425189114803754060L;

    public AppRTExcExampleInheritedRollback(String message) {
        super(message);
    }
}
