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
package org.apache.deltaspike.core.api.config.view;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Named;

import org.apache.deltaspike.core.api.config.view.controller.ViewControllerRef;
import org.apache.deltaspike.core.api.config.view.metadata.InlineViewMetaData;
import org.apache.deltaspike.core.api.literal.ViewControllerRefLiteral;
import org.apache.deltaspike.core.spi.config.view.InlineMetaDataTransformer;
import org.apache.deltaspike.core.spi.config.view.TargetViewConfigProvider;

/**
 * Allows to reference a view-config
 */

@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented

@InlineViewMetaData(
        targetViewConfigProvider = ViewRef.ViewRefTargetViewConfigProvider.class,
        inlineMetaDataTransformer = ViewRef.ViewRefInlineMetaDataTransformer.class)
public @interface ViewRef
{
    abstract class Manual implements ViewConfig
    {
    }

    /**
     * Specifies the pages via type-safe {@link ViewConfig}.
     *
     * @return views which should be aware of the bean or observer
     */
    @Nonbinding Class<? extends ViewConfig>[] config();

    class ViewRefTargetViewConfigProvider implements TargetViewConfigProvider<ViewRef>
    {
        @Override
        public Class<? extends ViewConfig>[] getTarget(ViewRef inlineMetaData)
        {
            return inlineMetaData.config();
        }
    }

    class ViewRefInlineMetaDataTransformer implements InlineMetaDataTransformer<ViewRef, ViewControllerRef>
    {
        @Override
        public ViewControllerRef convertToViewMetaData(ViewRef inlineMetaData, Class<?> sourceClass)
        {
            String beanName = null;

            Named named = sourceClass.getAnnotation(Named.class);

            if (named != null)
            {
                beanName = named.value();
            }

            return new ViewControllerRefLiteral(sourceClass, beanName);
        }
    }
}
