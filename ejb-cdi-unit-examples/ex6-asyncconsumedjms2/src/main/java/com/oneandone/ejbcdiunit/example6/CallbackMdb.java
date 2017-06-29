package com.oneandone.ejbcdiunit.example6;

import java.util.HashMap;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author aschoerk
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@MessageDriven(name = "CallbackMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/example6"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "destination='callback'")})
public class CallbackMdb implements MessageListener {

    @EJB(name = "Callback")
    AsynchronousServiceIntf.Callbacks callbacks;

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
                    callbacks.pushId(new AsynchronousServiceIntf.CorrelationId(mapMessage.getLong("correlationId")),
                            mapMessage.getLong("id"));
                    break;
                case "getStringValueFor":
                    callbacks.pushString(new AsynchronousServiceIntf.CorrelationId(mapMessage.getLong("correlationId")),
                            mapMessage.getString("stringFor"));
                    break;
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
