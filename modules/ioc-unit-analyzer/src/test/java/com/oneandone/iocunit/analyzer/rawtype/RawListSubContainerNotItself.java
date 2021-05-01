package com.oneandone.iocunit.analyzer.rawtype;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.rawtype.types.RawListSub;

/**
 * @author aschoerk
 */
@SutPackages(RawParamTest.class)
@ExcludedClasses(RawListSub.class)
public class RawListSubContainerNotItself extends BaseClass {
    @Inject
    RawListSub list;
}
