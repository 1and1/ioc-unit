package com.oneandone.iocunit.jtajpa.internal;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transaction;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;


/**
 * @author aschoerk
 */
public class EntityManagerDelegate implements EntityManager, Serializable {
    private static final long serialVersionUID = -6614196859383544873L;
    private final EntityManagerFactoryFactory factory;
    private final String puName;

    public EntityManagerDelegate(EntityManagerFactoryFactory factory, String puName) {
        this.factory = factory;
        this.puName = puName;
    }

    private EntityManager getEntityManager() {
        try {
            final Transaction transaction = TransactionImple.getTransaction();
            if (transaction != null) {
                return factory.getEntityManager(puName, true);
            } else {
                return factory.getTraLessEM(puName);
            }
        } catch (RuntimeException cex) {
            if(cex.getCause() != null && cex.getCause().getClass().getName().contains("ContextNotActiveException")) {
                return factory.getTraLessEM(puName);
            }
            else {
                throw cex;
            }
        }
    }

    private void needTransaction() {
        if (TransactionImple.getTransaction() == null) {
            throw new TransactionRequiredException("EntityManagerDelegate");
        }
    }

    private EntityManager clearIfNoTransaction(EntityManager em) {
        if (TransactionImple.getTransaction() == null) {
            em.clear();
        }
        return em;
    }

    @Override
    public void persist(final Object entity) {
        needTransaction();
        getEntityManager().persist(entity);
    }


    @Override
    public <T> T merge(final T entity) {
        needTransaction();
        return getEntityManager().merge(entity);
    }

    @Override
    public void remove(final Object entity) {
        needTransaction();
        getEntityManager().remove(entity);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey) {
        return clearIfNoTransaction(getEntityManager()).find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
        return clearIfNoTransaction(getEntityManager()).find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
        return clearIfNoTransaction(getEntityManager()).find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode, final Map<String, Object> properties) {
        return clearIfNoTransaction(getEntityManager()).find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
        return clearIfNoTransaction(getEntityManager()).getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public FlushModeType getFlushMode() {
        return getEntityManager().getFlushMode();
    }

    @Override
    public void setFlushMode(final FlushModeType flushMode) {
        getEntityManager().setFlushMode(flushMode);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode) {
        needTransaction();
        getEntityManager().lock(entity, lockMode);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        needTransaction();
        getEntityManager().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(final Object entity) {
        needTransaction();
        getEntityManager().refresh(entity);
    }

    @Override
    public void refresh(final Object entity, final Map<String, Object> properties) {
        needTransaction();
        getEntityManager().refresh(entity, properties);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode) {
        needTransaction();
        getEntityManager().refresh(entity, lockMode);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        needTransaction();
        getEntityManager().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        getEntityManager().clear();
    }

    @Override
    public void detach(final Object entity) {
        getEntityManager().detach(entity);
    }

    @Override
    public boolean contains(final Object entity) {
        return getEntityManager().contains(entity);
    }

    @Override
    public LockModeType getLockMode(final Object entity) {
        needTransaction();
        return getEntityManager().getLockMode(entity);
    }

    @Override
    public void setProperty(final String propertyName, final Object value) {
        getEntityManager().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return getEntityManager().getProperties();
    }

    @Override
    public Query createQuery(final String qlString) {
        return clearIfNoTransaction(getEntityManager()).createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        return clearIfNoTransaction(getEntityManager()).createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(final CriteriaUpdate updateQuery) {
        return clearIfNoTransaction(getEntityManager()).createQuery(updateQuery);
    }

    @Override
    public Query createQuery(final CriteriaDelete deleteQuery) {
        return clearIfNoTransaction(getEntityManager()).createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
        return clearIfNoTransaction(getEntityManager()).createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(final String name) {
        return clearIfNoTransaction(getEntityManager()).createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
        return clearIfNoTransaction(getEntityManager()).createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString) {
        return clearIfNoTransaction(getEntityManager()).createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final Class resultClass) {
        return clearIfNoTransaction(getEntityManager()).createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final String resultSetMapping) {
        return clearIfNoTransaction(getEntityManager()).createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(final String name) {
        return clearIfNoTransaction(getEntityManager()).createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName) {
        return clearIfNoTransaction(getEntityManager()).createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final Class... resultClasses) {
        return clearIfNoTransaction(getEntityManager()).createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final String... resultSetMappings) {
        return clearIfNoTransaction(getEntityManager()).createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        needTransaction();
        getEntityManager().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return getEntityManager().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(final Class<T> cls) {
        return getEntityManager().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return getEntityManager().getDelegate();
    }

    @Override
    public void close() {
      getEntityManager().close();
    }

    @Override
    public boolean isOpen() {
        return getEntityManager().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getEntityManager().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return getEntityManager().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(final Class<T> rootType) {
        return clearIfNoTransaction(getEntityManager()).createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(final String graphName) {
        return clearIfNoTransaction(getEntityManager()).createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(final String graphName) {
        return clearIfNoTransaction(getEntityManager()).getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> entityClass) {
        return clearIfNoTransaction(getEntityManager()).getEntityGraphs(entityClass);
    }
}
