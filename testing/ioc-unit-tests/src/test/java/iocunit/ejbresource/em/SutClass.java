package iocunit.ejbresource.em;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import iocunit.ejbresource.em.pu1.Entity1;
import iocunit.ejbresource.em.pu2.Entity2;

/**
 * @author aschoerk
 */
@Stateless
public class SutClass {
    @Inject
    @PUQual1
    EntityManager em1;

    @Inject
    @PUQual2
    EntityManager em2;

    @Inject
    EntityManager em3_1;

    @PersistenceContext(name = "pu3")  // in Testcode qualifiable by PersistenceContextQualifier
    EntityManager em3_2;

    @PersistenceContext(name = "pu1")  // in Testcode qualifiable by PersistenceContextQualifier
    EntityManager em1_2;

    @PersistenceContext(name = "pu2")
    EntityManager em2_2;

    @Resource
    SessionContext sessionContext;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    void createTables() {
        em1.createNativeQuery("drop table t if exists").executeUpdate();
        em1.createNativeQuery("create table t (a varchar)").executeUpdate();
        em1.createNativeQuery("drop table em1 if exists").executeUpdate();
        em1.createNativeQuery("create table em1 (a varchar)").executeUpdate();
        em2.createNativeQuery("drop table t if exists").executeUpdate();
        em2.createNativeQuery("create table t (a varchar)").executeUpdate();
        em2.createNativeQuery("drop table em2 if exists").executeUpdate();
        em2.createNativeQuery("create table em2 (a varchar)").executeUpdate();
        em3_1.createNativeQuery("drop table t if exists").executeUpdate();
        em3_1.createNativeQuery("create table t (a varchar)").executeUpdate();
        em3_1.createNativeQuery("drop table em3 if exists").executeUpdate();
        em3_1.createNativeQuery("create table em3 (a varchar)").executeUpdate();
    }

    public void workNative() {
        sessionContext.getBusinessObject(SutClass.class).createTables();
        em1.createNativeQuery("insert into t (a) values ('test')").executeUpdate();
        em1_2.createNativeQuery("insert into em1 (a) values ('testem1')").executeUpdate();
        em2.createNativeQuery("insert into t (a) values ('test')").executeUpdate();
        em2_2.createNativeQuery("insert into em2 (a) values ('testem2')").executeUpdate();
        em3_1.createNativeQuery("insert into t (a) values ('test1')").executeUpdate();
        em3_2.createNativeQuery("insert into t (a) values ('test2')").executeUpdate();
    }

    public void workWithEntities() {
        sessionContext.getBusinessObject(SutClass.class).createTables();
        Entity1 entity1 = new Entity1("em1");
        em1.persist(entity1);
        Entity2 entity2 = new Entity2("em2");
        em2.persist(entity2);
        Entity3 entity3 = new Entity3("em3");
        em3_1.persist(entity3);
    }







}
