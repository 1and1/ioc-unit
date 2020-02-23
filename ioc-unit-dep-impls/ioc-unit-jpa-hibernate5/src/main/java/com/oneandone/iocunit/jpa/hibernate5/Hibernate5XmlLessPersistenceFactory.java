package com.oneandone.iocunit.jpa.hibernate5;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jpa.jpa.PersistenceFactory;

/**
 * @author aschoerk
 */
public class Hibernate5XmlLessPersistenceFactory {
    static Logger logger = LoggerFactory.getLogger("XmlLessPersistenceFactory");

    HashMap<String, Object> properties = new HashMap<>();

    HashMap<String, Object> childProperties = new HashMap<>();

    public void addProperty(String name, Object property) {
        childProperties.put(name, property);
    }

    AtomicInteger count = new AtomicInteger(0);

    private String getProperty(final HashMap<String, Object> propertiesP, String name, String hibernateName, String defaultValue) {
        Object propValue = propertiesP.get(name);
        if (propValue != null) return propValue.toString();
        propValue = System.getProperty(name);
        if (propValue != null)
            return propValue.toString();
        propValue = propertiesP.get(hibernateName);
        if (propValue != null) return propValue.toString();
        propValue = System.getProperty(hibernateName);
        if (propValue != null)
            return propValue.toString();

        return defaultValue;
    }

    private List<URL> getJarFileUrls(PersistenceFactory p) {
        try {
            final ArrayList<URL> jarFiles = Collections.list(this.getClass()
                    .getClassLoader()
                    .getResources(""));
            logger.info("PUName: {} getJarFileUrls: {}", p.getPersistenceUnitName(), jarFiles);
            return jarFiles;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    protected PersistenceUnitInfo getHibernatePersistenceUnitInfo(final HashMap<String, Object> properties, final PersistenceFactory p) {

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

            public DataSource createDataSource() {
                BasicDataSource bds = p.createBasicDataSource();
                bds.setDriverClassName(getProperty(properties, "javax.persistence.jdbc.driver", "hibernate.connection.driverclass", "org.h2.Driver"));
                bds.setUrl(getProperty(properties, "javax.persistence.jdbc.url", "hibernate.connection.url", "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000"));
                bds.setUsername(getProperty(properties, "javax.persistence.jdbc.user", "hibernate.connection.username", "sa"));
                bds.setPassword(getProperty(properties, "javax.persistence.jdbc.password", "hibernate.connection.password", ""));
                return p.checkAndDoInFirstConnection(bds);
            }


            @Override
            public DataSource getNonJtaDataSource() {
                if (datasource == null)
                    datasource = createDataSource();
                return p.checkAndDoInFirstConnection(datasource);
            }

            @Override
            public List<String> getMappingFileNames() {
                return Collections.emptyList();
            }

            @Override
            public List<URL> getJarFileUrls() {
                return Hibernate5XmlLessPersistenceFactory.this.getJarFileUrls(p);
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null; // TestPersistenceFactory.this.getPersistenceUnitRootUrl("META-INF/persistence.xml");
            }

            @Override
            public List<String> getManagedClassNames() {
                return Hibernate5XmlLessPersistenceFactory.this.getManagedClassNames(p);
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
                return Hibernate5XmlLessPersistenceFactory.this.getEntityBeanRegex(p) != null;
            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return this.getClassLoader();
            }
        };
    }

    public String getEntityBeanRegex(PersistenceFactory p) {
        return null;
    }


    private List<String> getManagedClassNames(final PersistenceFactory p) {
        return new ArrayList<>();  // TODO;
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
    private void initHibernateProperties(final HashMap<String, Object> propertiesP) {
        propertiesP.put("javax.persistence.jdbc.driver","org.h2.Driver");
        String db = getDbNameOrMem();
        propertiesP.put("javax.persistence.jdbc.url",
                "jdbc:h2:" + db + ";DB_CLOSE_ON_EXIT=TRUE;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
        propertiesP.put("javax.persistence.jdbc.user" , "sa");
        propertiesP.put("javax.persistence.jdbc.password", "");
        propertiesP.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        propertiesP.put("hibernate.show_sql", true);
        propertiesP.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform");
        propertiesP.put("hibernate.hbm2ddl.auto", "create-drop");
        propertiesP.put("hibernate.id.new_generator_mappings", false);
        propertiesP.put("hibernate.archive.autodetection", "class");
        for (String name: childProperties.keySet()) {
            propertiesP.put(name, childProperties.get(name));
        }
        if (propertiesP.containsKey("hibernate.connection.url")) {
            propertiesP.put("javax.persistence.jdbc.url",propertiesP.get("hibernate.connection.url"));
        }

    }
    /**
     * Possibility to set certain properties as they are normally defined by persistence.xml
     *
     * @param propertiesP to be used to overwrite the property which are used as defaults.
     */
    public void overwritePersistenceProperties(Properties propertiesP) {
        for (Map.Entry<Object, Object> p : propertiesP.entrySet()) {
            this.properties.put((String) p.getKey(), p.getValue());
        }
    }


    public EntityManagerFactory createEntityManagerFactoryWOPersistenceXml(PersistenceFactory p, HashMap<String, Object> properties) {

        EntityManagerFactory result;
        initHibernateProperties(properties);
        // possibly override properties using system properties
        overwritePersistenceProperties(System.getProperties());
        final PersistenceUnitInfo persistenceUnitInfo = getHibernatePersistenceUnitInfo(properties, p);
        result = HibernateDependent.createFromPersistenceUnit(persistenceUnitInfo, properties);
        return result;
    }
}
