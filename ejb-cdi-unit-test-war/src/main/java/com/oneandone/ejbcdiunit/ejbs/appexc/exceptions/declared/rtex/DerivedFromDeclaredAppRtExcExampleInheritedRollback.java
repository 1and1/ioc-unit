package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DerivedFromDeclaredAppRtExcExampleInheritedRollback extends DeclaredAppRtExcExampleInheritedRollback {

    private static final long serialVersionUID = -4969387912800017883L;

    public DerivedFromDeclaredAppRtExcExampleInheritedRollback(String message) {
        super(message);
    }
}
