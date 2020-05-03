package com.oneandone.iocunit.jta.hibernate;

import java.util.Map;

import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver;
import org.hibernate.service.spi.ServiceRegistryImplementor;

/**
 * @author aschoerk
 */
public class NarayanaJtaPlatformResolver implements JtaPlatformResolver {
    private static final long serialVersionUID = 2758749956836509077L;

    @Override
    public JtaPlatform resolveJtaPlatform(final Map map, final ServiceRegistryImplementor serviceRegistryImplementor) {
        return new NarayanaJtaPlatform();
    }
}
