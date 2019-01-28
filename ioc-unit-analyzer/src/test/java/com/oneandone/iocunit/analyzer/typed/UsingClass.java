package com.oneandone.iocunit.analyzer.typed;

import javax.inject.Inject;

/**
 * @author aschoerk
 */
public class UsingClass {
    @Inject
    BaseClass baseClass;
    @Inject
    TypedSubClass typedSubClass;
}
