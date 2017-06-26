package com.oneandone.ejbcdiunit.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.jglue.cdiunit.AdditionalClasses;

import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.SupportEjbExtended;

/**
 * Persistencefactory with default Handling useable for Cdi-Unit tests with persistence unit "test"
 * Also produces  EntityManager, DataSource and UserTransaction annotated with Qualifier @Default
 *
 * @author aschoerk
 */
@ApplicationScoped
@SupportEjbExtended
@AdditionalClasses({SessionContextFactory.class})
public class TestPersistenceFactory extends PersistenceFactory {

    @Override
    protected String getPersistenceUnitName() {
        return "test";
    }


    /**
     * @return Usertransaction injectable
     */
    @Produces
    @Default
    @Override
    public UserTransaction produceUserTransaction() {
        return super.produceUserTransaction();
    }

    /**
     * returns EntityManager, to be injected and used so that the current threadSpecific context is correctly handled
     *
     * @return the EntityManager as it is returnable by producers.
     */
    @Produces
    @Default
    @Override
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

    /**
     * create a jdbc-Datasource using the same driver url user and password as the entityManager
     *
     * @return a jdbc-Datasource using the same driver url user and password as the entityManager
     */
    @Produces
    @Default
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }
}
