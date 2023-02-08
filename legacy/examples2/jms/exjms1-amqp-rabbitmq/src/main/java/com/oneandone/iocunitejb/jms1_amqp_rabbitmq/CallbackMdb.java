package com.oneandone.iocunitejb.jms1_amqp_rabbitmq;

import java.util.HashMap;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

/**
 * @author aschoerk
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@MessageDriven(name = "CallbackMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/callback"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        })
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
