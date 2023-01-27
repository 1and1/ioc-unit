package com.oneandone.iocunit.jtajpa.beans;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

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
