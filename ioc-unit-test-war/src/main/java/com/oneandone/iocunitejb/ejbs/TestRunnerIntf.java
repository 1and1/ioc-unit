package com.oneandone.iocunitejb.ejbs;

import com.oneandone.iocunitejb.testbases.TestEntity1Saver;

/**
 * @author aschoerk
 */
public interface TestRunnerIntf {
    void runTestInRolledBackTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception;
    void runTestWithoutTransaction(TestEntity1Saver saver, int num, boolean exceptionExpected) throws Exception;
    void setUp() throws Exception;
}
