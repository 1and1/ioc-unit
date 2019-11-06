package com.oneandone.iocunit.ejb.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

/**
 * @author aschoerk
 */
abstract public class XmlAwarePersistenceFactory extends PersistenceFactory {

    Map<String, Object> getPropertiesMap() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("hibernate.connection.provider_class", ProviderFactoryConnectionProvider.class.getName());
        return res;
    }

    protected EntityManagerFactory createEntityManagerFactory() {
        PersistenceProvider actProvider = getPersistenceProvider();
        return actProvider.createEntityManagerFactory(getPersistenceUnitName(), getPropertiesMap());
    }


}
