package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DeclaredAppRtExcExampleInheritedNoRollback extends RuntimeException {

    private static final long serialVersionUID = -4969387912800017883L;

    public DeclaredAppRtExcExampleInheritedNoRollback(String message) {
        super(message);
    }
}
