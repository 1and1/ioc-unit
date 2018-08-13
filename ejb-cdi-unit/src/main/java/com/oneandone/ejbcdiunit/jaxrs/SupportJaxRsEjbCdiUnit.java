/*
 *    Copyright 2014 Bryn Cooke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.ejbcdiunit.jaxrs;

import com.oneandone.ejbcdiunit.internal.jaxrs.JaxRsProducersEjbCdiUnit;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.internal.jaxrs.JaxRsExtension;
import org.jglue.cdiunit.internal.jaxrs.JaxRsProducers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Enable support for JaxRs mocking.
 * 
 * @author bryn
 *
 */
@AdditionalClasses({JaxRsExtension.class, JaxRsProducersEjbCdiUnit.class })
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportJaxRsEjbCdiUnit {

}
