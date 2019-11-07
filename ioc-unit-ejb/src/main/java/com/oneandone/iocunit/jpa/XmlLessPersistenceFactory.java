package com.oneandone.iocunit.jpa;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

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
 * This Persistencefactory should allow to create tests with in an h2 database very fast.
 * No persistence.xml for the testconfig is necessary. Default persistenceunitname is test. If the persistenceunit "test"
 * is not found in persistence.xml some defaults are used: Db-Driver: H2, username SA, password empty. "drop all objects"
 * before first connection.
 * To let it handle a defaultschema: subclass and override getSchema but: if subclassing all Producers produceEntityManager()
 * and produceDataSource() must be overridden as well.
 * If Entity-Beans are found by the EjbExtension, they are automatically added to the persistence-context.
 * This can be controlled by getEntityBeanRegex();
 * If PersistenceProvider is Hibernate and hibernate.default_schema is set --> that Schema is created in H2 at start.
 * Uses also eclipseLink if only that PersistenceProvider is found.
 *
 * Only works with Hibernate 5
 *
 * @author aschoerk
 */
@ApplicationScoped
@TestClasses({ SessionContextFactory.class })
public class XmlLessPersistenceFactory extends XmlLessPersistenceFactoryBase {

    static Logger logger = LoggerFactory.getLogger("XmlLessPersistenceFactory");
    @Inject
    private EjbExtensionExtended ejbExtensionExtended;
    HashMap<String, Object> properties = new HashMap<>();

    HashMap<String, Object> childProperties = new HashMap<>();

    public void addProperty(String name, Object property) {
        childProperties.put(name, property);
    }

    @Override
    protected String getPersistenceUnitName() {
        if (getFilenamePrefix() == null)
            return "test";
        else
            return getFilenamePrefix();
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

    public String getEntityBeanRegex() {
        return null;
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
            DataSource datasource;

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
                if (datasource == null)
                    datasource = createDataSource();
                return checkAndDoInFirstConnection(datasource);
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<URL> getJarFileUrls() {
                return XmlLessPersistenceFactory.this.getJarFileUrls();
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null; // TestPersistenceFactory.this.getPersistenceUnitRootUrl("META-INF/persistence.xml");
            }

            @Override
            public List<String> getManagedClassNames() {
                return XmlLessPersistenceFactory.this.getManagedClassNames();
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return SharedCacheMode.NONE;
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
            public boolean excludeUnlistedClasses() {
                return XmlLessPersistenceFactory.this.getEntityBeanRegex() != null;
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
        EntityManagerFactory res = createEntityManagerFactoryWOPersistenceXml();
        return res;
    }

    private EntityManagerFactory createEntityManagerFactoryWOPersistenceXml() {
        PersistenceProvider persistenceProvider = getPersistenceProvider();

        EntityManagerFactory result;
        if(getRecommendedProvider().equals(Provider.HIBERNATE)) {
            initHibernateProperties(properties);
            // possibly override properties using system properties
            overwritePersistenceProperties(System.getProperties());
            final PersistenceUnitInfo persistenceUnitInfo = getHibernatePersistenceUnitInfo(properties);
            try {
                result = new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), properties).build();
            } catch (Throwable thw) {
                throw new RuntimeException(thw);
            }

        } else {
            initEclipseLinkProperties(properties);
            overwritePersistenceProperties(System.getProperties());
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
                    return XmlLessPersistenceFactory.this.getPersistenceUnitRootUrl(".");
                }

                /**
                 * @return
                 */
                @Override
                public List<URL> getJarFileUrls() {
                    return XmlLessPersistenceFactory.this.getJarFileUrls();
                }

                @Override
                public List<String> getManagedClassNames() {
                    return XmlLessPersistenceFactory.this.getManagedClassNames();
                }

                @Override
                public DataSource getNonJtaDataSource() {
                    DataSource ds = this.nonJtaDataSource;
                    if (ds == null) {
                        ds = createDataSource();
                    }
                    return checkAndDoInFirstConnection(ds);
                }

                @Override
                public boolean excludeUnlistedClasses() {
                    return XmlLessPersistenceFactory.this.getEntityBeanRegex() != null;
                }
                @Override
                public ClassLoader getClassLoader() {
                    return Thread.currentThread().getContextClassLoader();
                }

            });
            result = persistenceProvider.createEntityManagerFactory(getPersistenceUnitName(), properties);
        }

        return result;
    }

    /**
     * Possibility to set certain properties as they are normally defined by persistence.xml
     *
     * @param properties to be used to overwrite the property which are used as defaults.
     */
    public void overwritePersistenceProperties(Properties properties) {
        for (Map.Entry<Object, Object> p : properties.entrySet()) {
            this.properties.put((String) p.getKey(), p.getValue());
        }
    }

    AtomicInteger count = new AtomicInteger(0);

    private void initEclipseLinkProperties(final HashMap<String, Object> properties) {
        properties.put("javax.persistence.jdbc.driver","org.h2.Driver");
        String db = getDbNameOrMem();
        properties.put("javax.persistence.jdbc.url",
                "jdbc:h2:" + db + ";DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
        properties.put("javax.persistence.jdbc.user" , "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("eclipselink.disableXmlSecurity","true");
        properties.put("eclipselink.ddl-generation", "drop-and-create-tables");
        properties.put("eclipselink.target-database", "MYSQL");
        for (String name: childProperties.keySet()) {
            properties.put(name, childProperties.get(name));
        }
        System.clearProperty("hibernate.default_schema");
    }
    private String getProperty(final HashMap<String, Object> properties, String name, String hibernateName, String defaultValue) {
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

    private String getDbNameOrMem() {
        return getFilenamePrefix() == null ? "mem:test" : "file:" + System.getProperty("java.io.tmpdir")
                                                          + File.separatorChar
                                                          + getFilenamePrefix()
                                                          + ManagementFactory.getRuntimeMXBean().getName()
                                                          + count.incrementAndGet();
    }

    protected String getFilenamePrefix() {
        return null;
    }

    private void initHibernateProperties(final HashMap<String, Object> properties) {
        properties.put("javax.persistence.jdbc.driver","org.h2.Driver");
        String db = getDbNameOrMem();
        properties.put("javax.persistence.jdbc.url",
                "jdbc:h2:" + db + ";DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
        properties.put("javax.persistence.jdbc.user" , "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.archive.autodetection", "class");
        for (String name: childProperties.keySet()) {
            properties.put(name, childProperties.get(name));
        }
        if (properties.containsKey("hibernate.connection.url")) {
            properties.put("javax.persistence.jdbc.url",properties.get("hibernate.connection.url"));
        }

    }


    private List<String> getManagedClassNames() {
        final String entityBeanRegex = getEntityBeanRegex();
        List<String> result = new ArrayList<>();
        for (Class<?> c : getEjbExtensionExtended().getEntityClasses()) {
            if (entityBeanRegex == null || Pattern.matches(entityBeanRegex, c.getName()))
                result.add(c.getName());
        }
        logger.info("PUName: {} getManagedClassNames: {}", XmlLessPersistenceFactory.this.getPersistenceUnitName(), result);
        return result;
    }
    private List<URL> getJarFileUrls() {
        if (getEntityBeanRegex() == null) {
            try {
                final ArrayList<URL> jarFiles = Collections.list(this.getClass()
                        .getClassLoader()
                        .getResources(""));
                logger.info("PUName: {} getJarFileUrls: {}", XmlLessPersistenceFactory.this.getPersistenceUnitName(), jarFiles);
                return jarFiles;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Collections.emptyList();
        }
    }

    public URL getPersistenceUnitRootUrl(String resourcePath) {
        try {
            final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
            if (resources.hasMoreElements())
                return resources.nextElement();
            else
                return null;
        } catch (IOException e) {
            return null;
        }
    }


    public DataSource createDataSource() {
        BasicDataSource bds = createBasicDataSource();
        bds.setDriverClassName(getProperty(properties, "javax.persistence.jdbc.driver", "hibernate.connection.driverclass", "org.h2.Driver"));
        bds.setUrl(getProperty(properties, "javax.persistence.jdbc.url", "hibernate.connection.url", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000"));
        bds.setUsername(getProperty(properties, "javax.persistence.jdbc.user", "hibernate.connection.username", "sa"));
        bds.setPassword(getProperty(properties, "javax.persistence.jdbc.password", "hibernate.connection.password", ""));
        return checkAndDoInFirstConnection(bds);
    }

    public Properties getProperties() {
        Properties result = new Properties();
        for (Map.Entry<String, Object> e: properties.entrySet()) {
            result.setProperty(e.getKey(), e.getValue().toString());
        }
        return result;
    }



}
