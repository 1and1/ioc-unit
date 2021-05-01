package com.oneandone.iocunit.analyzer.specializing.testsamples;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.specializing.sutsamples.SpecializingClass;

/**
 * @author aschoerk
 */
@SutClasspaths(SpecializingClass.class)
public class TestWithSpecAsSutClasspathSpecAsInject extends BaseClass {
    @Inject
    SpecializingClass specClass;
}
