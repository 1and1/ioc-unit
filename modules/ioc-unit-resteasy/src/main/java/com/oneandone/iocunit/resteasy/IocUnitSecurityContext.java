package com.oneandone.iocunit.resteasy;

import com.oneandone.iocunit.resteasy.auth.TestAuth;

import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IocUnitSecurityContext implements SecurityContext {
    public static final String IOC_UNIT_SECURITY_CONTEXT_DEFAULT_USER = "IocUnitSecurityContextDefaultUser";
    String userName = IOC_UNIT_SECURITY_CONTEXT_DEFAULT_USER;
    List<String> roles = Collections.emptyList();
    String authenticationScheme = SecurityContext.BASIC_AUTH;
    boolean secure = true;

    public IocUnitSecurityContext() {
        TestAuth testAuth = RestEasyTestExtensionServices.testSecurityThreadLocal.get();
        if (testAuth != null) {
            userName = testAuth.user();
            roles = Arrays.asList(testAuth.value());
        }
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return userName;
            }
        };
    }

    @Override
    public boolean isUserInRole(String s) {
        if (userName.equals(IOC_UNIT_SECURITY_CONTEXT_DEFAULT_USER))
            return true;
        else
            return roles.contains(s);
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return authenticationScheme;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAuthenticationScheme(String authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setRoles(List<String> roles) {
        this.roles = Collections.unmodifiableList(roles);
    }
}
