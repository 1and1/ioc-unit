package com.oneandone.iocunit.analyzer.rawtype;

import java.util.List;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;

/**
 * @author aschoerk
 */
@SutPackages(RawParamTest.class)
@ExcludedClasses({StringListProducer.class, StringList.class})
public class ParameterizedListContainerStringListExcluded {
    @Inject
    List<String> list;
}
