package com.oneandone.iocunitejb.validation;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.validation.constraints.NotNull;

/**
 * @author aschoerk
 */
@Stateless
public class ValidatedEjb implements Serializable {
    private static final long serialVersionUID = -4827926958090675644L;

    public Integer callWithoutNull(@NotNull Integer i) {
        return i;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Integer callWithoutNullSinTransaction(@NotNull Integer i) {
        return i;
    }


}
