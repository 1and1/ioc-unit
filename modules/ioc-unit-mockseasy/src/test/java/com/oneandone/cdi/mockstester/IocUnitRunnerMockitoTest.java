package com.oneandone.cdi.mockstester;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

@RunWith(IocUnitRunner.class)
@TestClasses({IocUnitRunnerMockitoTest.Resources.class})
public class IocUnitRunnerMockitoTest {

    public static class DummyClass {
        int return10() {
            return 10;
        }
    }

    public static class Resources {
        @Produces
        @Mock
        DummyClass dummyClass;
    }

    @Inject
    DummyClass dummyClass;


    @Test
    public void test() {
        Assert.assertEquals(0, dummyClass.return10());
    }
}
