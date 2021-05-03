package com.oneandone.iocunit.jtajpa;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;

import com.arjuna.ats.jdbc.TransactionalDriver;
import com.oneandone.iocunit.jtajpa.internal.ConnectionProviderBase;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

/**
 * @author aschoerk
 */
public class PersistenceXmlConnectionProvider extends ConnectionProviderBase {
    private static final long serialVersionUID = -3816330716215256749L;
    AtomicInteger connectionCount = new AtomicInteger();
    private String url;
    private TransactionalDriver arjunaJDBC2Driver = null;
    private Properties dbProps = null;

    public PersistenceXmlConnectionProvider() {
        String puName = EntityManagerFactoryFactory.currentPuName.get();
        if(puName == null) {
            throw new RuntimeException("Expected JtaEntityManagerFactoryBase to be defined with persistence-unit-name and entitymanager-Producer.");
        }
        Map<String, String> tmp = new HashMap<>();
        List<ParsedPersistenceXmlDescriptor> units = PersistenceXmlParser.locatePersistenceUnits(tmp);
        Optional<ParsedPersistenceXmlDescriptor> pu = units.stream().filter(u -> u.getName().equals(puName)).findFirst();
        if(!pu.isPresent()) {
            throw new RuntimeException("Persistenceunit: " + puName + " not found");
        }
        ParsedPersistenceXmlDescriptor descriptor = pu.get();
        Properties props = descriptor.getProperties();
        try {
            url = (String) props.get("javax.persistence.jdbc.url");
            String userName = (String) props.get("javax.persistence.jdbc.user");
            String password = (String) props.get("javax.persistence.jdbc.password");
            String driverName = (String) props.get("javax.persistence.jdbc.driver");
            if(driverName.equals(TestContainer.class.getName())) {
                TestContainer testContainer = (TestContainer) System.getProperties().get(TestContainer.TESTCONTAINERINITIALIZED);
                userName = testContainer.getUsername();
                password = testContainer.getPassword();
                url = testContainer.getJdbcUrl();
                driverName = testContainer.getDriverclassname();
            }
            Class dsClass = Class.forName(getDatasourceClassName(driverName));
            Method setUrlMethod = getSetUrlMethod(dsClass);
            Constructor constructor = dsClass.getDeclaredConstructor();
            Object jdbcDataSource = constructor.newInstance();
            // keep db open when all connections are closed!!
            setUrlMethod.invoke(jdbcDataSource, url);
            dbProps = new Properties();
            dbProps.put(TransactionalDriver.userName, userName);
            dbProps.put(TransactionalDriver.password, password);
            dbProps.put(TransactionalDriver.XADataSource, jdbcDataSource);
            dbProps.put(TransactionalDriver.poolConnections, "false");

            this.arjunaJDBC2Driver = new TransactionalDriver();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    String getDatasourceClassName(String drivername) {
        if(drivername.contains(".h2.")) {
            return "org.h2.jdbcx.JdbcDataSource";
        }
        if(drivername.contains(".postgresql.")) {
            return "org.postgresql.xa.PGXADataSource";
        }
        if(drivername.contains(".mariadb.")) {
            return "org.mariadb.jdbc.MariaDbDataSource";
        }
        throw new RuntimeException("XA Datasource Classname for Driver: " + drivername + " not found.");
    }

    private Method getSetUrlMethod(final Class dsClass) throws NoSuchMethodException {
        if(dsClass.equals(Object.class)) {
            throw new RuntimeException("SetUrl-Method not found in datasource class");
        }
        try {
            return dsClass.getDeclaredMethod("setUrl", String.class);
        } catch (NoSuchMethodException nme) {
            return getSetUrlMethod(dsClass.getSuperclass());
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        Connection result;
        result = arjunaJDBC2Driver.connect("jdbc:arjuna:" + url, dbProps);
        connectionCount.incrementAndGet();
        return result;
    }

    @Override
    public void closeConnection(final Connection connection) throws SQLException {
        try {
            super.closeConnection(connection);
        } finally {
            int count = connectionCount.decrementAndGet();
        }
    }
}
