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

import org.apache.deltaspike.core.api.literal.DefaultLiteral;
import org.apache.deltaspike.core.util.ArraysUtils;
import org.apache.deltaspike.core.util.metadata.InjectionPointWrapper;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * A base class for implementing {@link Bean}. The attributes are immutable, and
 * collections are defensively copied on instantiation. It uses the defaults
 * from the specification for properties if not specified.
 * </p>
 * <p/>
 * <p>
 * This class does not provide any bean lifecycle operations
 * </p>
 *
 * @see ImmutableBeanWrapper
 */
public abstract class BaseImmutableBean<T> implements Bean<T>
{
    private static final Logger LOG = Logger.getLogger(BaseImmutableBean.class.getName());

    private final Class<?> beanClass;
    private final String name;
    private final Set<Annotation> qualifiers;
    private final Class<? extends Annotation> scope;
    private final Set<Class<? extends Annotation>> stereotypes;
    private final Set<Type> types;
    private final boolean alternative;
    private final Set<InjectionPoint> injectionPoints;
    private final String toString;

    /**
     * Create a new, immutable bean. All arguments passed as collections are
     * defensively copied.
     *
     * @param beanClass       The Bean class, may not be null
     * @param name            The bean name
     * @param qualifiers      The bean's qualifiers, if null, a singleton set of
     *                        {@link jakarta.enterprise.inject.Default} is used
     * @param scope           The bean's scope, if null, the default scope of
     *                        {@link Dependent} is used
     * @param stereotypes     The bean's stereotypes, if null, an empty set is used
     * @param types           The bean's types, if null, the beanClass and {@link Object}
     *                        will be used
     * @param alternative     True if the bean is an alternative
     * @param injectionPoints the bean's injection points, if null an empty set is used
     * @param toString        the string which should be returned by #{@link #toString()}
     * @throws IllegalArgumentException if the beanClass is null
     */
    public BaseImmutableBean(Class<?> beanClass,
                             String name,
                             Set<Annotation> qualifiers,
                             Class<? extends Annotation> scope,
                             Set<Class<? extends Annotation>> stereotypes,
                             Set<Type> types,
                             boolean alternative,
                             Set<InjectionPoint> injectionPoints,
                             String toString)
    {
        if (beanClass == null)
        {
            throw new IllegalArgumentException("beanClass cannot be null");
        }

        this.beanClass = beanClass;
        this.name = name;

        if (qualifiers == null)
        {
            this.qualifiers = Collections.<Annotation>singleton(new DefaultLiteral());

            LOG.finest("No qualifers provided for bean class " + beanClass + ", using singleton set of @Default");
        }
        else
        {
            this.qualifiers = new HashSet<Annotation>(qualifiers);
        }

        if (scope == null)
        {
            this.scope = Dependent.class;

            LOG.finest("No scope provided for bean class " + beanClass + ", using @Dependent");
        }
        else
        {
            this.scope = scope;
        }

        if (stereotypes == null)
        {
            this.stereotypes = Collections.emptySet();
        }
        else
        {
            this.stereotypes = new HashSet<Class<? extends Annotation>>(stereotypes);
        }

        if (types == null)
        {
            //noinspection unchecked
            this.types = ArraysUtils.<Type>asSet(Object.class, beanClass);

            LOG.finest("No types provided for bean class " + beanClass
                    + ", using [java.lang.Object.class, " + beanClass.getName()
                    + ".class]");
        }
        else
        {
            this.types = new HashSet<Type>(types);
        }

        if (injectionPoints == null)
        {
            this.injectionPoints = Collections.emptySet();
        }
        else
        {
            // Check for null Beans, wrap if there isn't one -- DELTASPIKE-400
            final HashSet<InjectionPoint> ips = new HashSet<InjectionPoint>(injectionPoints.size());

            for (InjectionPoint ip : injectionPoints)
            {
                if (ip.getBean() == null)
                {
                    ips.add(new InjectionPointWrapper(ip, this));
                }
                else
                {
                    ips.add(ip);
                }
            }

            this.injectionPoints = ips;
        }

        this.alternative = alternative;

        if (toString != null)
        {
            this.toString = toString;
        }
        else
        {
            this.toString = "Custom Bean with bean class " + beanClass + " and qualifiers " + qualifiers;
        }
    }

    @Override
    public Class<?> getBeanClass()
    {
        return beanClass;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints()
    {
        return injectionPoints;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Set<Annotation> getQualifiers()
    {
        return Collections.unmodifiableSet(qualifiers);
    }

    @Override
    public Class<? extends Annotation> getScope()
    {
        return scope;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes()
    {
        return Collections.unmodifiableSet(stereotypes);
    }

    @Override
    public Set<Type> getTypes()
    {
        return Collections.unmodifiableSet(types);
    }

    @Override
    public boolean isAlternative()
    {
        return alternative;
    }

    @Override
    public String toString()
    {
        return toString;
    }
}
