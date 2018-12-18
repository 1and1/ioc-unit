package com.oneandone.cdi.weldstarter;

/**
 * Since the Deployment exception differs completely between the different Weld-Types. It gets wrapped into StarterDeploymentException.
 *
 * @author aschoerk
 */
public class StarterDeploymentException extends RuntimeException {
    private static final long serialVersionUID = 1152040475215368105L;

    public StarterDeploymentException(final Throwable cause) {
        super(cause);
    }
}
