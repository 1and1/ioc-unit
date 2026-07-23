package com.oneandone.iocunit.ejb.persistence;

import java.util.List;
import java.util.Map;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.ConnectionConsumer;
import jakarta.persistence.ConnectionFunction;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FindOption;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.Query;
import jakarta.persistence.RefreshOption;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;

/**
 * Used to delegate EntityManager actions to the current EntityManager of the Thread, as it is defined according
 * to Initialization and Transaction-Context
 * Created by aschoerk2 on 3/2/14.
 */
@SuppressWarnings("ClassWithTooManyMethods")
class EntityManagerDelegate implements EntityManager {

    private final PersistenceFactory entityManagerStore;
    private final JdbcSqlConverter converter;

    EntityManagerDelegate(PersistenceFactory entityManagerStore, final JdbcSqlConverter c) {
        this.entityManagerStore = entityManagerStore;
        this.converter = c;
    }

    private EntityManager getEmbeddedEntityManager() {
        /*
         * make sure the transaction context is correctly started, if necessary, then return the workable EntityManager
         * of the thread.
         */
        try {
            return entityManagerStore.getTransactional(false);
        } catch (TransactionRequiredException e) {
            throw new RuntimeException("not expected exception: ", e);
        }
    }


    private EntityManager getEmbeddedEntityManager(boolean expectTransaction) {
        /*
         * make sure the transaction context is correctly started, if necessary, then return the workable EntityManager of the thread.
         */
        return entityManagerStore.getTransactional(expectTransaction);
    }

    @Override
    public void persist(final Object entity) {
        getEmbeddedEntityManager(true).persist(entity);
    }

    @Override
    public <T> T merge(final T entity) {
        return getEmbeddedEntityManager(true).merge(entity);
    }

    @Override
    public void remove(final Object entity) {
        getEmbeddedEntityManager(true).remove(entity);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey) {
        return getEmbeddedEntityManager().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
        return getEmbeddedEntityManager().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
        return getEmbeddedEntityManager().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode,
                      final Map<String, Object> properties) {
        return getEmbeddedEntityManager().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T find(final Class<T> entityClass, final Object primaryKey, final FindOption... options) {
        return getEmbeddedEntityManager().find(entityClass, primaryKey, options);
    }

    @Override
    public <T> T find(final EntityGraph<T> entityGraph, final Object primaryKey, final FindOption... options) {
        return getEmbeddedEntityManager().find(entityGraph, primaryKey, options);
    }

    @Override
    public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
        return getEmbeddedEntityManager().getReference(entityClass, primaryKey);
    }

    @Override
    public <T> T getReference(final T entity) {
        return getEmbeddedEntityManager().getReference(entity);
    }

    @Override
    public void flush() {
        getEmbeddedEntityManager(true).flush();
    }

    @Override
    public FlushModeType getFlushMode() {
        return getEmbeddedEntityManager().getFlushMode();
    }

    @Override
    public void setFlushMode(final FlushModeType flushMode) {
        getEmbeddedEntityManager().setFlushMode(flushMode);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode) {
        getEmbeddedEntityManager(true).lock(entity, lockMode);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        getEmbeddedEntityManager(true).lock(entity, lockMode, properties);
    }

    @Override
    public void lock(final Object entity, final LockModeType lockMode, final LockOption... lockOptions) {
        getEmbeddedEntityManager(true).lock(entity, lockMode, lockOptions);
    }

    @Override
    public void refresh(final Object entity) {
        getEmbeddedEntityManager(true).refresh(entity);
    }

    @Override
    public void refresh(final Object entity, final Map<String, Object> properties) {
        getEmbeddedEntityManager(true).refresh(entity, properties);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode) {
        getEmbeddedEntityManager(true).refresh(entity, lockMode);
    }

    @Override
    public void refresh(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        getEmbeddedEntityManager(true).refresh(entity, lockMode, properties);
    }

    @Override
    public void refresh(final Object entity, final RefreshOption... refreshOptions) {
        getEmbeddedEntityManager(true).refresh(entity, refreshOptions);
    }

    @Override
    public void clear() {
        getEmbeddedEntityManager().clear();
    }

    @Override
    public void detach(final Object entity) {
        getEmbeddedEntityManager().detach(entity);
    }

    @Override
    public boolean contains(final Object entity) {
        return getEmbeddedEntityManager().contains(entity);
    }

    @Override
    public LockModeType getLockMode(final Object entity) {
        return getEmbeddedEntityManager(true).getLockMode(entity);
    }

    @Override
    public void setCacheRetrieveMode(final CacheRetrieveMode cacheRetrieveMode) {
        getEmbeddedEntityManager().setCacheRetrieveMode(cacheRetrieveMode);
    }

    @Override
    public void setCacheStoreMode(final CacheStoreMode cacheStoreMode) {
        getEmbeddedEntityManager().setCacheStoreMode(cacheStoreMode);
    }

    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        return getEmbeddedEntityManager().getCacheRetrieveMode();
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        return getEmbeddedEntityManager().getCacheStoreMode();
    }

    @Override
    public void setProperty(final String propertyName, final Object value) {
        getEmbeddedEntityManager().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return getEmbeddedEntityManager().getProperties();
    }

    @Override
    public Query createQuery(final String qlString) {
        return getEmbeddedEntityManager().createQuery(converter.convert(qlString));
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        return getEmbeddedEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final CriteriaSelect<T> criteriaSelect) {
        return getEmbeddedEntityManager().createQuery(criteriaSelect);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final TypedQueryReference<T> typedQueryReference) {
        return getEmbeddedEntityManager().createQuery(typedQueryReference);
    }

    @Override
    public Query createQuery(CriteriaUpdate criteriaUpdate) {
        return getEmbeddedEntityManager().createQuery(criteriaUpdate);
    }

    @Override
    public Query createQuery(CriteriaDelete criteriaDelete) {
        return getEmbeddedEntityManager().createQuery(criteriaDelete);
    }

    @Override
    public <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
        return getEmbeddedEntityManager().createQuery(converter.convert(qlString), resultClass);
    }

    @Override
    public Query createNamedQuery(final String name) {
        return getEmbeddedEntityManager().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
        return getEmbeddedEntityManager().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString) {
        return getEmbeddedEntityManager().createNativeQuery(converter.convert(sqlString));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Query createNativeQuery(final String sqlString, final Class resultClass) {
        return getEmbeddedEntityManager().createNativeQuery(converter.convert(sqlString), resultClass);
    }

    @Override
    public Query createNativeQuery(final String sqlString, final String resultSetMapping) {
        return getEmbeddedEntityManager().createNativeQuery(converter.convert(sqlString), resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return getEmbeddedEntityManager().createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return getEmbeddedEntityManager().createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class[] resultClasses) {
        return getEmbeddedEntityManager().createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        return getEmbeddedEntityManager().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        getEmbeddedEntityManager().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return getEmbeddedEntityManager().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(final Class<T> cls) {
        return getEmbeddedEntityManager().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return getEmbeddedEntityManager().getDelegate();
    }

    @Override
    public void close() {
        getEmbeddedEntityManager().close();
    }

    @Override
    public boolean isOpen() {
        return getEmbeddedEntityManager().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return new SimulatedEntityTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getEmbeddedEntityManager().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEmbeddedEntityManager().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return getEmbeddedEntityManager().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return getEmbeddedEntityManager().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return getEmbeddedEntityManager().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return getEmbeddedEntityManager().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return getEmbeddedEntityManager().getEntityGraphs(entityClass);
    }

    @Override
    public <C> void runWithConnection(final ConnectionConsumer<C> action) {
        getEmbeddedEntityManager().runWithConnection(action);
    }

    @Override
    public <C, T> T callWithConnection(final ConnectionFunction<C, T> function) {
        return getEmbeddedEntityManager().callWithConnection(function);
    }

}
