package com.oneandone.iocunitejb.example5;

import java.util.HashMap;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

/**
 * @author aschoerk
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@MessageDriven(name = "SenderMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/incoming"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        })
public class SenderMdb implements MessageListener {

    @EJB(name = "RemoteServiceIntf")
    RemoteServiceIntf remoteService;

    @Inject
    private JmsDispatcher jmsDispatcher;

    /**
     * Passes a message to the listener.
     *
     * @param message the message passed to the listener
     */
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage)message;
        try {
            HashMap<String, Object> result;
            switch (mapMessage.getString("operation")) {
                case "newEntity1":
                    final long l = remoteService.newEntity1(mapMessage.getInt("intValue"),
                        mapMessage.getString("stringValue"));
                    result = jmsDispatcher.initializeMessage(mapMessage);
                    result.put("id", l);
                    jmsDispatcher.send(result, jmsDispatcher.getCallbackQueue());
                    break;
                case "getStringValueFor":
                    final String stringFor = remoteService.getStringValueFor(mapMessage.getLong("id"));
                    result = jmsDispatcher.initializeMessage(mapMessage);
                    result.put("stringFor", stringFor);
                    jmsDispatcher.send(result, jmsDispatcher.getCallbackQueue());
                    break;
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
