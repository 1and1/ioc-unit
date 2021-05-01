package com.oneandone.iocunit.analyzer.instance.test;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.instance.sut.Container;
import com.oneandone.iocunit.analyzer.instance.sut.Impl1;
import com.oneandone.iocunit.analyzer.instance.sut.Intf;

/**
 * @author aschoerk
 */
@SutClasses({Container.class, Intf.class})
@SutPackages(Impl1.class)
public class TestClassSample extends BaseClass {
}
