package com.oneandone.iocunitejb.excludedclasses;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.iocunitejb.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@ExcludedClasses({ ToExclude.class })
public class IndirectExcluding {}
