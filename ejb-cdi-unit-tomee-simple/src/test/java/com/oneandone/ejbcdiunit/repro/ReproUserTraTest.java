package com.oneandone.ejbcdiunit.repro;

import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.openejb.config.EjbModule;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

/**
 * @author aschoerk
 */

@RunWith(ApplicationComposer.class)
public class ReproUserTraTest {

    @Inject
    private EntityManager entityManager;

    @Inject
    Logger logger;

    @Module
    public PersistenceUnit persistence() {
        PersistenceUnit unit = new PersistenceUnit("repro-unit");
        unit.setJtaDataSource("reproDatabase");
        unit.setNonJtaDataSource("reproDatabaseUnmanaged");
        unit.getClazz().add(ReproEntity.class.getName());
        unit.setProperty("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
        return unit;
    }

    @Module
    public EjbModule module() {
        EjbModule module = new EjbModule(new EjbJar("repro-beans")
                .enterpriseBean(new StatelessBean(SaverBean.class)));
        Beans beans = new Beans();
        beans.managedClass(TomeeResources.class.getName());
        module.setBeans(beans);
        module.setModuleId("repro-module");
        return module;
    }


    @Configuration
    public Properties config() throws Exception {
        Properties p = new Properties();
        p.put("reproDatabase", "new://Resource?type=DataSource");
        p.put("reproDatabase.JdbcDriver", "org.h2.Driver");
        p.put("reproDatabase.JdbcUrl", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0");
        return p;
    }

    @Resource
    UserTransaction userTransaction;

    @EJB
    SaverBean saverBean;

    @Test
    public void test() throws SystemException, NotSupportedException {
        logger.info("starting test");
        userTransaction.begin();

        saverBean.saveOneEntity();

        assert 1 == entityManager.createQuery("select count(e) from ReproEntity e", Long.class).getSingleResult();

        userTransaction.rollback();

        logger.info("end of test");
    }

}
