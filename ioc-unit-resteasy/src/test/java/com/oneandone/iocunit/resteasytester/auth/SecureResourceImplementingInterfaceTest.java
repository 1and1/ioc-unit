package com.oneandone.iocunit.resteasytester.auth;

import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.resteasy.JaxRSClasses;
import com.oneandone.iocunit.resteasy.auth.TestAuth;
import com.oneandone.iocunit.resteasytester.auth.resources.SecuredResourceImplementingInterface;

@RunWith(IocUnitRunner.class)
@JaxRSClasses({SecuredResourceImplementingInterface.class})
public class SecureResourceImplementingInterfaceTest {

    @Test
    @TestAuth({"maycallmethod1"})
    public void canUseClassAnnotation() {
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath3/method1");
        given()
                .expect()
                .statusCode(403)
                .when()
                .get("/restpath3/method2");
    }

    @Test
    @TestAuth({"mayuseclass","maycallmethod2","maycallmethod1"})
    public void canTestMultipleRoles() {
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath3/method1");
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath3/method2");
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath3/method3");
    }

    @Test
    @TestAuth({"norole"})
    public void doesTestMultipleRoles() {
        given()
                .expect()
                .statusCode(403)
                .when()
                .get("/restpath3/method1");
        given()
                .expect()
                .statusCode(403)
                .when()
                .get("/restpath3/method2");
        given()
                .expect()
                .statusCode(403)
                .when()
                .get("/restpath3/method3");
    }
}
