package com.oneandone.iocunit.analyzer.packagedeep.teststructure;

import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.BaseClass;
import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;

/**
 * @author aschoerk
 */
@AnalyzerFlags(allowGuessing = true)
public class NoPackagesTest extends BaseClass {
    @Inject
    Bean1Intf bean1;
    @Inject
    Bean2Intf bean2;

}
