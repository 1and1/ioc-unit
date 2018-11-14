package com.oneandone.cdi.weldstarter;

/**
 * @author aschoerk
 */
public class WrappedDeploymentException extends RuntimeException {
    private static final long serialVersionUID = 1152040475215368105L;

    public WrappedDeploymentException(final Throwable cause) {
        super(cause);
    }
}
