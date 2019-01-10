package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DerivedFromDeclaredAppRtExcExampleNotInheritedRollback extends DeclaredAppRtExcExampleNotInheritedRollback {

    private static final long serialVersionUID = -4969387912800017883L;

    public DerivedFromDeclaredAppRtExcExampleNotInheritedRollback(String message) {
        super(message);
    }
}
