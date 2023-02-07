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
package org.apache.deltaspike.core.api.config.view.metadata;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( METHOD )
@Retention(RUNTIME)
@Documented

/**
 * A ConfigDescriptor can contain CallbackDescriptors in general as well as ExecutableCallbackDescriptors.
 * An ExecutableCallbackDescriptor can reference one or multiple callback-method/s. If there is only one callback type,
 * it's possible to annotate it with @DefaultCallback.
 * That allows to handle it in an easier fashion
 * (= without providing a special marker (-annotation) for the target method).
 *
 * If there are multiple callback types, it's needed to use custom annotations as marker for the target method.
 * (e.g. see @Secured vs. @ViewControllerBean)
 *
 * ViewConfigDescriptor viewConfigDescriptor = viewConfigResolver.getViewConfigDescriptor(PageConfig.class);
 * viewConfigDescriptor.getExecutableCallbackDescriptor(
 *   Secured.class, Secured.Descriptor.class).execute(accessDecisionVoterContext);
 * is short for
 * viewConfigDescriptor.getExecutableCallbackDescriptor(
 *   Secured.class, DefaultCallback.class, Secured.Descriptor.class).execute(accessDecisionVoterContext);
 *
 * whereas e.g.
 * viewConfigDescriptor.getExecutableCallbackDescriptor(
 *   ViewControllerBean.class, PreRenderView.class, ViewControllerBean.ViewControllerDescriptor.class).execute();
 * or just
 * viewConfigDescriptor.getExecutableCallbackDescriptor(
 * ViewControllerBean.class, PreRenderView.class, SimpleCallbackDescriptor.class).execute();
 *
 * are needed to call only @PreRenderView callbacks
 * (and not the others like @InitView which are also bound to @ViewControllerBean)
 */
//TODO find a better name
public @interface DefaultCallback
{
}
