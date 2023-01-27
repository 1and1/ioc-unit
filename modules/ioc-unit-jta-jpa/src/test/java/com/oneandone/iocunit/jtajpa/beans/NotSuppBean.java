package com.oneandone.iocunit.jtajpa.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import com.oneandone.iocunit.jtajpa.TestBeanBase;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class NotSuppBean extends TestBeanBase {

    public void callBean() {
        reading();
        System.out.println("ReqNewBean");
    }
}
