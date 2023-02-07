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
package org.apache.deltaspike.core.util;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.inject.Typed;

/**
 * Helper for proxies
 */
@Typed()
public abstract class ProxyUtils
{
    private ProxyUtils()
    {
        // prevent instantiation
    }

    /**
     * @param currentClass current class
     * @return class of the real implementation
     */
    public static Class getUnproxiedClass(Class currentClass)
    {
        if (isProxiedClass(currentClass))
        {
            return currentClass.getSuperclass();
        }
        return currentClass;
    }

    /**
     * Analyses if the given class is a generated proxy class
     * @param currentClass current class
     * @return true if the given class is a known proxy class, false otherwise
     */
    public static boolean isProxiedClass(Class currentClass)
    {
        if (currentClass == null || currentClass.getSuperclass() == null)
        {
            return false;
        }

        return currentClass.getName().startsWith(currentClass.getSuperclass().getName()) &&
            currentClass.getName().contains("$$");
    }

    public static List<Class<?>> getProxyAndBaseTypes(Class<?> proxyClass)
    {
        List<Class<?>> result = new ArrayList<Class<?>>();
        result.add(proxyClass);
        if (isInterfaceProxy(proxyClass))
        {
            result.addAll(Arrays.asList(proxyClass.getInterfaces()));
        }
        else
        {
            result.add(proxyClass.getSuperclass());
        }
        return result;
    }

    public static boolean isInterfaceProxy(Class<?> proxyClass)
    {
        Class<?>[] interfaces = proxyClass.getInterfaces();
        return Proxy.class.equals(proxyClass.getSuperclass()) &&
                interfaces != null && interfaces.length > 0;
    }
}
