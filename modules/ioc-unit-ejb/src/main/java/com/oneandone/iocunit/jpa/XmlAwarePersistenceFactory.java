package com.oneandone.iocunit.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
abstract public class XmlAwarePersistenceFactory extends PersistenceFactory {

    @Inject
    BeanManager beanManager;

    static ThreadLocal<BeanManager> currentBeanManager = new ThreadLocal<>();

    public static BeanManager getCurrentBeanManager() {
        return currentBeanManager.get();
    }

    Map<String, Object> getPropertiesMap() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("hibernate.connection.provider_class", "com.oneandone.iocunit.ejb.persistence.HibernateConnectionProvider");
        res.put("eclipselink.jdbc.connector", "com.oneandone.iocunit.ejb.persistence.EclipselinkConnectionProvider");
        return res;
    }

    protected EntityManagerFactory createEntityManagerFactory() {
        PersistenceProvider actProvider = getPersistenceProvider();
        currentBeanManager.set(beanManager);
        EntityManagerFactory res = actProvider.createEntityManagerFactory(getPersistenceUnitName(), getPropertiesMap());
        currentBeanManager.set(null);
        return res;
    }


}
