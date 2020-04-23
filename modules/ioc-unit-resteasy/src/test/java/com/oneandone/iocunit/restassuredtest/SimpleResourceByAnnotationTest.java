package com.oneandone.iocunit.restassuredtest;

import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.restassuredtest.http.SimpleInterfaceResource;
import com.oneandone.iocunit.restassuredtest.http.SimpleResourceByInterface;
import com.oneandone.iocunit.resteasy.JaxRSClasses;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@JaxRSClasses(value = {SimpleResourceByInterface.class}, onlyDefinedByAnnotation = true)
@SutPackages(SimpleInterfaceResource.class)
public class SimpleResourceByAnnotationTest {

    @Test
    public void test() {
        given().get("/check").then().statusCode(200);
    }
}
