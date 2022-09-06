package com.oneandone.iocunit.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
abstract public class XmlAwarePersistenceFactory extends PersistenceFactory {

    Map<String, Object> getPropertiesMap() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("hibernate.connection.provider_class", "com.oneandone.iocunit.ejb.persistence.HibernateConnectionProvider");
        res.put("eclipselink.jdbc.connector", "com.oneandone.iocunit.ejb.persistence.EclipselinkConnectionProvider");
        return res;
    }

    protected EntityManagerFactory createEntityManagerFactory() {
        PersistenceProvider actProvider = getPersistenceProvider();
        EntityManagerFactory res = actProvider.createEntityManagerFactory(getPersistenceUnitName(), getPropertiesMap());
        return res;
    }


}
