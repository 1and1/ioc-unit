package com.oneandone.ejbcdiunit.example5;

import java.util.HashMap;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class AsynchronousService implements AsynchronousServiceIntf {

    @Inject
    private JmsDispatcher jmsDispatcher;

    @Override
    public CorrelationId newRemoteEntity1(int intValue, String stringValue) {
        final HashMap<String, Object> message = jmsDispatcher.initializeMessage("newEntity1");
        message.put("intValue", intValue);
        message.put("stringValue", stringValue);
        return jmsDispatcher.postMessageToJms(message, jmsDispatcher.getIncomingQueue());
    }

    @Override
    public CorrelationId getRemoteStringValueFor(long id) {
        final HashMap<String, Object> message = jmsDispatcher.initializeMessage("getStringValueFor");
        message.put("id", id);
        return jmsDispatcher.postMessageToJms(message, jmsDispatcher.getIncomingQueue());
    }


}
