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
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory.EntityManagerWrapper;


/**
 * @author aschoerk
 */
public class EntityManagerDelegate implements EntityManager, Serializable {
    private static final long serialVersionUID = -6614196859383544873L;
    private final EntityManagerFactoryFactory factory;
    private final String puName;
    ThreadLocal<EntityManagerWrapper> entityManager = new ThreadLocal<>();
    ThreadLocal<EntityManager> tmpEntityManager = new ThreadLocal<>();

    public EntityManagerDelegate(EntityManagerFactoryFactory factory, String puName) {
        this.factory = factory;
        this.puName = puName;
    }

    private EntityManager getEntityManager() {
        try {
            if(entityManager.get() == null || entityManager.get().getEntityManager() == null) {
                final Transaction transaction = TransactionImple.getTransaction();
                if(transaction != null &&
                   transaction.getStatus() == Status.STATUS_ACTIVE) {
                    entityManager.set(factory.getEntityManager(puName, true));
                    return entityManager.get().getEntityManager();
                }
                else {
                    tmpEntityManager.set(factory.getEntityManager(puName, false).getEntityManager());
                    return tmpEntityManager.get();
                }
            }
            else {
                entityManager.get().getEntityManager().isJoinedToTransaction();
            }
        } catch (SystemException sex) {
            throw new RuntimeException(sex);
        } catch (RuntimeException cex) {
            if(cex.getCause().getClass().getName().contains("ContextNotActiveException")) {
                if(tmpEntityManager == null) {
                    tmpEntityManager.set(factory.getEntityManager(puName, false).getEntityManager());
                }
                return tmpEntityManager.get();
            }
            else {
                throw cex;
            }
        }
        return entityManager.get().getEntityManager();
    }

    @Override
    public void persist(final Object entity) {
        getEntityManager().persist(entity);
    }


    @Override
    public <T> T merge(final T entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public void remove(final Object entity) {
        getEntityManager().remove(entity);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey) {
        return getEntityManager().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
        return getEntityManager().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
        return getEntityManager().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode, final Map<String, Object> properties) {
        return getEntityManager().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
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
        getEntityManager().lock(entity, lockMode);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        getEntityManager().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(final Object entity) {
        getEntityManager().refresh(entity);
    }

    @Override
    public void refresh(final Object entity, final Map<String, Object> properties) {
        getEntityManager().refresh(entity, properties);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode) {
        getEntityManager().refresh(entity, lockMode);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
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
        return getEntityManager().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(final CriteriaUpdate updateQuery) {
        return getEntityManager().createQuery(updateQuery);
    }

    @Override
    public Query createQuery(final CriteriaDelete deleteQuery) {
        return getEntityManager().createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
        return getEntityManager().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(final String name) {
        return getEntityManager().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
        return getEntityManager().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString) {
        return getEntityManager().createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final Class resultClass) {
        return getEntityManager().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final String resultSetMapping) {
        return getEntityManager().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(final String name) {
        return getEntityManager().createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName) {
        return getEntityManager().createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final Class... resultClasses) {
        return getEntityManager().createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final String... resultSetMappings) {
        return getEntityManager().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
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
        if(tmpEntityManager.get() != null) tmpEntityManager.get().close();
        tmpEntityManager.set(null);
        if(entityManager.get() != null) {
            if(entityManager.get().getEntityManager() != null) {
                entityManager.get().getEntityManager().close();
            }
            entityManager.get().clrEntityManager();
        }
        entityManager.set(null);
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
        return getEntityManager().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(final String graphName) {
        return getEntityManager().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(final String graphName) {
        return getEntityManager().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> entityClass) {
        return getEntityManager().getEntityGraphs(entityClass);
    }
}
