package com.oneandone.iocunitejb.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

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
