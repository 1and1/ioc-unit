package com.oneandone.iocunit.jtajpa;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author aschoerk
 */
public class TestContainer {
    public final static String TESTCONTAINERINITIALIZED = "TestContainerInitialized";
    public final static String DRIVERCLASSNAME = "DriverClassName";
    public final static String PASSWORD = "Password";
    public final static String USERNAME = "Username";
    public final static String JDBCURL = "JdbcUrl";

    public final Properties props = new Properties();

    private final Object container;

    public TestContainer(final Object sqlContainer) {
        this.container = sqlContainer;
        callVoidValue(container, "start");
        System.getProperties().put(TESTCONTAINERINITIALIZED, this);
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
}
