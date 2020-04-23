package net.oneandone.example;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.injection.spi.EjbInjectionServices;

/**
 * @author aschoerk
 */
public class EjbInjectionService implements EjbInjectionServices {
    /**
     * Resolve the value for the given @EJB injection point
     *
     * @param injectionPoint
     *            the injection point metadata
     * @return an instance of the EJB
     * @throws IllegalArgumentException
     *             if the injection point is not annotated with @EJB, or, if the injection point is a method that doesn't follow JavaBean conventions
     */
    @Override
    public Object resolveEjb(InjectionPoint injectionPoint) {

        try {
            return injectionPoint.getMember().getDeclaringClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called by Weld when it is shutting down, allowing the service to perform any cleanup needed.
     */
    @Override
    public void cleanup() {

    }
}
