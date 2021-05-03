package com.oneandone.iocunit.jtajpa.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public class QueryDelegate implements Query {
    private final Query query;
    private EntityManagerDelegate entityManagerDelegate;

    public QueryDelegate(Query query, EntityManagerDelegate entityManagerDelegate) {
        this.query = query;
        this.entityManagerDelegate = entityManagerDelegate;
    }

    public List getResultList() {
        List l = query.getResultList();
        this.entityManagerDelegate.clearIfNoTransaction();
        return l;
    }

    public Stream getResultStream() {
        Stream stream = query.getResultStream();
        this.entityManagerDelegate.clearIfNoTransaction();
        return stream;
    }

    public Object getSingleResult() {
        Object o = query.getSingleResult();
        this.entityManagerDelegate.clearIfNoTransaction();
        return o;
    }

    public int executeUpdate() {
        int res = query.executeUpdate();
        this.entityManagerDelegate.clearIfNoTransaction();
        return res;
    }

    public Query setMaxResults(final int maxResult) {
        return query.setMaxResults(maxResult);
    }

    public int getMaxResults() {
        return query.getMaxResults();
    }

    public Query setFirstResult(final int startPosition) {
        return query.setFirstResult(startPosition);
    }

    public int getFirstResult() {
        return query.getFirstResult();
    }

    public Query setHint(final String hintName, final Object value) {
        return query.setHint(hintName, value);
    }

    public Map<String, Object> getHints() {
        return query.getHints();
    }

    public <T> Query setParameter(final Parameter<T> param, final T value) {
        return query.setParameter(param, value);
    }

    public Query setParameter(final Parameter<Calendar> param, final Calendar value, final TemporalType temporalType) {
        return query.setParameter(param, value, temporalType);
    }

    public Query setParameter(final Parameter<Date> param, final Date value, final TemporalType temporalType) {
        return query.setParameter(param, value, temporalType);
    }

    public Query setParameter(final String name, final Object value) {
        return query.setParameter(name, value);
    }

    public Query setParameter(final String name, final Calendar value, final TemporalType temporalType) {
        return query.setParameter(name, value, temporalType);
    }

    public Query setParameter(final String name, final Date value, final TemporalType temporalType) {
        return query.setParameter(name, value, temporalType);
    }

    public Query setParameter(final int position, final Object value) {
        return query.setParameter(position, value);
    }

    public Query setParameter(final int position, final Calendar value, final TemporalType temporalType) {
        return query.setParameter(position, value, temporalType);
    }

    public Query setParameter(final int position, final Date value, final TemporalType temporalType) {
        return query.setParameter(position, value, temporalType);
    }

    public Set<Parameter<?>> getParameters() {
        return query.getParameters();
    }

    public Parameter<?> getParameter(final String name) {
        return query.getParameter(name);
    }

    public <T> Parameter<T> getParameter(final String name, final Class<T> type) {
        return query.getParameter(name, type);
    }

    public Parameter<?> getParameter(final int position) {
        return query.getParameter(position);
    }

    public <T> Parameter<T> getParameter(final int position, final Class<T> type) {
        return query.getParameter(position, type);
    }

    public boolean isBound(final Parameter<?> param) {
        return query.isBound(param);
    }

    public <T> T getParameterValue(final Parameter<T> param) {
        return query.getParameterValue(param);
    }

    public Object getParameterValue(final String name) {
        return query.getParameterValue(name);
    }

    public Object getParameterValue(final int position) {
        return query.getParameterValue(position);
    }

    public Query setFlushMode(final FlushModeType flushMode) {
        return query.setFlushMode(flushMode);
    }

    public FlushModeType getFlushMode() {
        return query.getFlushMode();
    }

    public Query setLockMode(final LockModeType lockMode) {
        return query.setLockMode(lockMode);
    }

    public LockModeType getLockMode() {
        return query.getLockMode();
    }

    public <T> T unwrap(final Class<T> cls) {
        return query.unwrap(cls);
    }

    public EntityManagerDelegate getEntityManagerDelegate() {
        return entityManagerDelegate;
    }
}
