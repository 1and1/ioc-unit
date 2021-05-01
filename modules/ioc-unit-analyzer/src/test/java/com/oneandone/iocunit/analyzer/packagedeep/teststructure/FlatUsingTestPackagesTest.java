package com.oneandone.iocunit.analyzer.packagedeep.teststructure;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.TestPackages;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.a.PackageDefiningBean;

/**
 * @author aschoerk
 */
@TestPackages(PackageDefiningBean.class)
@AnalyzerFlags(allowGuessing = false)
public class FlatUsingTestPackagesTest extends BaseClass {
    @Inject
    Bean1Intf bean1;
}
