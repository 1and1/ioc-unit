package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DerivedFromDeclaredAppRtExcExampleNotInheritedNoRollback extends DeclaredAppRtExcExampleNotInheritedNoRollback {

    private static final long serialVersionUID = -4969387912800017883L;

    public DerivedFromDeclaredAppRtExcExampleNotInheritedNoRollback(String message) {
        super(message);
    }
}
