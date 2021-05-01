package com.oneandone.iocunit.analyzer.typed;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;

/**
 * @author aschoerk
 */
public class UsingClassTyped extends BaseClass {
    @Inject
    TypedBaseClass typedBaseClass;
    @Inject
    TypedSubClassTyped typedSubClass;
}
