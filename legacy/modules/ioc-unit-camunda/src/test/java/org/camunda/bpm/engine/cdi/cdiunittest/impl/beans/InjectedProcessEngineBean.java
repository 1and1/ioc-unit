package org.camunda.bpm.engine.cdi.cdiunittest.impl.beans;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.camunda.bpm.engine.ProcessEngine;

/**
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
@Named
public class InjectedProcessEngineBean {

    @Inject
    public ProcessEngine processEngine;
}
