package com.oneandone.iocunit.ejb.resourcesimulators;

import javax.ejb.MessageDrivenContext;

import com.oneandone.iocunit.ejb.ResourceQualifier;

/**
 * @author aschoerk
 */
@ResourceQualifier (name = "javax.ejb.MessageDrivenContext")
public class MessageContextSimulation extends EjbContextSimulation implements MessageDrivenContext {

}
