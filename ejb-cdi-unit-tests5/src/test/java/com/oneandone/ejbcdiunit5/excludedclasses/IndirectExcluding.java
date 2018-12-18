package com.oneandone.ejbcdiunit5.excludedclasses;

import com.oneandone.cdi.testanalyzer.annotations.ExcludedClasses;
import com.oneandone.ejbcdiunit5.excludedclasses.pcktoinclude.ToExclude;

/**
 * @author aschoerk excluding by other class, not testIntercepted, does not work.
 */
@ExcludedClasses({ ToExclude.class })
public class IndirectExcluding {}
