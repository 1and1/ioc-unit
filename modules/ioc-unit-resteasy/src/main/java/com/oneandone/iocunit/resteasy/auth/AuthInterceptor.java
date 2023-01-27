package com.oneandone.iocunit.resteasy.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.RunAs;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import com.oneandone.iocunit.InterceptorBase;
import com.oneandone.iocunit.resteasy.IocUnitSecurityContext;
import com.oneandone.iocunit.util.Annotations;

@Interceptor
@RestEasyAuthorized
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class AuthInterceptor extends InterceptorBase {

    public static class ForbiddenException extends WebApplicationException {

        private static final long serialVersionUID = -3245946809293670040L;

        public ForbiddenException(final String message) {
            super(message, Response.Status.FORBIDDEN);
        }
    }

    @Inject
    Instance<IocUnitSecurityContext> securityContext;  // manipulatable context (runAs)

    ThreadLocal<Stack<List<String>>> runAsStack = new ThreadLocal<>();

    @AroundInvoke
    public Object manageSecurity(InvocationContext ctx) throws Exception {
        boolean didPush = false;
        try {
            final Class<?> declaringClass = ctx.getMethod().getDeclaringClass();
            if(Annotations.getMethodAnnotation(declaringClass, ctx.getMethod(), PermitAll.class) == null) {
                if(securityContext != null && !securityContext.isUnsatisfied() && !securityContext.isAmbiguous()) {
                    boolean foundOnMethodLevel = false;
                    if(Annotations.getMethodAnnotation(declaringClass, ctx.getMethod(), DenyAll.class) != null) {
                        throw new ForbiddenException("DenyAll on " + ctx.getMethod().getName());
                    }
                    List<RunAs> runAs = Annotations.findAnnotations(declaringClass, RunAs.class);
                    if(!runAs.isEmpty()) {
                        if(runAs.size() != 1) {
                            throw new RuntimeException("Invalid multiple RunAs in " + declaringClass.getName());
                        }
                        String runAsRole = runAs.get(0).value();
                        if(!securityContext.get().isUserInRole(runAsRole)) {
                            throw new ForbiddenException("RunAs and user not in role " + runAsRole);
                        }
                        runAsStack.get().push(securityContext.get().getRoles());
                        didPush = true;
                        ArrayList<String> runAsRoleList = new ArrayList<>();
                        runAsRoleList.add(runAsRole);
                        securityContext.get().setRoles(runAsRoleList);
                    }
                    RolesAllowed rolesAllowedOnMethod = Annotations.getMethodAnnotation(declaringClass, ctx.getMethod(), RolesAllowed.class);
                    if(rolesAllowedOnMethod != null) {
                        for (String role : rolesAllowedOnMethod.value()) {
                            if(securityContext.get().isUserInRole(role)) {
                                return ctx.proceed();
                            }
                        }
                        throw new ForbiddenException("User not in roles " + rolesAllowedOnMethod);
                    }
                    else {
                        Class<?> targetClass = getTargetClass(ctx);
                        if(!Annotations.findAnnotations(targetClass, PermitAll.class).isEmpty()) {
                            return ctx.proceed();
                        }
                        if(!Annotations.findAnnotations(targetClass, DenyAll.class).isEmpty()) {
                            throw new ForbiddenException("DenyAll on " + targetClass.getName());
                        }
                        List<RolesAllowed> rolesAllowedList = Annotations.findAnnotations(targetClass, RolesAllowed.class);
                        if(!rolesAllowedList.isEmpty()) {
                            for (RolesAllowed rolesAllowed : rolesAllowedList) {
                                for (String role : rolesAllowed.value()) {
                                    if(securityContext.get().isUserInRole(role)) {
                                        return ctx.proceed();
                                    }
                                }
                            }
                            throw new ForbiddenException("User not in roles " + rolesAllowedList);
                        }
                        else {
                            return ctx.proceed();
                        }
                    }
                }
            }
            return ctx.proceed();

        } finally {
            if(didPush) {
                this.securityContext.get().setRoles(runAsStack.get().pop());
            }
        }
    }
}
