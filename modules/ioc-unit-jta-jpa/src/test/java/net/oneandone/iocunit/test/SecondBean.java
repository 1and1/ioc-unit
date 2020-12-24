package net.oneandone.iocunit.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * @author aschoerk
 */
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class SecondBean {
    @Inject
    EntityManager entityManager;

    public void callSecondBean() {
        entityManager.createNativeQuery("Select 1");
        System.out.println("SecondBean");
    }
}
