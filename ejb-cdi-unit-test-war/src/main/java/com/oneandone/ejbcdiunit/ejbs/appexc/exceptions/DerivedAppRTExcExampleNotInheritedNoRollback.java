package com.oneandone.ejbcdiunit.ejbs.appexc.exceptions;

/**
 * @author aschoerk
 */
public class DerivedAppRTExcExampleNotInheritedNoRollback extends AppRTExcExampleNotInheritedNoRollback {


    private static final long serialVersionUID = -4724305824767687007L;

    public DerivedAppRTExcExampleNotInheritedNoRollback(String message) {
        super(message);
    }
}
