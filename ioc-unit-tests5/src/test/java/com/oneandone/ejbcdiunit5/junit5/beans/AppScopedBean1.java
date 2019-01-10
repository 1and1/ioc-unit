package com.oneandone.ejbcdiunit5.junit5.beans;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class AppScopedBean1 extends BaseBean {
    public static int APPSCOPED_BEAN_INIT_VALUE = 2018;

    int value = APPSCOPED_BEAN_INIT_VALUE;

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AppScopedBean1{}";
    }
}
