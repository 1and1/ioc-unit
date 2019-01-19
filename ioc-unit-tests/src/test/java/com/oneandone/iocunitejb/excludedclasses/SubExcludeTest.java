package com.oneandone.iocunitejb.excludedclasses;


import org.junit.Ignore;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;

/**
 * @author aschoerk
 */
@Ignore
@ExcludedClasses({ ExcludeTest.class })
public class SubExcludeTest extends AbstractExcludeTest {

}
