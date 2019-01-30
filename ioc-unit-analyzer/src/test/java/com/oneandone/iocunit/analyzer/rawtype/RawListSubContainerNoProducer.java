package com.oneandone.iocunit.analyzer.rawtype;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

/**
 * @author aschoerk
 */
@SutPackages(RawParamTest.class)
@ExcludedClasses(RawListSubProducer.class)
public class RawListSubContainerNoProducer {
    @Inject
    RawListSub list;
}
