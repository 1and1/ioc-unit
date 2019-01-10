package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

import javax.ejb.ApplicationException;

/**
 * @author aschoerk
 */
@ApplicationException(inherited = true, rollback = true)
public class AppExcExampleInheritedRollback extends Exception {
    private static final long serialVersionUID = -425189114803754060L;

    public AppExcExampleInheritedRollback(String message) {
        super(message);
    }
}
