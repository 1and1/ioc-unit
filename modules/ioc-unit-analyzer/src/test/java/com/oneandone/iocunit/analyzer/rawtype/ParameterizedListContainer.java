package com.oneandone.iocunit.analyzer.rawtype;

import java.util.List;

import jakarta.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;

/**
 * @author aschoerk
 */
public class ParameterizedListContainer extends BaseClass {
    @Inject
    List<String> list;
}
