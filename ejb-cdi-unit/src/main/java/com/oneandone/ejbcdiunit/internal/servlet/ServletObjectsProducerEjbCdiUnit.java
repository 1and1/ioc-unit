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
package com.oneandone.ejbcdiunit.internal.servlet;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oneandone.cdiunit.internal.servlet.CdiUnitServlet;
import com.oneandone.cdiunit.internal.servlet.MockServletContextImpl;
import com.oneandone.ejbcdiunit.ContextControllerEjbCdiUnit;


public class ServletObjectsProducerEjbCdiUnit {

	@Inject
	@CdiUnitServlet
    MockServletContextImpl servletContext;

	@Produces
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	@Inject
	ContextControllerEjbCdiUnit contextController;

	@Produces
	@RequestScoped
	public HttpServletRequest getHttpServletRequest() {
		return contextController.currentRequest();
	}
	
	@Produces
	@SessionScoped
	public HttpSession getHttpSession() {
		return contextController.currentRequest().getSession();
	}
	
}
