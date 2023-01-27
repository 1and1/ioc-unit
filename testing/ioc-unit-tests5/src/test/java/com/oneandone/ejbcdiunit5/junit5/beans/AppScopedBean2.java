package com.oneandone.ejbcdiunit5.junit5.beans;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class AppScopedBean2 extends BaseBean {
    @Override
    public String toString() {
        return "AppScopedBean2{}";
    }
}
