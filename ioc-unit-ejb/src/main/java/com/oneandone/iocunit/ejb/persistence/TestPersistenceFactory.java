package com.oneandone.iocunit.ejb.persistence;

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
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.EjbExtensionExtended;
import com.oneandone.iocunit.ejb.SessionContextFactory;

/**
 * This Persistencefactory should allow to create tests with in memory database very fast.
 * No persistence.xml for the testconfig is necessary. Default persistenceunitname is test. If that is not
 * found some defaults are used: Db-Driver: H2, username SA, password empty.
 * To let it handle a defaultschema: subclass and override getSchema but: if subclassing all Producers produceEntityManager()
 * and produceDataSource() must be overridden as well.
 * If PersistenceProvider is Hibernate and hibernate.default_schema is set --> that Schema is created in H2 at start.
 * Uses also eclipseLink if that PersistenceProvider is found.
 *
 * @author aschoerk
 */
@ApplicationScoped
@TestClasses({ SessionContextFactory.class })
public class TestPersistenceFactory extends PersistenceFactory {

    public static Set<String> notFoundPersistenceUnits = new HashSet<>();
    static Logger logger = LoggerFactory.getLogger("TestPersistenceFactory");
    @Inject
    private EjbExtensionExtended ejbExtensionExtended;

    @Override
    protected String getPersistenceUnitName() {
        return "test";
    }

    protected String getSchema()  {
        String hibernateSchema = System.getProperty("hibernate.default_schema");
        return hibernateSchema;
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

    protected PersistenceUnitInfo getHibernatePersistenceUnitInfo(final HashMap<String, Object> properties) {

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

            private String getProperty(String name, String hibernateName, String defaultValue) {
                Object propValue = properties.get(name);
                if (propValue != null) return propValue.toString();
                propValue = System.getProperty(name);
                if (propValue != null)
                    return propValue.toString();
                propValue = properties.get(hibernateName);
                if (propValue != null) return propValue.toString();
                propValue = System.getProperty(hibernateName);
                if (propValue != null)
                    return propValue.toString();

                return defaultValue;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                BasicDataSource bds = new BasicDataSource();
                bds.setDriverClassName(getProperty("javax.persistence.jdbc.driver", "hibernate.connection.driverclass", "org.h2.Driver"));
                bds.setUrl(getProperty("javax.persistence.jdbc.url","hibernate.connection.url","jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000"));
                bds.setUsername(getProperty("javax.persistence.jdbc.user", "hibernate.connection.username", "sa"));
                bds.setPassword(getProperty("javax.persistence.jdbc.password", "hibernate.connection.password", ""));
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
    @Override
    protected EntityManagerFactory createEntityManagerFactory() {
        Throwable possiblyToThrow = null;
        if(!notFoundPersistenceUnits.contains(getPersistenceUnitName())) {
            try {
                EntityManagerFactory result = super.createEntityManagerFactory();
                if (result != null)
                    return result;
                notFoundPersistenceUnits.add(getPersistenceUnitName());
            } catch (Throwable e) {
                possiblyToThrow = e;
                notFoundPersistenceUnits.add(getPersistenceUnitName());
            }
        }

        EntityManagerFactory res = createEntityManagerFactoryWOPersistenceXml();
        return res;
    }

    private EntityManagerFactory createEntityManagerFactoryWOPersistenceXml() {
        PersistenceProvider persistenceProvider = getPersistenceProvider();
        HashMap<String, Object> properties = new HashMap<>();


        if(getRecommendedProvider().equals(Provider.HIBERNATE)) {
            initHibernateProperties(properties);
            // possibly override properties using system properties
            for (Map.Entry<Object, Object> p : System.getProperties().entrySet()) {
                properties.put((String) p.getKey(), p.getValue());
            }
            final PersistenceUnitInfo persistenceUnitInfo = getHibernatePersistenceUnitInfo(properties);
            try {
                return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), properties).build();
            } catch (Throwable thw) {
                throw new RuntimeException(thw);
            }

        } else {
            initEclipseLinkProperties(properties);
            for (Map.Entry<Object, Object> p : System.getProperties().entrySet()) {
                properties.put((String) p.getKey(), p.getValue());
            }
            properties.put("eclipselink.se-puinfo", new SEPersistenceUnitInfo() {
                @Override
                public String getPersistenceUnitName() {
                    return "TestPersistenceUnit";
                }
                @Override
                public DataSource getJtaDataSource() {
                    return null;
                }

                /**
                 * take the first URL found, which supports Entities
                 * @return the first URL found, depending on EntityClasses
                 */
                @Override
                public URL getPersistenceUnitRootUrl() {
                    try {
                        final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(".");
                        if (resources.hasMoreElements())
                            return resources.nextElement();
                        else
                            return null;
                    } catch (IOException e) {
                        return null;
                    }
                }

                /**
                 * @return
                 */
                @Override
                public List<URL> getJarFileUrls() {
                    Set<URL> urls = new HashSet<>();
                    for (Class<?> c : getEjbExtensionExtended().getEntityClasses()) {
                        urls.add(c.getProtectionDomain().getCodeSource().getLocation());
                    }
                    List<URL> jarFiles = new ArrayList<>();
                    jarFiles.addAll(urls);
                    logger.info("getJarFileUrls: {}", jarFiles);
                    return jarFiles;

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
                public ClassLoader getClassLoader() {
                    return Thread.currentThread().getContextClassLoader();
                }

            });
            return persistenceProvider.createEntityManagerFactory(getPersistenceUnitName(), properties);
        }
    }

    private void initEclipseLinkProperties(final HashMap<String, Object> properties) {
        properties.put("javax.persistence.jdbc.driver","org.h2.Driver");
        properties.put("javax.persistence.jdbc.url",
                "jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000"
                + createSchemaForH2());
        properties.put("javax.persistence.jdbc.user" , "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("eclipselink.disableXmlSecurity","true");
        properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
        properties.put("eclipselink.target-database", "MYSQL");
        System.clearProperty("hibernate.default_schema");
    }

    private void initHibernateProperties(final HashMap<String, Object> properties) {
        if (System.getProperty("hibernate.connection.driverclass") == null)
            properties.put("javax.persistence.jdbc.driver","org.h2.Driver");
        if (System.getProperty("hibernate.connection.url") == null)
            properties.put("javax.persistence.jdbc.url",
                    "jdbc:h2:mem:test;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000"
                    + createSchemaForH2());
        if (System.getProperty("hibernate.connection.username") == null)
            properties.put("javax.persistence.jdbc.user" , "sa");
        if (System.getProperty("hibernate.connection.password") == null)
            properties.put("javax.persistence.jdbc.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.archive.autodetection", "class");
    }

    private String createSchemaForH2() {
        return getSchema() != null ? ";INIT=create schema if not exists " + getSchema() : "";
    }
}
