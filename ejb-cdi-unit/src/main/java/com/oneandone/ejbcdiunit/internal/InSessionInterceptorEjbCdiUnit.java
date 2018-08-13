/*
 *    Copyright 2011 Bryn Cooke
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
package com.oneandone.ejbcdiunit.internal;

import com.oneandone.ejbcdiunit.ContextControllerEjbCdiUnit;
import org.jglue.cdiunit.InSessionScope;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@InSessionScope
public class InSessionInterceptorEjbCdiUnit {

	
	@Inject
	private ContextControllerEjbCdiUnit contextControllerEjbCdiUnit;

	@AroundInvoke
	public Object around(InvocationContext ctx) throws Exception {

		try {
			return ctx.proceed();
		} finally {
			contextControllerEjbCdiUnit.closeSession();
		}

	}
}
