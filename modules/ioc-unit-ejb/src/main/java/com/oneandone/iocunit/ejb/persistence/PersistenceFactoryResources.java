package com.oneandone.iocunit.ejb.persistence;

import java.lang.reflect.Method;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class PersistenceFactoryResources {
    @Inject
    private Instance<JdbcSqlConverter> jdbcSqlConverterInstance;

    protected JdbcSqlConverter getJdbcSqlConverterIfThereIsOne() {
        JdbcSqlConverter jdbcSqlConverter = null;
        try {
            Method m = Instance.class.getMethod("isResolvable");
            if ((boolean)m.invoke(jdbcSqlConverterInstance)) {
                jdbcSqlConverter = jdbcSqlConverterInstance.get();
            }
        } catch (NoSuchMethodException e) {
            if (!jdbcSqlConverterInstance.isUnsatisfied()) {
                jdbcSqlConverter = jdbcSqlConverterInstance.get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unexpected", e);
        }
        return jdbcSqlConverter;
    }
}
