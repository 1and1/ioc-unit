package com.oneandone.iocunit.analyzer.rawtype;

import java.util.List;

import jakarta.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.rawtype.types.StringList;

/**
 * @author aschoerk
 */
public class RawListContainerRawProducerExcluded extends BaseClass {
    @Inject
    List list;

    @Inject
    StringList stringList;
}
