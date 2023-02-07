package com.oneandone.iocunitejb.helpers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import javax.sql.DataSource;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class J2eeSimTest1Factory extends XmlAwarePersistenceFactory {
    @Override
    public String getPersistenceUnitName() {
        return "j2eeSimDS1Test";
    }

    /**
     * create a jdbc-Datasource using the same driver url user and password as the entityManager
     *
     * @return a jdbc-Datasource using the same driver url user and password as the entityManager
     */
    @Override
    @Produces
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }
}
