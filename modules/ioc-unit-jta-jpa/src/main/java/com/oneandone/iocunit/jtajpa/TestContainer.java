package com.oneandone.iocunit.jtajpa;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

/**
 * @author aschoerk
 */

/**
 * used to signal PersistenceXmlConnectionProvider, that a Testcontainer is used.
 * and used to start a db-Testcontainer by the test.
 */
@ApplicationScoped
public class TestContainer implements Driver {
    public final static String DRIVERCLASSNAME = "DriverClassName";
    public final static String PASSWORD = "Password";
    public final static String USERNAME = "Username";
    public final static String JDBCURL = "JdbcUrl";

    private final Object container;

    public TestContainer(final Object sqlContainer) {
        this.container = sqlContainer;
        callVoidValue(container, "start");
    }

    public Method getVoidMethod(Class c, String name) {
        if (c.equals(Object.class))
            throw new RuntimeException("Method " + name + " not found");
        try {
            return c.getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            return getVoidMethod(c.getSuperclass(), name);
        }
    }

    public Object getValue(Object o, String name) {
        try {
            return getVoidMethod(o.getClass(), "get" + name).invoke(o);
        } catch (Exception e) {
            throw new RuntimeException("Could not get Value of " + name + " from Object of class: " + o.getClass());
        }
    }

    public void callVoidValue(Object o, String name) {
        try {
            getVoidMethod(o.getClass(), name).invoke(o);
        } catch (Exception e) {
            throw new RuntimeException("Could not call Void method " + name + " at Object of class: " + o.getClass());
        }
    }

    public String getUsername() {
        return (String)getValue(container, USERNAME);
    }
    public String getPassword() {
        return (String)getValue(container, PASSWORD);
    }
    public String getJdbcUrl() {
        return (String)getValue(container, JDBCURL);
    }
    public String getDriverclassname() {
        return (String)getValue(container, DRIVERCLASSNAME);
    }

    public void start() {
        callVoidValue(container,"start");
    }

    public void stop() {
        callVoidValue(container,"stop");
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }

    @Override
    public int getMajorVersion() {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }

    @Override
    public int getMinorVersion() {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }

    @Override
    public boolean jdbcCompliant() {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new IllegalAccessError("TestContainer Driver should never get used directly");
    }
}
