package com.oneandone.iocunit.validate;

import jakarta.validation.constraints.NotNull;

/**
 * @author aschoerk
 */
public class Sut1 {
    public Integer method1(@NotNull Integer notnull) {
        return notnull;
    }
}
