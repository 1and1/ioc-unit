package com.oneandone.iocunit.jtajpa.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.oneandone.iocunit.jtajpa.TestBeanBase;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class SupportsBean extends TestBeanBase {

    public void callBean() {
        reading();
        System.out.println("supportsBean");
    }
}
