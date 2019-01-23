package com.oneandone.iocunit.basetests.mnotest;

import javax.enterprise.inject.Produces;

import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;

@RunWith(IocUnitRunner.class)
public abstract class TestBase {

    @Produces
    private Bean bean = new Bean();

}
