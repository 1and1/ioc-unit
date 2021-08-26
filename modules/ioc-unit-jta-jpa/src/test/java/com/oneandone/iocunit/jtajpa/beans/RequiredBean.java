package com.oneandone.iocunit.jtajpa.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.oneandone.iocunit.jtajpa.TestBeanBase;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class RequiredBean extends TestBeanBase {
    public void callBean() {
        writing();
        System.out.println("RequiredBean");
    }
}
