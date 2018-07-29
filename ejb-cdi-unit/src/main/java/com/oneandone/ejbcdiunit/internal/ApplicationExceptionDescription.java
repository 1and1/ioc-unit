package com.oneandone.ejbcdiunit.internal;

/**
 * @author aschoerk
 */

public class ApplicationExceptionDescription {
    private String className;
    private boolean rollback = false;
    private boolean inherited = true;

    public String getClassName() {
        return className;
    }

    public void setClassName(final String classNameP) {
        this.className = classNameP;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(final boolean rollbackP) {
        this.rollback = rollbackP;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(final boolean inheritedP) {
        this.inherited = inheritedP;
    }
}
