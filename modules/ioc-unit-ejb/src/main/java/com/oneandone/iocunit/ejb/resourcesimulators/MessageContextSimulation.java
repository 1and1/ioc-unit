package com.oneandone.iocunit.ejb.resourcesimulators;

import jakarta.ejb.MessageDrivenContext;
import jakarta.enterprise.inject.Vetoed;

/**
 * @author aschoerk
 */
@Vetoed
public class MessageContextSimulation extends EjbContextSimulation implements MessageDrivenContext {

}
