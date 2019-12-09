package com.oneandone.iocunit.resteasytester;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.resteasy.auth.TestAuth;
import com.oneandone.iocunit.resteasytester.resources.SecuredResourceDefinedByInterface;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;

@RunWith(IocUnitRunner.class)
@SutClasses({SecuredResourceDefinedByInterface.class})
public class SecureResourcesTest {

    @Test
    @TestAuth({"MayUseResourceIfNotOtherwiseDefinedByMethod"})
    public void testGreen() {
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
}
