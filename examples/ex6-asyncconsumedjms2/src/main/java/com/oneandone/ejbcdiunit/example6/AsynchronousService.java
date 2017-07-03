package com.oneandone.ejbcdiunit.example6;

import java.util.HashMap;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;

/**
 * Created by aschoerk on 28.06.17.
 */
@Stateless
public class AsynchronousService implements AsynchronousServiceIntf {

    @Inject
    private JmsDispatcher jmsDispatcher;

    private AsynchronousServiceIntf.CorrelationId postMessageToJms(HashMap<String, Object> message) {
        try {
            jmsDispatcher.send(message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return new AsynchronousServiceIntf.CorrelationId((Long)message.get("correlationId"));
    }

    @Override
    public CorrelationId newRemoteEntity1(int intValue, String stringValue) {
        final HashMap<String, Object> message = jmsDispatcher.initializeMessageForSender("newEntity1");
        message.put("intValue", intValue);
        message.put("stringValue", stringValue);
        return postMessageToJms(message);
    }

    @Override
    public CorrelationId getRemoteStringValueFor(long id) {
        final HashMap<String, Object> message = jmsDispatcher.initializeMessageForSender("getStringValueFor");
        message.put("id", id);
        return postMessageToJms(message);
    }


}
