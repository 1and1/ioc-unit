package com.oneandone.iocunit.resteasytester.auth;

import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.resteasy.auth.TestAuth;
import com.oneandone.iocunit.resteasytester.auth.resources.SecuredResource;

@RunWith(IocUnitRunner.class)
@SutClasses({SecuredResource.class})
public class SecureResourcesTest {

    @Test
    @TestAuth({"MayUseResourceIfNotOtherwiseDefinedByMethod"})
    public void canUseClassAnnotation() {
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath2/method1");
        given()
                .expect()
                .statusCode(401)
                .when()
                .get("/restpath2/method2");
    }

    @Test
    @TestAuth({"MayUseResourceIfNotOtherwiseDefinedByMethod","maycallmethod2"})
    public void canTestMultipleRoles() {
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath2/method1");
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath2/method2");
        given()
                .expect()
                .statusCode(200)
                .when()
                .get("/restpath2/method3");
    }
}
