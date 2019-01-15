package com.oneandone.ejbcdiunit.excludedclasses;

import com.oneandone.iocunit.analyzer.annotations.ExcludedClasses;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@ExcludedClasses({ ToExclude.class })
public class IndirectExcluding {}
