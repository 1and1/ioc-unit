package com.oneandone.iocunit.jtajpa.internal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TemporalType;

/**
 * @author aschoerk
 */
public class StoredProcedureQueryDelegate extends QueryDelegate implements StoredProcedureQuery {
    private final StoredProcedureQuery storedProcedureQuery;

    public StoredProcedureQueryDelegate(final StoredProcedureQuery query, EntityManagerDelegate entityManagerDelegate) {
        super(query, entityManagerDelegate);
        this.storedProcedureQuery = query;
    }

    @Override
    public StoredProcedureQuery setHint(final String hintName, final Object value) {
        return storedProcedureQuery.setHint(hintName, value);
    }

    @Override
    public <T> StoredProcedureQuery setParameter(final Parameter<T> param, final T value) {
        return storedProcedureQuery.setParameter(param, value);
    }

    @Override
    public StoredProcedureQuery setParameter(final Parameter<Calendar> param, final Calendar value, final TemporalType temporalType) {
        return storedProcedureQuery.setParameter(param, value, temporalType);
    }

    @Override
    public StoredProcedureQuery setParameter(final Parameter<Date> param, final Date value, final TemporalType temporalType) {
        return storedProcedureQuery.setParameter(param, value, temporalType);
    }

    @Override
    public StoredProcedureQuery setParameter(final String name, final Object value) {
        return storedProcedureQuery.setParameter(name, value);
    }

    @Override
    public StoredProcedureQuery setParameter(final String name, final Calendar value, final TemporalType temporalType) {
        return storedProcedureQuery.setParameter(name, value, temporalType);
    }

    @Override
    public StoredProcedureQuery setParameter(final String name, final Date value, final TemporalType temporalType) {
        return storedProcedureQuery.setParameter(name, value, temporalType);
    }

    @Override
    public StoredProcedureQuery setParameter(final int position, final Object value) {
        return storedProcedureQuery.setParameter(position, value);
    }

    @Override
    public StoredProcedureQuery setParameter(final int position, final Calendar value, final TemporalType temporalType) {
        return storedProcedureQuery.setParameter(position, value, temporalType);
    }

    @Override
    public StoredProcedureQuery setParameter(final int position, final Date value, final TemporalType temporalType) {
        return storedProcedureQuery.setParameter(position, value, temporalType);
    }

    @Override
    public StoredProcedureQuery setFlushMode(final FlushModeType flushMode) {
        return storedProcedureQuery.setFlushMode(flushMode);
    }

    @Override
    public StoredProcedureQuery registerStoredProcedureParameter(final int position, final Class type, final ParameterMode mode) {
        return storedProcedureQuery.registerStoredProcedureParameter(position, type, mode);
    }

    @Override
    public StoredProcedureQuery registerStoredProcedureParameter(final String parameterName, final Class type, final ParameterMode mode) {
        return storedProcedureQuery.registerStoredProcedureParameter(parameterName, type, mode);
    }

    @Override
    public Object getOutputParameterValue(final int position) {
        return storedProcedureQuery.getOutputParameterValue(position);
    }

    @Override
    public Object getOutputParameterValue(final String parameterName) {
        return storedProcedureQuery.getOutputParameterValue(parameterName);
    }

    @Override
    public boolean execute() {
        boolean res = storedProcedureQuery.execute();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public int executeUpdate() {
        int res = storedProcedureQuery.executeUpdate();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public List getResultList() {
        List res = storedProcedureQuery.getResultList();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public Object getSingleResult() {
        Object res = storedProcedureQuery.getSingleResult();
        this.getEntityManagerDelegate().clearIfNoTransaction();
        return res;
    }

    @Override
    public boolean hasMoreResults() {
        return storedProcedureQuery.hasMoreResults();
    }

    @Override
    public int getUpdateCount() {
        return storedProcedureQuery.getUpdateCount();
    }
}
