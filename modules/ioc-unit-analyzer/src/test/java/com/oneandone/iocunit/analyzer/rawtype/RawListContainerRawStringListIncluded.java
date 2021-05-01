package com.oneandone.iocunit.analyzer.rawtype;

import java.util.List;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.rawtype.types.StringList;

/**
 * @author aschoerk
 */
@SutPackages({StringList.class})
public class RawListContainerRawStringListIncluded extends BaseClass {
    @Inject
    List list;

    @Inject
    StringList stringList;
}
