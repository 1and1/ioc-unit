package com.oneandone.iocunit.analyzer.specializing.testsamples;

import jakarta.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SpecializingClass;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SutSamplesBaseClass;

/**
 * @author aschoerk
 */
@SutClasses(SpecializingClass.class)
public class TestWithSpecializingAsSutClassBaseAsInject extends BaseClass {
    @Inject
    SutSamplesBaseClass specializingClass;
}
