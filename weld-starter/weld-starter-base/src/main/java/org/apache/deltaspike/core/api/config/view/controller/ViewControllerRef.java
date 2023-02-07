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
package org.apache.deltaspike.core.api.config.view.controller;

/**
 * Specifies one or more page-beans via the type-safe view-config.
 * Such page beans support e.g. the view-controller annotations.
 */

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.deltaspike.core.api.config.view.metadata.SimpleCallbackDescriptor;
import org.apache.deltaspike.core.api.config.view.metadata.ViewMetaData;
import org.apache.deltaspike.core.spi.config.view.ConfigPreProcessor;
import org.apache.deltaspike.core.spi.config.view.ViewConfigNode;

//don't use @Inherited
@Target(TYPE)
@Retention(RUNTIME)
@Documented

@ViewMetaData(preProcessor = ViewControllerRef.AnnotationPreProcessor.class)
public @interface ViewControllerRef
{
    /**
     * Class of the page-bean
     *
     * @return class of the page-bean
     */
    Class value();

    /**
     * Currently not implemented
     *
     *
     * Optional name of the page-bean
     *
     * @return name of the page-bean
     */
    //TODO
    String name() default "";

    class AnnotationPreProcessor implements ConfigPreProcessor<ViewControllerRef>
    {
        @Override
        public ViewControllerRef beforeAddToConfig(ViewControllerRef metaData, ViewConfigNode viewConfigNode)
        {
            viewConfigNode.registerCallbackDescriptors(
                    ViewControllerRef.class, new Descriptor(metaData.value(), InitView.class));
            viewConfigNode.registerCallbackDescriptors(
                    ViewControllerRef.class, new Descriptor(metaData.value(), PreViewAction.class));
            viewConfigNode.registerCallbackDescriptors(
                    ViewControllerRef.class, new Descriptor(metaData.value(), PreRenderView.class));
            viewConfigNode.registerCallbackDescriptors(
                    ViewControllerRef.class, new Descriptor(metaData.value(), PostRenderView.class));
            return metaData; //no change needed
        }
    }

    //not needed outside
    static class Descriptor extends SimpleCallbackDescriptor<Void>
    {
        protected Descriptor(Class beanClass, Class<? extends Annotation> callbackType)
        {
            super(beanClass, callbackType);
        }
    }
}
