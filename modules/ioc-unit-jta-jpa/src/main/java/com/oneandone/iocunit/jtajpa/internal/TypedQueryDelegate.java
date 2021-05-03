package com.oneandone.iocunit.jtajpa.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

/**
 * @author aschoerk
 */
public class TypedQueryDelegate<X> extends QueryDelegate implements TypedQuery<X> {
    private final TypedQuery typedQuery;

    public TypedQueryDelegate(final TypedQuery<X> typedQuery, EntityManagerDelegate entityManagerDelegate) {
        super(typedQuery, entityManagerDelegate);
        this.typedQuery = typedQuery;
    }

    @Override
    public List<X> getResultList() {
        List<X> res = typedQuery.getResultList();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public Stream getResultStream() {
        Stream res = typedQuery.getResultStream();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public X getSingleResult() {
        X res = (X) typedQuery.getSingleResult();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public TypedQuery setMaxResults(final int maxResult) {
        return typedQuery.setMaxResults(maxResult);
    }

    @Override
    public TypedQuery setFirstResult(final int startPosition) {
        return typedQuery.setFirstResult(startPosition);
    }

    @Override
    public TypedQuery setHint(final String hintName, final Object value) {
        return typedQuery.setHint(hintName, value);
    }

    @Override
    public TypedQuery setParameter(final Parameter param, final Object value) {
        return typedQuery.setParameter(param, value);
    }

    @Override
    public TypedQuery setParameter(final Parameter param, final Calendar value, final TemporalType temporalType) {
        return typedQuery.setParameter(param, value, temporalType);
    }

    @Override
    public TypedQuery setParameter(final Parameter param, final Date value, final TemporalType temporalType) {
        return typedQuery.setParameter(param, value, temporalType);
    }

    @Override
    public TypedQuery setParameter(final String name, final Object value) {
        return typedQuery.setParameter(name, value);
    }

    @Override
    public TypedQuery setParameter(final String name, final Calendar value, final TemporalType temporalType) {
        return typedQuery.setParameter(name, value, temporalType);
    }

    @Override
    public TypedQuery setParameter(final String name, final Date value, final TemporalType temporalType) {
        return typedQuery.setParameter(name, value, temporalType);
    }

    @Override
    public TypedQuery setParameter(final int position, final Object value) {
        return typedQuery.setParameter(position, value);
    }

    @Override
    public TypedQuery setParameter(final int position, final Calendar value, final TemporalType temporalType) {
        return typedQuery.setParameter(position, value, temporalType);
    }

    @Override
    public TypedQuery setParameter(final int position, final Date value, final TemporalType temporalType) {
        return typedQuery.setParameter(position, value, temporalType);
    }

    @Override
    public TypedQuery setFlushMode(final FlushModeType flushMode) {
        return typedQuery.setFlushMode(flushMode);
    }

    @Override
    public TypedQuery setLockMode(final LockModeType lockMode) {
        return typedQuery.setLockMode(lockMode);
    }
}
