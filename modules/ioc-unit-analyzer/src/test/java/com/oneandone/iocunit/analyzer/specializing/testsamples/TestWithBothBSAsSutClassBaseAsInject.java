package com.oneandone.iocunit.analyzer.specializing.testsamples;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SpecializingClass;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SutSamplesBaseClass;

/**
 * @author aschoerk
 */
@SutClasses({SpecializingClass.class, SutSamplesBaseClass.class})
public class TestWithBothBSAsSutClassBaseAsInject extends BaseClass {
    @Inject
    SutSamplesBaseClass sutSamplesBaseClass;
}
