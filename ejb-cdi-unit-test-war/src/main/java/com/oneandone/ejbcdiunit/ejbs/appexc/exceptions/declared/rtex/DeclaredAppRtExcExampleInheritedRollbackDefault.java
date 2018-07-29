package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DeclaredAppRtExcExampleInheritedRollbackDefault extends RuntimeException {

    private static final long serialVersionUID = -4969387912800017883L;

    public DeclaredAppRtExcExampleInheritedRollbackDefault(String message) {
        super(message);
    }
}
