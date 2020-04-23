package iocunit.ejbresource.em;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author aschoerk
 */
public class SutResources {
    @Produces
    @PUQual1
    @PersistenceContext(name = "pu1")  // no PersistenceContextQualifier here, because of Produces
                                       // TestProducer can use @PUQual1 to address this
    EntityManager em1;

    @Produces
    @PUQual2
    @PersistenceContext(name = "pu2") // no PersistenceContextQualifier here, because of Produces.
                                      // TestProducer can use @PUQual2 to address this
    EntityManager em2;

    @Produces
    @PersistenceContext(name = "pu3") // no PersistenceContextQualifier here, because of Produces, no qualifier at all
    EntityManager em3;

}


