package com.oneandone.iocunit.analyzer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aschoerk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnalyzerFlags {
    // Guess new Candidates from current classpath
    public boolean allowGuessing() default true;
    // find Beans to produce for Instance<> from Availables.
    // if false, feeds only from toBeStarted beens
    public boolean produceInstanceInjectsByAvailables() default false;

    // TODO:
    public boolean addAllStartableBeans() default false;

    // TODO:
    public boolean addAllFoundInterceptors() default false;

    // TODO:
    public boolean addAllFoundDelegates() default false;

    // TODO:
    public boolean allowParameterizedInjectedToRawtype() default true;


}
