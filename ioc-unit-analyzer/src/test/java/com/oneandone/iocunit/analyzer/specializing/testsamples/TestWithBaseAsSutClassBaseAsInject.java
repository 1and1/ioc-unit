package com.oneandone.iocunit.analyzer.specializing.testsamples;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.BaseClass;

/**
 * @author aschoerk
 */
@SutClasses(BaseClass.class)
public class TestWithBaseAsSutClassBaseAsInject {
    @Inject
    BaseClass baseClass;
}
