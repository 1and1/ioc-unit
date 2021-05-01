package com.oneandone.iocunit.analyzer.specializing.testsamples;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SpecializingClass;

/**
 * @author aschoerk
 */
@SutClasses(SpecializingClass.class)
public class TestWithSpecializingAsSutClassSpecAsInject extends BaseClass {
    @Inject
    SpecializingClass specializingClass;
}
