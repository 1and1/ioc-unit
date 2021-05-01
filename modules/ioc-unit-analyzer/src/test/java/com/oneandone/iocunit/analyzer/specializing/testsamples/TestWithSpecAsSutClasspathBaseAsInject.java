package com.oneandone.iocunit.analyzer.specializing.testsamples;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SpecializingClass;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SutSamplesBaseClass;

/**
 * @author aschoerk
 */
@SutClasspaths(SpecializingClass.class)
public class TestWithSpecAsSutClasspathBaseAsInject extends BaseClass {
    @Inject
    SutSamplesBaseClass sutSamplesBaseClass;
}
