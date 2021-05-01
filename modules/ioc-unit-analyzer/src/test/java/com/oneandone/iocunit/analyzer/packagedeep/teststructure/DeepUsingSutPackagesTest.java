package com.oneandone.iocunit.analyzer.packagedeep.teststructure;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;
import com.oneandone.iocunit.analyzer.annotations.SutPackagesDeep;
import com.oneandone.iocunit.analyzer.packagedeep.teststructure.a.PackageDefiningBean;

/**
 * @author aschoerk
 */
@SutPackagesDeep(PackageDefiningBean.class)
@AnalyzerFlags(allowGuessing = false)
public class DeepUsingSutPackagesTest extends BaseClass {
    @Inject
    Bean2Intf bean2;
}
