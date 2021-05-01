package com.oneandone.iocunit.analyzer.rawtype;

import java.util.List;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;

/**
 * @author aschoerk
 */
public class ParameterizedListContainer extends BaseClass {
    @Inject
    List<String> list;
}
