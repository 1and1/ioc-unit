package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DeclaredAppRtExcExampleInheritedRollback extends RuntimeException {

    private static final long serialVersionUID = -4969387912800017883L;

    public DeclaredAppRtExcExampleInheritedRollback(String message) {
        super(message);
    }
}
