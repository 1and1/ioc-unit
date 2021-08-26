package com.oneandone.iocunit.jtajpa.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.oneandone.iocunit.jtajpa.TestBeanBase;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(Transactional.TxType.NEVER)
public class NeverBean extends TestBeanBase {

    public void callBean() {
        reading();
        System.out.println("ReqNewBean");
    }
}
