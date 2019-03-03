package com.oneandone.iocunit.analyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.spi.Extension;

/**
 * @author aschoerk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnalyzerFlags {
    static final Class<? extends Extension>[] noExcludedExtensions = new Class[] {};
    // Guess new Candidates from current classpath
    public boolean allowGuessing() default true;
    // find Beans to produce for Instance<> from Availables.
    // if false, feeds only from toBeStarted beens
    public boolean produceInstanceInjectsByAvailables() default false;

    // if some extensions should be deactivated for a certain test.
    public Class<? extends Extension>[] excludedExtensions() default {};

    // TODO:
    public boolean addAllStartableBeans() default false;

    // TODO:
    public boolean addAvailableInterceptorsAndDecorators() default false;

}
