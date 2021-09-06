/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.cdi.discoveryrunner.internal;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.apache.deltaspike.core.api.literal.ApplicationScopedLiteral;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.Rule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;


/**
 * Extension class that ensures selected classes are excluded as
 * beans.
 */
public class WeldDiscoveryCdiExtension implements Extension {
    private static final ApplicationScopedLiteral APPLICATIONSCOPED = new ApplicationScopedLiteral();

    private Set<String> excludedBeanClassNames;
    private List<Pattern> excludedNameExpressions;
    private List<String> excludedNameParts;

    public WeldDiscoveryCdiExtension(WeldInfo weldInfo) {
        this.excludedBeanClassNames = weldInfo.getToExcludeClassNames();
        this.excludedNameParts = weldInfo.getToExcludeClassNameParts();
        excludedNameExpressions = weldInfo
                .getToExcludeExpressions()
                .stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    <T> void excludeBeans(@Observes ProcessAnnotatedType<T> pat) {
        final String name = pat.getAnnotatedType().getJavaClass().getName();
        if(excludedBeanClassNames.contains(name)) {
            pat.veto();
        } else {
            excludedNameParts.forEach(p -> {
                if (name.contains(p)) {
                    pat.veto();
                    return;
                }
            });
            excludedNameExpressions.forEach(p -> {
                if (p.matcher(name).matches()) {
                    pat.veto();
                    return;
                }
            });
        }
    }

    <T> void processAnnotatedType(@Observes @WithAnnotations({
            ExtendWith.class, RunWith.class, Rule.class}) ProcessAnnotatedType<T> pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(annotatedType).addToClass(APPLICATIONSCOPED);
        pat.setAnnotatedType(builder.create());
    }

}
