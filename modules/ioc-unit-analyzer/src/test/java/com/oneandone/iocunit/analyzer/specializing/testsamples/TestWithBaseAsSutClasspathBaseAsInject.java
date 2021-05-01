package com.oneandone.iocunit.analyzer.specializing.testsamples;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SutSamplesBaseClass;

/**
 * @author aschoerk
 */
@SutClasspaths(SutSamplesBaseClass.class)
public class TestWithBaseAsSutClasspathBaseAsInject extends BaseClass {
    @Inject
    SutSamplesBaseClass sutSamplesBaseClass;
}
