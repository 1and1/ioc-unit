package com.oneandone.iocunit.analyzer.rawtype;

import java.util.List;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

/**
 * @author aschoerk
 */
@SutPackages({RawListContainerRawListSubExcluded.class})
@ExcludedClasses({RawListSubProducer.class, RawListSub.class})
public class RawListContainerRawListSubExcluded {
    @Inject
    List list;
}
