package com.oneandone.iocunit.jtajpa;

import javax.transaction.Transactional;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.discoveryrunner.WeldDiscoveryExtension;

/**
 * @author aschoerk
 */
@ExtendWith(WeldDiscoveryExtension.class)
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class TestJtaJpaAnnotationsWeldDiscovery extends TestJtaJpaAnnotationsBase {
}
