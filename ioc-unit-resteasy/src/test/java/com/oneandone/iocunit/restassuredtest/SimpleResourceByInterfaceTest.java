package com.oneandone.iocunit.restassuredtest;

import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.restassuredtest.http.SimpleResourceByInterface;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({SimpleResourceByInterface.class})
public class SimpleResourceByInterfaceTest {

    @Test
    public void test() {
        // can't be found since @Path, @GET, ... not in Class-Definition to be found.
        given().get("/check").then().statusCode(404);
    }
}
