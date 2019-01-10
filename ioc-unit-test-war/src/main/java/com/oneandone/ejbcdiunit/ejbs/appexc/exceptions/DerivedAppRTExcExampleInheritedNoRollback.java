package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

/**
 * @author aschoerk
 */
public class DerivedAppRTExcExampleInheritedNoRollback extends AppRTExcExampleInheritedNoRollback {

    private static final long serialVersionUID = -5087496104139541558L;

    public DerivedAppRTExcExampleInheritedNoRollback(String message) {
        super(message);
    }
}
