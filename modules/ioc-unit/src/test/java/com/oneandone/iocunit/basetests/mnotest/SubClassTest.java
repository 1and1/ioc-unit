package com.oneandone.iocunit.basetests.mnotest;

import jakarta.inject.Inject;

import org.junit.Test;

public class SubClassTest extends TestBase {

    @Inject
    private Bean bean;

    @Test
    public void success_MnoChangeService() throws Exception {
        bean.warn("test");
    }


}
