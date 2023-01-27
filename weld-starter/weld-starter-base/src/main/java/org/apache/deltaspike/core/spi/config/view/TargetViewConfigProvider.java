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
package org.apache.deltaspike.core.spi.config.view;

import org.apache.deltaspike.core.api.config.view.ViewConfig;

import java.lang.annotation.Annotation;

/**
 * It's restricted to reference {@link ViewConfig} classes to force more solid references.
 * (This restriction is intended.)
 * To reference folder-nodes, it's needed that the corresponding config-class implements {@link ViewConfig} as well.
 *
 * It's used instead of a marker annotation to be more flexible (e.g. for special cases like conditional references).
 *
 * @param <T> type of the annotation which provides the information about the target view-config/s
 */
public interface TargetViewConfigProvider<T extends Annotation>
{
    Class<? extends ViewConfig>[] getTarget(T inlineMetaData);
}
