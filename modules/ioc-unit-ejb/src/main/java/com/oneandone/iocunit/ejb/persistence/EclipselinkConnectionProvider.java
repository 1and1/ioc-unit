package com.oneandone.iocunit.ejb.persistence;

import java.sql.Connection;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.Session;

import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
public class EclipselinkConnectionProvider extends DefaultConnector {
    private static final long serialVersionUID = -3538987211261126789L;

    @Override
    public Connection connect(final Properties properties, final Session session) throws DatabaseException {
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext();
            final BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");
            Bean<?> bean = beanManager.resolve(beanManager.getBeans(JdbcSqlConverter.class));
            if(bean != null) {
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    JdbcSqlConverter jdbcSqlConverter = (JdbcSqlConverter) creationalContexts.create(bean, ApplicationScoped.class);
                    return new ConnectionDelegate(super.connect(properties, session), jdbcSqlConverter, true);
                }
            }
            else {
                return super.connect(properties, session);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
