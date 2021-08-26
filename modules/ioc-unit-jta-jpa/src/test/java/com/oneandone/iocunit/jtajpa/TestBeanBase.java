package com.oneandone.iocunit.jtajpa;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.oneandone.iocunit.jtajpa.beans.MandatoryBean;
import com.oneandone.iocunit.jtajpa.beans.NeverBean;
import com.oneandone.iocunit.jtajpa.beans.NotSuppBean;
import com.oneandone.iocunit.jtajpa.beans.ReqNewBean;
import com.oneandone.iocunit.jtajpa.beans.RequiredBean;
import com.oneandone.iocunit.jtajpa.beans.SupportsBean;
import com.oneandone.iocunit.jtajpa.helpers.TestEntity;

/**
 * @author aschoerk
 */
public class TestBeanBase {
    @Inject
    protected EntityManager entityManager;

    @Inject
    protected
    NotSuppBean notSuppBean;

    @Inject
    protected
    SupportsBean supportsBean;

    @Inject
    protected MandatoryBean mandatoryBean;

    @Inject
    protected
    NeverBean neverBean;

    @Inject
    protected
    RequiredBean requiredBean;

    @Inject
    protected
    ReqNewBean reqNewBean;

    public void callNotSuppBean() {
        this.notSuppBean.callBean();
    }

    public void callSupportsBean() {
        this.supportsBean.callBean();
    }

    public void callMandatoryBean() {
        this.mandatoryBean.callBean();
    }

    public void callNeverBean() {
        this.neverBean.callBean();
    }

    public void callRequiredBean() {
        this.requiredBean.callBean();
    }

    public void callReqNewBean() {
        this.reqNewBean.callBean();
    }

    protected void reading() {
        entityManager.createNativeQuery("Select 1");
    }

    protected void writing() {
        TestEntity testEntity = new TestEntity();
        entityManager.persist(testEntity);
        entityManager.createNativeQuery("Select 1");
    }

    static class TestFactory extends JtaEntityManagerFactoryBase {
        @Override
        public String getPersistenceUnitName() {
            return "test";
        }

        @Override
        @Produces
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }
    }

}



