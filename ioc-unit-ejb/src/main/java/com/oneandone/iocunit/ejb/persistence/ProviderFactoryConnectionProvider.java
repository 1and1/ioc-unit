package com.oneandone.iocunit.ejb.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;

import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
public class ProviderFactoryConnectionProvider extends DriverManagerConnectionProviderImpl {
    private static final long serialVersionUID = -3538987211261126789L;

    @Override
    public Connection getConnection() throws SQLException {
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext();
            final BeanManager beanManager = (BeanManager) initialContext.lookup("java:comp/BeanManager");
            Bean<?> bean = beanManager.resolve(beanManager.getBeans(JdbcSqlConverter.class));
            if(bean != null) {
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    JdbcSqlConverter jdbcSqlConverter = (JdbcSqlConverter) creationalContexts.create(bean, ApplicationScoped.class);
                    return new ConnectionDelegate(super.getConnection(), jdbcSqlConverter, true);
                }
            }
            else {
                return super.getConnection();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
