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
package com.oneandone.iocunit.contexts.internal;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.contexts.ContextController;
import com.oneandone.iocunit.contexts.InRequestScope;

@Interceptor
@InRequestScope
public class InRequestInterceptorEjbCdiUnit {
	private static final Logger log = LoggerFactory
			.getLogger(InRequestInterceptorEjbCdiUnit.class);

    public InRequestInterceptorEjbCdiUnit() {
        System.out.println();
    }

	@Inject
    private ContextController contextController;

	@AroundInvoke
	public Object around(InvocationContext ctx) throws Exception {
		try {
			try {
				contextController.openRequest();

			} catch (Exception e) {
				log.error("Failed to open request context", e);
				throw e;

			}
			return ctx.proceed();
		} finally {
			contextController.closeRequest();
		}
	}
}
