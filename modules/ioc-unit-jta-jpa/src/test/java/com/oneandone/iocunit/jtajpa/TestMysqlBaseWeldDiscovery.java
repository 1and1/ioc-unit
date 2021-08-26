package com.oneandone.iocunit.jtajpa;

import org.junit.runner.RunWith;

import com.oneandone.cdi.discoveryrunner.WeldDiscoveryRunner;
import com.oneandone.cdi.discoveryrunner.annotations.ExcludedClasses;

/**
 * @author aschoerk
 */
@RunWith(WeldDiscoveryRunner.class)
@ExcludedClasses({TestMysqlBaseIocUnitDiscovery.class})
public class TestMysqlBaseWeldDiscovery extends TestMysqlBase {
}
