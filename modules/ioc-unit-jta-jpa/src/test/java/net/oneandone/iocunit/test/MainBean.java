package net.oneandone.iocunit.test;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(REQUIRED)
public class MainBean {
    @Inject
    EntityManager entityManager;

    @Inject
    SecondBean secondBean;

    public void call() {
        entityManager.createNativeQuery("Select 1");
        secondBean.callSecondBean();
    }
}
