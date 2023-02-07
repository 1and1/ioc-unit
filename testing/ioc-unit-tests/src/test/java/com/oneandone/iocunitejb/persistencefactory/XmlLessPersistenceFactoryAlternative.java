package com.oneandone.iocunitejb.persistencefactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import javax.sql.DataSource;

import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class XmlLessPersistenceFactoryAlternative extends XmlLessPersistenceFactory {
    @Override
    public String getPersistenceUnitName() {
        return "testalternative";
    }

    @Produces
    @Default
    @Override
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

    @Produces
    @Default
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }

}
