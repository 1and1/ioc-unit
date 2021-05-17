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
import javax.transaction.Status;
import javax.transaction.SystemException;
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
            if(transaction != null) {
                return factory.getEntityManager(puName, true);
            }
            else {
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
        if(TransactionImple.getTransaction() == null) {
            throw new TransactionRequiredException("EntityManagerDelegate");
        }
    }

    public void clearIfNoTransaction() {
        if(TransactionImple.getTransaction() == null) {
            clear();
        }
        else {
            try {
                int status = TransactionImple.getTransaction().getStatus();
                switch (status) {
                    case Status
                            .STATUS_COMMITTED:
                    case Status
                            .STATUS_ROLLEDBACK:
                    case Status
                            .STATUS_NO_TRANSACTION:
                        clear();
                    default:
                        ;

                }
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }
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
        clearIfNoTransaction();
        return getEntityManager().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
        clearIfNoTransaction();
        return getEntityManager().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
        clearIfNoTransaction();
        return getEntityManager().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode, final Map<String, Object> properties) {
        clearIfNoTransaction();
        return getEntityManager().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
        clearIfNoTransaction();
        return getEntityManager().getReference(entityClass, primaryKey);
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
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createQuery(qlString), this);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        clearIfNoTransaction();
        return new TypedQueryDelegate(getEntityManager().createQuery(criteriaQuery), this);
    }

    @Override
    public Query createQuery(final CriteriaUpdate updateQuery) {
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createQuery(updateQuery), this);
    }

    @Override
    public Query createQuery(final CriteriaDelete deleteQuery) {
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createQuery(deleteQuery), this);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
        clearIfNoTransaction();
        return new TypedQueryDelegate(getEntityManager().createQuery(qlString, resultClass), this);
    }

    @Override
    public Query createNamedQuery(final String name) {
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createNamedQuery(name), this);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
        clearIfNoTransaction();
        return new TypedQueryDelegate(getEntityManager().createNamedQuery(name, resultClass), this);
    }

    @Override
    public Query createNativeQuery(final String sqlString) {
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createNativeQuery(sqlString), this);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final Class resultClass) {
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createNativeQuery(sqlString, resultClass), this);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final String resultSetMapping) {
        clearIfNoTransaction();
        return new QueryDelegate(getEntityManager().createNativeQuery(sqlString, resultSetMapping), this);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(final String name) {
        clearIfNoTransaction();
        return new StoredProcedureQueryDelegate(getEntityManager().createNamedStoredProcedureQuery(name), this);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName) {
        clearIfNoTransaction();
        return new StoredProcedureQueryDelegate(getEntityManager().createStoredProcedureQuery(procedureName), this);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final Class... resultClasses) {
        clearIfNoTransaction();
        return new StoredProcedureQueryDelegate(getEntityManager().createStoredProcedureQuery(procedureName, resultClasses), this);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final String... resultSetMappings) {
        clearIfNoTransaction();
        return new StoredProcedureQueryDelegate(getEntityManager().createStoredProcedureQuery(procedureName, resultSetMappings), this);
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
        clearIfNoTransaction();
        return getEntityManager().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(final String graphName) {
        clearIfNoTransaction();
        return getEntityManager().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(final String graphName) {
        clearIfNoTransaction();
        return getEntityManager().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> entityClass) {
        clearIfNoTransaction();
        return getEntityManager().getEntityGraphs(entityClass);
    }
}
