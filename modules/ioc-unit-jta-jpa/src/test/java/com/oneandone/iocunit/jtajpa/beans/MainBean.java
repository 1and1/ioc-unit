package com.oneandone.iocunit.jtajpa.beans;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.oneandone.iocunit.jtajpa.TestBeanBase;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(REQUIRED)
public class MainBean extends TestBeanBase {

    public void callReqNew() {
        entityManager.createNativeQuery("Select 1");
        reqNewBean.callBean();
    }
}
