/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.deltaspike.core.util.bean;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.InjectionPoint;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 *
 */
public class ImmutableBean<T> extends BaseImmutableBean<T> {
    private final ContextualLifecycle<T> lifecycle;

    /**
     * Create a new, immutable bean. All arguments passed as collections are
     * defensively copied.
     *
     * @param beanClass           The Bean class, may not be null
     * @param name                The bean name
     * @param qualifiers          The bean's qualifiers, if null, a singleton set of
     *                            {@link jakarta.enterprise.inject.Default} is used
     * @param scope               The bean's scope, if null, the default scope of
     *                            {@link jakarta.enterprise.context.Dependent} is used
     * @param stereotypes         The bean's stereotypes, if null, an empty set is used
     * @param types               The bean's types, if null, the beanClass and {@link Object}
     *                            will be used
     * @param alternative         True if the bean is an alternative
     * @param injectionPoints     the bean's injection points, if null an empty set is used
     * @param toString            the string which should be returned by #{@link #toString()}
     * @param contextualLifecycle Handler for {@link #create(CreationalContext)} and
     *                            {@link #destroy(Object, CreationalContext)}
     * @throws IllegalArgumentException if the beanClass is null
     */
    // CHECKSTYLE:OFF
    public ImmutableBean(Class<?> beanClass, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope,
                         Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative,
                         Set<InjectionPoint> injectionPoints, String toString,
                         ContextualLifecycle<T> contextualLifecycle) {
        // CHECKSTYLE:ON
        super(beanClass, name, qualifiers, scope, stereotypes, types, alternative, injectionPoints, toString);
        this.lifecycle = contextualLifecycle;
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        return lifecycle.create(this, creationalContext);
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        this.lifecycle.destroy(this, instance, creationalContext);
    }
}
