package com.oneandone.iocunitejb.helpers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class J2eeSimTest1Factory extends XmlAwarePersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
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
