package com.oneandone.iocunit.analyzer.packagedeep.teststructure;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.a.PackageDefiningBean;

/**
 * @author aschoerk
 */
@SutPackages(PackageDefiningBean.class)
@AnalyzerFlags(allowGuessing = false)
public class FlatUsingSutPackagesTest extends BaseClass {
    @Inject
    Bean2Intf bean2;
}
