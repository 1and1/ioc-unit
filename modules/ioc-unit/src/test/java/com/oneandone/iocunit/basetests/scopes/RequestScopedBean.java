package com.oneandone.iocunit.basetests.scopes;

import jakarta.enterprise.context.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
@RequestScoped
public class RequestScopedBean {
    Logger logger = LoggerFactory.getLogger(RequestScopedBean.class);
    private static int counter = 0;
    private int localVariable = ++counter;

    public int getLocalVariable() {
        return localVariable;
    }

    public RequestScopedBean() {
        logger.info("creating RequestScoped {}", localVariable);
    }
}
