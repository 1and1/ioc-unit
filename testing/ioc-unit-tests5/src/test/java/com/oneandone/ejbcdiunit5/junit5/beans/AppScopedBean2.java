package com.oneandone.ejbcdiunit5.junit5.beans;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class AppScopedBean2 extends BaseBean {
    public static int APPSCOPED_BEAN_INIT_VALUE = 4711;

    int value = APPSCOPED_BEAN_INIT_VALUE;

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AppScopedBean2{}";
    }
}
