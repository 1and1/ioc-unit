package com.oneandone.ejbcdiunit.persistence;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.ejb.HibernatePersistence;
import org.jglue.cdiunit.AdditionalClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.SessionContextFactory;
import com.oneandone.ejbcdiunit.internal.EjbExtensionExtended;

/**
 * Persistencefactory with default Handling useable for Cdi-Unit tests with persistence unit "test" Also produces EntityManager, DataSource and
 * UserTransaction annotated with Qualifier @Default To simplify testing, this TestPersistenceFactory does not require persistence.xml. If the
 * persistence unti test cannot be found, it tries to create it's own persistenceprovider.
 *
 * @author aschoerk
 */
@ApplicationScoped
@AdditionalClasses({SessionContextFactory.class})
public class TestPersistenceFactory extends PersistenceFactory {

    public static Set<String> notFoundPersistenceUnits = new HashSet<>();
    static Logger logger = LoggerFactory.getLogger("TestPersistenceFactory");
    @Inject
    EjbExtensionExtended ejbExtensionExtended;

    @Override
    protected String getPersistenceUnitName() {
        return "test";
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

    public EjbExtensionExtended getEjbExtensionExtended() {
        return ejbExtensionExtended;
    }

    protected PersistenceUnitInfo testPersistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "TestPersistenceUnit";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return "org.hibernate.ejb.HibernatePersistence";
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                BasicDataSource bds = new BasicDataSource();
                bds.setDriverClassName(System.getProperty("hibernate.connection.driverclass", "org.h2.Driver"));
                bds.setUrl(System.getProperty("hibernate.connection.url",
                        "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000"));
                bds.setUsername(System.getProperty("hibernate.connection.username", "sa"));
                bds.setPassword(System.getProperty("hibernate.connection.password", ""));
                return bds;
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<URL> getJarFileUrls() {
                try {
                    final ArrayList<URL> jarFiles = Collections.list(this.getClass()
                            .getClassLoader()
                            .getResources(""));
                    logger.info("getJarFileUrls: {}", jarFiles);
                    return jarFiles;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                try {
                    final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("META-INF/persistence.xml");
                    if (resources.hasMoreElements())
                        return resources.nextElement();
                    else
                        return null;
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            public List<String> getManagedClassNames() {
                List<String> result = new ArrayList<>();
                for (Class<?> c : getEjbExtensionExtended().getEntityClasses()) {
                    result.add(c.getName());
                }
                return result;
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return false;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return null;
            }

            @Override
            public ValidationMode getValidationMode() {
                return null;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return null;
            }

            @Override
            public ClassLoader getClassLoader() {
                return Thread.currentThread().getContextClassLoader();
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {

            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return this.getClassLoader();
            }
        };
    }

    /**
     * should work without needing a persistence.xml create it using
     * 
     * @return
     */
    protected EntityManagerFactory createEntityManagerFactory() {
        Throwable possiblyToThrow = null;
        if (!notFoundPersistenceUnits.contains(getPersistenceUnitName())) {
            try {
                return Persistence.createEntityManagerFactory(getPersistenceUnitName());
            } catch (Throwable e) {
                possiblyToThrow = e;
                notFoundPersistenceUnits.add(getPersistenceUnitName());
            }
        }

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.archive.autodetection", "class");
        // possibly override properties using system properties
        for (Map.Entry<Object, Object> p : System.getProperties().entrySet()) {
            properties.put((String) p.getKey(), p.getValue());
        }

        final PersistenceUnitInfo persistenceUnitInfo = testPersistenceUnitInfo();
        try {
            return new HibernatePersistence().createContainerEntityManagerFactory(persistenceUnitInfo,
                    properties);
        } catch (Throwable thw) {
            if (possiblyToThrow != null) {
                throw new RuntimeException(possiblyToThrow);
            } else {
                throw (RuntimeException) thw;
            }
        }
    }
}
