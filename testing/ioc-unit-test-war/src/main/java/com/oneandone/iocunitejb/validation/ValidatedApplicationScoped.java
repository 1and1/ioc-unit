package com.oneandone.iocunitejb.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class ValidatedApplicationScoped {

    @Transactional(Transactional.TxType.REQUIRED)
    public Integer callWithoutNull(@NotNull Integer i) {
        return i;
    }

    public Integer callWithoutNullSinTransaction(@NotNull Integer i) {
        return i;
    }


}
