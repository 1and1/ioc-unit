package com.oneandone.iocunit.jtajpa.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.oneandone.iocunit.jtajpa.TestBeanBase;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(Transactional.TxType.MANDATORY)
public class MandatoryBean extends TestBeanBase {

    @Transactional(Transactional.TxType.MANDATORY)
    public void callBean() {
        reading();
        writing();
        System.out.println("ReqNewBean");
    }


}
