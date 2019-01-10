package com.oneandone.ejbcdiunit;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.oneandone.ejbcdiunit.cdiunit.Pu1Em;
import com.oneandone.ejbcdiunit.cdiunit.Pu2Em;
import com.oneandone.ejbcdiunit.entities.TestEntity1;

/**
 * @author aschoerk
 */
public class ClassWithTwoDifferentEntityManagers {

    @Pu1Em
    @Inject
    private EntityManager pu1Em;

    @Pu2Em
    @Inject
    private EntityManager pu2Em;

    /**
     * Kind of dao-method able to use two different entitymanagers
     * @param useEm1 either em1 or em2
     * @param intValue the intAttribute to search Testentitys for
     * @return the single TestEntity1
     */
    public TestEntity1 readEntities(boolean useEm1, int intValue) {
        EntityManager em = useEm1 ? pu1Em : pu2Em;

        return em.createQuery("select e from TestEntity1 e where e.intAttribute = :intValue", TestEntity1.class)
                .setParameter("intValue", intValue).getSingleResult();
    }

}
