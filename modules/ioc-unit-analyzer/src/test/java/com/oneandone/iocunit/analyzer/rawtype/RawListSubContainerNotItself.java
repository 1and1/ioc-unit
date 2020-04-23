package com.oneandone.iocunit.analyzer.rawtype;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

/**
 * @author aschoerk
 */
@SutPackages(RawParamTest.class)
@ExcludedClasses(RawListSub.class)
public class RawListSubContainerNotItself {
    @Inject
    RawListSub list;
}
