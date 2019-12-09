package com.oneandone.iocunit.resteasy.auth;

import com.oneandone.iocunit.InterceptorBase;
import com.oneandone.iocunit.resteasy.IocUnitSecurityContext;
import com.oneandone.iocunit.util.Annotations;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.NotAuthorizedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Interceptor
@RestEasyAuthorized
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class AuthInterceptor extends InterceptorBase {

    @Inject
    Instance<IocUnitSecurityContext> securityContext;  // manipulatable context (runAs)

    ThreadLocal<Stack<List<String>>> runAsStack = new ThreadLocal<>();

    @AroundInvoke
    public Object manageSecurity(InvocationContext ctx) throws Exception {
        boolean didPush = false;
        try {
            final Class<?> declaringClass = ctx.getMethod().getDeclaringClass();
            if (Annotations.findAnnotations(ctx.getMethod(), PermitAll.class).isEmpty()) {
                if (securityContext != null && securityContext.isResolvable()) {
                    boolean foundOnMethodLevel = false;
                    if (!Annotations.findAnnotations(ctx.getMethod(), DenyAll.class).isEmpty()) {
                        throw new NotAuthorizedException("DenyAll on " + ctx.getMethod().getName());
                    }
                    List<RunAs> runAs = Annotations.findAnnotations(declaringClass, RunAs.class);
                    if (!runAs.isEmpty()) {
                        if (runAs.size() != 1) {
                            throw new RuntimeException("Invalid multiple RunAs in " + declaringClass.getName());
                        }
                        String runAsRole = runAs.get(0).value();
                        if (!securityContext.get().isUserInRole(runAsRole)) {
                            throw new NotAuthorizedException("RunAs and user not in role " + runAsRole);
                        }
                        runAsStack.get().push(securityContext.get().getRoles());
                        didPush = true;
                        ArrayList<String> runAsRoleList = new ArrayList<>();
                        runAsRoleList.add(runAsRole);
                        securityContext.get().setRoles(runAsRoleList);
                    }
                    List<RolesAllowed> rolesAllowedList = Annotations.findAnnotations(ctx.getMethod(), RolesAllowed.class);
                    if (!rolesAllowedList.isEmpty()) {
                        for (RolesAllowed rolesAllowed : rolesAllowedList) {
                            for (String role : rolesAllowed.value()) {
                                if (securityContext.get().isUserInRole(role)) {
                                    return ctx.proceed();
                                }
                            }
                        }
                        throw new NotAuthorizedException("User not in roles " + rolesAllowedList);
                    } else {
                        Class<?> targetClass = getTargetClass(ctx);
                        if (!Annotations.findAnnotations(targetClass, PermitAll.class).isEmpty())
                            return ctx.proceed();
                        if (!Annotations.findAnnotations(targetClass, DenyAll.class).isEmpty())
                            throw new NotAuthorizedException("DenyAll on " + targetClass.getName());
                        rolesAllowedList = Annotations.findAnnotations(targetClass, RolesAllowed.class);
                        if (!rolesAllowedList.isEmpty()) {
                            for (RolesAllowed rolesAllowed : rolesAllowedList) {
                                for (String role : rolesAllowed.value()) {
                                    if (securityContext.get().isUserInRole(role)) {
                                        return ctx.proceed();
                                    }
                                }
                            }
                            throw new NotAuthorizedException("User not in roles " + rolesAllowedList);
                        } else {
                            return ctx.proceed();
                        }
                    }
                }
            }
            return ctx.proceed();

        } finally {
            if (didPush) {
                this.securityContext.get().setRoles(runAsStack.get().pop());
            }
        }
    }
}
