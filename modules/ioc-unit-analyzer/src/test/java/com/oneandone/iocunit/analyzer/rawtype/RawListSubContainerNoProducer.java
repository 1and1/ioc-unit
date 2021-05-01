package com.oneandone.iocunit.analyzer.rawtype;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.rawtype.producers.RawListSubProducer;
import com.oneandone.iocunit.analyzer.rawtype.types.RawListSub;

/**
 * @author aschoerk
 */
@SutPackages(RawParamTest.class)
@ExcludedClasses(RawListSubProducer.class)
public class RawListSubContainerNoProducer extends BaseClass {
    @Inject
    RawListSub list;
}
