package com.oneandone.iocunit.validate;

import javax.validation.constraints.NotNull;

/**
 * @author aschoerk
 */
public class Sut1 {
    public Integer method1(@NotNull Integer notnull) {
        return notnull;
    }
}
