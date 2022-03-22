package com.oneandone.iocunitejb.resource;

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.resource.ResourceQualifier;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class TestResources {

    static class Resources {
        @Resource(lookup = "mylookup1")
        int resource1;

        @Resource(lookup = "mylookup2")
        int resource2;
    }

    static class Producing {
        @Produces
        @ResourceQualifier(lookup = "mylookup1")
        int producingInt1() {
            return 1;
        }
        @Produces
        @ResourceQualifier(lookup = "mylookup2")
        int producingInt2() {
            return 2;
        }
    }

    @Inject
    Resources resource;

    @Test
    public void test() {
        assertTrue(resource.resource1 == 1);
        assertTrue(resource.resource2 == 2);
    }
}
