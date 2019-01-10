package com.oneandone.ejbcdiunit.excludedclasses;

import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.ejbcdiunit.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk
 */
@ExcludedClasses({ ToExclude.class })
public class IndirectExcluding {}
