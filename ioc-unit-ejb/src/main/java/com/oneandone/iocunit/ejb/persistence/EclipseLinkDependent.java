package com.oneandone.iocunit.ejb.persistence;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;

/**
 * @author aschoerk
 */
public class EclipseLinkDependent {
    static public void addPersistenceUnitInfoToProperties(HashMap<String, Object> properties, PersistenceUnitInfo pu) {
        properties.put("eclipselink.se-puinfo", new SEPersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return pu.getPersistenceUnitName();
            }
            @Override
            public DataSource getJtaDataSource() {
                return pu.getJtaDataSource();
            }

            /**
             * take the first URL found, which supports Entities
             * @return the first URL found, depending on EntityClasses
             */
            @Override
            public URL getPersistenceUnitRootUrl() {
                return pu.getPersistenceUnitRootUrl();
            }

            /**
             * @return
             */
            @Override
            public List<URL> getJarFileUrls() {
                return pu.getJarFileUrls();
            }

            @Override
            public List<String> getManagedClassNames() {
                return pu.getManagedClassNames();
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return pu.getNonJtaDataSource();
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return pu.excludeUnlistedClasses();
            }
            @Override
            public ClassLoader getClassLoader() {
                return pu.getClassLoader();
            }
        });

    }
}
