package com.oneandone.iocunit.ejb.resourcesimulators;

import javax.ejb.MessageDrivenContext;
import javax.enterprise.inject.Vetoed;

/**
 * @author aschoerk
 */
@Vetoed
public class MessageContextSimulation extends EjbContextSimulation implements MessageDrivenContext {

}
