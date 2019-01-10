package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.declared.rtex;

/**
 * @author aschoerk
 */
public class DerivedFromDeclaredAppRtExcExampleInheritedNoRollback extends DeclaredAppRtExcExampleInheritedNoRollback {

    private static final long serialVersionUID = -4969387912800017883L;

    public DerivedFromDeclaredAppRtExcExampleInheritedNoRollback(String message) {
        super(message);
    }
}
