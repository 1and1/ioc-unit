package com.oneandone.iocunit.jtajpa;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;

import com.oneandone.cdi.discoveryrunner.WeldDiscoveryRunner;

/**
 * @author aschoerk
 */
@RunWith(WeldDiscoveryRunner.class)
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class TestAnnotationsWeldDiscovery extends TestAnnotationsBase {
}
